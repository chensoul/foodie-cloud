package com.chensoul.commons.constant;

import lombok.Getter;

/**
 * 积分类型
 */
@Getter
public enum PointType {

	SIGN(0),
	FOLLOW(1),
	FEED(2),
	REVIEW(3);

	private final int type;

	PointType(final int key) {
		type = key;
	}

}
