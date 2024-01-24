package com.imooc.commons.constant;

import lombok.Getter;

/**
 * 积分类型
 */
@Getter
public enum PointType {

	sign(0),
	follow(1),
	feed(2),
	review(3);

	private final int type;

	PointType(final int key) {
		this.type = key;
	}

}
