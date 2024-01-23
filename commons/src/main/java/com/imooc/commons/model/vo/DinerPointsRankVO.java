package com.imooc.commons.model.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DinerPointsRankVO extends ShortDinerInfo {
	private static final long serialVersionUID = 3235514876665410346L;
	private int total;

	private int ranks;

	private int isMe;

}
