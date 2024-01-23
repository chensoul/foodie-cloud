package com.imooc.commons.model.pojo;

import com.imooc.commons.model.base.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Feeds extends BaseModel {

	private static final long serialVersionUID = -4148182756606260330L;
	private String content;
	private Integer fkDinerId;
	private int praiseAmount;
	private int commentAmount;
	private Integer fkRestaurantId;

}
