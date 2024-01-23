package com.imooc.commons.model.pojo;

import com.imooc.commons.model.base.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DinerPoints extends BaseModel {
	private static final long serialVersionUID = -3051862591232648062L;
	private Integer fkDinerId;
	private Integer points;
	//    @ApiModelProperty(name = "类型",example = "0=签到，1=关注好友，2=添加Feed，3=添加商户评论")
	private Integer types;

}
