package com.imooc.user.service;

import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.constant.PointType;
import com.imooc.commons.model.domain.R;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 签到业务逻辑层
 */
@Service
public class SignService {

	@Value("${service.name.foodie-point}")
	private String pointServerName;
	@Resource
	private RestTemplate restTemplate;
	@Resource
	private RedisTemplate redisTemplate;

	/**
	 * 获取当月签到情况
	 *
	 * @param date
	 * @return
	 */
	public Map<String, Boolean> getSignInfo(final LocalDateTime date) {
		// 构建 Key
		final String signKey = SignService.buildSignKey(SecurityContextHolder.getContext().getAuthentication().getName(), date);
		// 构建一个自动排序的 Map
		final Map<String, Boolean> signInfo = new TreeMap<>();
		// 获取某月的总天数（考虑闰年）
		final int dayOfMonth = date.getMonth().length(date.toLocalDate().isLeapYear());
		// bitfield user:sign:5:202011 u30 0
		final BitFieldSubCommands bitFieldSubCommands = BitFieldSubCommands.create()
			.get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth))
			.valueAt(0);
		final List<Long> list = this.redisTemplate.opsForValue().bitField(signKey, bitFieldSubCommands);
		if (list == null || list.isEmpty()) {
			return signInfo;
		}
		long v = list.get(0) == null ? 0 : list.get(0);
		// 从低位到高位进行遍历，为 0 表示未签到，为 1 表示已签到
		for (int i = dayOfMonth; i > 0; i--) {
            /*
                签到：  yyyy-MM-01 true
                未签到：yyyy-MM-01 false
             */
			final boolean flag = v >> 1 << 1 != v;
			signInfo.put(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), flag);
			v >>= 1;
		}
		return signInfo;
	}

	/**
	 * 获取用户签到次数
	 *
	 * @param date
	 * @return
	 */
	public long getSignCount(final LocalDateTime date) {
		final String signKey = SignService.buildSignKey(SecurityContextHolder.getContext().getAuthentication().getName(), date);
		return (Long) this.redisTemplate.execute(
			(RedisCallback<Long>) con -> con.bitCount(signKey.getBytes())
		);
	}

	/**
	 * 用户签到
	 *
	 * @param date
	 * @return
	 */
	public int doSign(final LocalDateTime date) {
		// 获取日期
		final int offset = date.getDayOfMonth(); // 从 0 开始
		// 构建 Key user:sign:5:yyyyMM
		final String signKey = SignService.buildSignKey(SecurityContextHolder.getContext().getAuthentication().getName(), date);
		// 查看是否已签到
		final boolean isSigned = this.redisTemplate.opsForValue().getBit(signKey, offset);
		Assert.isTrue(isSigned, "当前日期已完成签到，无需再签");
		// 签到
		this.redisTemplate.opsForValue().setBit(signKey, offset, true);
		// 统计连续签到的次数
		final int count = this.getContinuousSignCount(SecurityContextHolder.getContext().getAuthentication().getName(), date);
		// 添加签到积分并返回
		final int point = this.addpoint(count, SecurityContextHolder.getContext().getAuthentication().getName());
		return point;
	}

	/**
	 * 统计连续签到的次数
	 *
	 * @param username
	 * @param date
	 * @return
	 */
	private int getContinuousSignCount(final String username, final LocalDateTime date) {
		// 获取日期对应的天数，多少号，假设是 30
		final int dayOfMonth = 30;
		// 构建 Key
		final String signKey = SignService.buildSignKey(username, date);
		// bitfield user:sgin:5:202011 u30 0
		final BitFieldSubCommands bitFieldSubCommands = BitFieldSubCommands.create()
			.get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth))
			.valueAt(0);
		final List<Long> list = this.redisTemplate.opsForValue().bitField(signKey, bitFieldSubCommands);
		if (list == null || list.isEmpty()) {
			return 0;
		}
		int signCount = 0;
		long v = list.get(0) == null ? 0 : list.get(0);
		for (int i = dayOfMonth; i > 0; i--) {// i 表示位移操作次数
			// 右移再左移，如果等于自己说明最低位是 0，表示未签到
			if (v >> 1 << 1 == v) {
				// 低位 0 且非当天说明连续签到中断了
				if (i != dayOfMonth) {
					break;
				}
			} else {
				signCount++;
			}
			// 右移一位并重新赋值，相当于把最低位丢弃一位
			v >>= 1;
		}
		return signCount;
	}

	/**
	 * 构建 Key -- user:sign:5:yyyyMM
	 *
	 * @param username
	 * @param date
	 * @return
	 */
	private static String buildSignKey(final String username, final LocalDateTime date) {
		return String.format("user:sign:%d:%s", username, date.format(DateTimeFormatter.ofPattern("yyyyMM")));
	}


	/**
	 * 添加用户积分
	 *
	 * @param count    连续签到次数
	 * @param username 登录用户id
	 * @return 获取的积分
	 */
	private int addpoint(final int count, final String username) {
		// 签到1天送10积分，连续签到2天送20积分，3天送30积分，4天以上均送50积分
		int point = 10;
		if (count == 2) {
			point = 20;
		} else if (count == 3) {
			point = 30;
		} else if (count >= 4) {
			point = 50;
		}
		// 调用积分接口添加积分
		// 构建请求头
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		// 构建请求体（请求参数）
		final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("username", username);
		body.add("point", point);
		body.add("type", PointType.sign.getType());
		final HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
		// 发送请求
		final ResponseEntity<R> result = this.restTemplate.postForEntity(this.pointServerName,
			entity, R.class);
		Assert.isTrue(result.getStatusCode() != HttpStatus.OK, "登录失败！");
		final R resultInfo = result.getBody();
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			// 失败了, 事物要进行回滚
			throw new IllegalArgumentException(resultInfo.getMessage());
		}
		return point;
	}

}
