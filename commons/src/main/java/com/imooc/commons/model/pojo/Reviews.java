package com.imooc.commons.model.pojo;

import com.imooc.commons.model.base.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Reviews extends BaseModel {

	private static final long serialVersionUID = -4974178162326082799L;
	private Integer fkRestaurantId;

	private String content;
	private Integer fkDinerId;
	private int likeIt;

}
