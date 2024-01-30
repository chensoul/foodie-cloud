package com.imooc.diner.vo;

import com.imooc.auth.model.SimpleUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NearMeUserVO extends SimpleUser {
	private static final long serialVersionUID = -9025802073924655381L;
	private String distance;

}
