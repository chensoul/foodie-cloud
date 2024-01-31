package com.chensoul.feed.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.chensoul.auth.model.SimpleUser;
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
	private SimpleUser userInfo;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	public LocalDateTime createTime;

}
