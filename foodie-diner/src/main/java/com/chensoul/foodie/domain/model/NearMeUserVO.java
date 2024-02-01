package com.chensoul.foodie.domain.model;

import com.chensoul.foodie.domain.user.model.SimpleUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NearMeUserVO extends SimpleUser {
	private static final long serialVersionUID = -9025802073924655381L;
	private String distance;

}
