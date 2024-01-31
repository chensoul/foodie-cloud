package com.chensoul.restaurant;

import com.chensoul.auth.model.SimpleUser;
import com.chensoul.commons.model.entity.Review;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewVO extends Review {
	private static final long serialVersionUID = -6854973884896957287L;
	private SimpleUser userInfo;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private Date createDate;

}
