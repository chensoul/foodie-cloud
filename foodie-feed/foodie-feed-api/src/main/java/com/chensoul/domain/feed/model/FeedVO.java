package com.chensoul.domain.feed.model;

import com.chensoul.domain.user.model.SimpleUser;
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

	public LocalDateTime createTime;

}
