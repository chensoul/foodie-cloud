package com.chensoul.foodie.domain.restaurant.model;

import com.chensoul.foodie.domain.restaurant.entity.Review;
import com.chensoul.foodie.domain.user.model.SimpleUser;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewVO extends Review {
	private static final long serialVersionUID = -6854973884896957287L;
	private SimpleUser userInfo;


	private LocalDateTime createDate;

}
