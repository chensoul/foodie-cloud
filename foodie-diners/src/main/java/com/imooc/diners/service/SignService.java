package com.imooc.diners.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.exception.ParameterException;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.vo.SignInDinerInfo;
import com.imooc.commons.utils.AssertUtil;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 签到业务逻辑层
 */
@Service
public class SignService {

	@Value("${service.name.foodie-oauth-server}")
	private String oauthServerName;
	@Resource
	private RestTemplate restTemplate;
	@Resource
	private RedisTemplate redisTemplate;

	/**
	 * 获取当月签到情况
	 *
	 * @param accessToken
	 * @param dateStr
	 * @return
	 */
	public Map<String, Boolean> getSignInfo(final String accessToken, final String dateStr) {
		// 获取登录用户信息
		final SignInDinerInfo dinerInfo = this.loadSignInDinerInfo(accessToken);
		// 获取日期
		final Date date = SignService.getDate(dateStr);
		// 构建 Key
		final String signKey = SignService.buildSignKey(dinerInfo.getId(), date);
		// 构建一个自动排序的 Map
		final Map<String, Boolean> signInfo = new TreeMap<>();
		// 获取某月的总天数（考虑闰年）
		final int dayOfMonth = DateUtil.lengthOfMonth(DateUtil.month(date) + 1,
			DateUtil.isLeapYear(DateUtil.year(date)));
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
			final LocalDateTime localDateTime = LocalDateTimeUtil.of(date).withDayOfMonth(i);
			final boolean flag = v >> 1 << 1 != v;
			signInfo.put(DateUtil.format(localDateTime, "yyyy-MM-dd"), flag);
			v >>= 1;
		}
		return signInfo;
	}

	/**
	 * 获取用户签到次数
	 *
	 * @param accessToken
	 * @param dateStr
	 * @return
	 */
	public long getSignCount(final String accessToken, final String dateStr) {
		// 获取登录用户信息
		final SignInDinerInfo dinerInfo = this.loadSignInDinerInfo(accessToken);
		// 获取日期
		final Date date = SignService.getDate(dateStr);
		// 构建 Key
		final String signKey = SignService.buildSignKey(dinerInfo.getId(), date);
		// e.g. BITCOUNT user:sign:5:202011
		return (Long) this.redisTemplate.execute(
			(RedisCallback<Long>) con -> con.bitCount(signKey.getBytes())
		);
	}

	/**
	 * 用户签到
	 *
	 * @param accessToken
	 * @param dateStr
	 * @return
	 */
	public int doSign(final String accessToken, final String dateStr) {
		// 获取登录用户信息
		final SignInDinerInfo dinerInfo = this.loadSignInDinerInfo(accessToken);
		// 获取日期
		final Date date = SignService.getDate(dateStr);
		// 获取日期对应的天数，多少号
		final int offset = DateUtil.dayOfMonth(date) - 1; // 从 0 开始
		// 构建 Key user:sign:5:yyyyMM
		final String signKey = SignService.buildSignKey(dinerInfo.getId(), date);
		// 查看是否已签到
		final boolean isSigned = this.redisTemplate.opsForValue().getBit(signKey, offset);
		AssertUtil.isTrue(isSigned, "当前日期已完成签到，无需再签");
		// 签到
		this.redisTemplate.opsForValue().setBit(signKey, offset, true);
		// 统计连续签到的次数
		final int count = this.getContinuousSignCount(dinerInfo.getId(), date);
		return count;
	}

	/**
	 * 统计连续签到的次数
	 *
	 * @param dinerId
	 * @param date
	 * @return
	 */
	private int getContinuousSignCount(final Integer dinerId, final Date date) {
		// 获取日期对应的天数，多少号，假设是 30
		final int dayOfMonth = DateUtil.dayOfMonth(date);
		// 构建 Key
		final String signKey = SignService.buildSignKey(dinerId, date);
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
				if (i != dayOfMonth) break;
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
	 * @param dinerId
	 * @param date
	 * @return
	 */
	private static String buildSignKey(final Integer dinerId, final Date date) {
		return String.format("user:sign:%d:%s", dinerId,
			DateUtil.format(date, "yyyyMM"));
	}

	/**
	 * 获取日期
	 *
	 * @param dateStr
	 * @return
	 */
	private static Date getDate(final String dateStr) {
		if (StrUtil.isBlank(dateStr)) {
			return new Date();
		}
		try {
			return DateUtil.parseDate(dateStr);
		} catch (final Exception e) {
			throw new ParameterException("请传入yyyy-MM-dd的日期格式");
		}
	}

	/**
	 * 获取登录用户信息
	 *
	 * @param accessToken
	 * @return
	 */
	private SignInDinerInfo loadSignInDinerInfo(final String accessToken) {
		// 必须登录
		AssertUtil.mustLogin(accessToken);
		final String url = this.oauthServerName + "user/me?access_token={accessToken}";
		final ResultInfo resultInfo = this.restTemplate.getForObject(url, ResultInfo.class, accessToken);
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new ParameterException(resultInfo.getCode(), resultInfo.getMessage());
		}
		final SignInDinerInfo dinerInfo = BeanUtil.fillBeanWithMap((LinkedHashMap) resultInfo.getData(),
			new SignInDinerInfo(), false);
		if (dinerInfo == null) {
			throw new ParameterException(ApiConstant.NO_LOGIN_CODE, ApiConstant.NO_LOGIN_MESSAGE);
		}
		return dinerInfo;
	}

}
