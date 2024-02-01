package com.chensoul.foodie.domain.feed.entity;

import com.chensoul.core.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Feed extends BaseEntity {

	private static final long serialVersionUID = -4148182756606260330L;
	private String content;
	private Long userId;
	private int praiseAmount;
	private int commentAmount;
	private Long restaurantId;

}
