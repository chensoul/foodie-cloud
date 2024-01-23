package com.imooc.commons.model.pojo;

import com.imooc.commons.model.base.BaseModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Restaurant extends BaseModel {

	private static final long serialVersionUID = 1314367383400988586L;
	private String name;
	private String cnName;
	private Float x;
	private Float y;
	private String location;
	private String cnLocation;
	private String area;
	private String telephone;
	private String email;
	private String website;
	//    @ApiModelProperty("菜系")
	private String cuisine;
	//    @ApiModelProperty("均价，不显示具体金额")
	private String averagePrice;
	private String introduction;
	private String thumbnail;
	private int likeVotes;
	private int dislikeVotes;
	private Integer cityId;

}
