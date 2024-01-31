package com.chensoul.commons.constant;

import lombok.Getter;

@Getter
public enum RedisKeyConstant {
	FOLLOWER("follower:", "粉丝集合key"),
	FOLLOWING("following:", "关注集合Key"),
	FOLLOWING_FEED("following_feed:", "我关注的好友的FeedsKey"),
	LOCK_KEY("lockby:", "分布式锁的key"),
	POINT("sign:point", "user用户的积分Key"),
	RESTAURANT("restaurant:", "餐厅的Key"),
	RESTAURANT_NEW_REVIEW("restaurant:new:reviews:", "餐厅评论Key"),
	SECKILL_VOUCHER("seckill_voucher:", "秒杀券的key"),
	USER_LOCATION("sign:location", "user地理位置Key"),
	VERIFY_CODE("verify_code:", "验证码"),
	;

	private final String key;
	private final String desc;

	RedisKeyConstant(final String key, final String desc) {
		this.key = key;
		this.desc = desc;
	}

}
