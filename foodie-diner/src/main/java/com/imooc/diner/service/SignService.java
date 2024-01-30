package com.imooc.diner.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * 签到业务逻辑层
 */
@Service
@AllArgsConstructor
public class SignService {
	private RedisTemplate redisTemplate;

	/**
	 * 获取当月签到情况
	 *
	 * @param date
	 * @return
	 */
	public Map<String, Boolean> getSignInfo(final LocalDateTime date) {
		final String signKey = SignService.buildSignKey(SecurityContextHolder.getContext().getAuthentication().getName(), date);
		final Map<String, Boolean> signInfo = new TreeMap<>();
		final int dayOfMonth = date.getMonth().length(date.toLocalDate().isLeapYear());
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
	public int doSign(LocalDateTime date) {
		if (date == null) {
			date = LocalDateTime.now();
		}

		final String username = SecurityContextHolder.getContext().getAuthentication().getName();
		final String signKey = SignService.buildSignKey(username, date);

		final int offset = date.getDayOfMonth(); // 从 0 开始
		final boolean isSigned = this.redisTemplate.opsForValue().getBit(signKey, offset);
		Assert.isTrue(!isSigned, "当前日期已完成签到，无需再签");

		this.redisTemplate.opsForValue().setBit(signKey, offset, true);

		final int count = this.getContinuousSignCount(username, date);
		final int point = SignService.addPoint(count, username);
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
		final int dayOfMonth = 30;
		final String signKey = SignService.buildSignKey(username, date);
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
	 * 构建 Key -- diner:sign:5:yyyyMM
	 *
	 * @param username
	 * @param date
	 * @return
	 */
	private static String buildSignKey(final String username, final LocalDateTime date) {
		return String.format("diner:sign:%s:%s", username, date.format(DateTimeFormatter.ofPattern("yyyyMM")));
	}


	/**
	 * 添加用户积分
	 *
	 * @param count    连续签到次数
	 * @param username 登录用户id
	 * @return 获取的积分
	 */
	private static int addPoint(final int count, final String username) {
		// 签到1天送10积分，连续签到2天送20积分，3天送30积分，4天以上均送50积分
		int point = 10;
		if (count == 2) {
			point = 20;
		} else if (count == 3) {
			point = 30;
		} else if (count >= 4) {
			point = 50;
		}
		//TODO 调用积分服务
		return point;
	}

}
