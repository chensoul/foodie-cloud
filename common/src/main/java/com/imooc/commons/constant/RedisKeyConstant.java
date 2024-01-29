package com.imooc.commons.constant;

import lombok.Getter;

@Getter
public enum RedisKeyConstant {
	verify_code("verify_code:", "验证码"),
	seckill_voucher("seckill_voucher:", "秒杀券的key"),
	lock_key("lockby:", "分布式锁的key"),
	following("following:", "关注集合Key"),
	follower("follower:", "粉丝集合key"),
	following_feed("following_feed:", "我关注的好友的FeedsKey"),
	point("user:point", "user用户的积分Key"),
	user_location("user:location", "user地理位置Key"),
	restaurant("restaurant:", "餐厅的Key"),
	restaurant_new_review("restaurant:new:reviews:", "餐厅评论Key"),
	;

	private final String key;
	private final String desc;

	RedisKeyConstant(final String key, final String desc) {
		this.key = key;
		this.desc = desc;
	}

}
