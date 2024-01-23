package com.imooc.commons.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedsVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 4586475739686387629L;
	private Integer id;
	private String content;
	private int praiseAmount;
	private int commentAmount;
	private Integer fkRestaurantId;
	private Integer fkDinerId;
	private ShortDinerInfo dinerInfo;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	public Date createDate;

}
