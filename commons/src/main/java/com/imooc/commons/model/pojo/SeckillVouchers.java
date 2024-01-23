package com.imooc.commons.model.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imooc.commons.model.base.BaseModel;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Setter
@Getter
public class SeckillVouchers extends BaseModel {

	private static final long serialVersionUID = -8368221217142979462L;
	private Integer fkVoucherId;
	private int amount;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date startTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date endTime;

}
