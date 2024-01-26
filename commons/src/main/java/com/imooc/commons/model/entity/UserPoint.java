package com.imooc.commons.model.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPoint extends BaseEntity {
	private static final long serialVersionUID = -3051862591232648062L;
	private Long dinerId;
	private Integer point;
	//    @ApiModelProperty(name = "类型",example = "0=签到，1=关注好友，2=添加Feed，3=添加商户评论")
	private Integer type;

}
