package com.imooc.commons.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedVO implements Serializable {
	private static final long serialVersionUID = 4586475739686387629L;
	private Long id;
	private String content;
	private int praiseAmount;
	private int commentAmount;
	private Long restaurantId;
	private Long userId;
	private ShortUserInfo userInfo;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	public LocalDateTime createTime;

}
