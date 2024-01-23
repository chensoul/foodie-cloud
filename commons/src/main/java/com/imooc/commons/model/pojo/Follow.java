package com.imooc.commons.model.pojo;

import com.imooc.commons.model.base.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Follow extends BaseModel {
	private static final long serialVersionUID = 2840550359254845479L;
	private int dinerId;
	private Integer followDinerId;

}
