package com.imooc.commons.model.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Follow extends BaseEntity {
	private static final long serialVersionUID = 2840550359254845479L;
	private int userId;
	private Integer followUserId;

}
