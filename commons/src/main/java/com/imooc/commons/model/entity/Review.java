package com.imooc.commons.model.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Review extends BaseEntity {
	private static final long serialVersionUID = -4974178162326082799L;
	private Long restaurantId;

	private String content;
	private Long dinerId;
	private int likeIt;

}
