package com.imooc.commons.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imooc.commons.model.entity.Review;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewVO extends Review {
	private static final long serialVersionUID = -6854973884896957287L;
	private ShortDinerInfo dinerInfo;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private Date createDate;

}
