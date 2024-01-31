package com.chensoul.point.domain.model;

import com.chensoul.auth.domain.model.SimpleUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPointRankVO extends SimpleUser {
	private static final long serialVersionUID = 3235514876665410346L;
	private int total;

	private int rank;

	private int isMe;

}
