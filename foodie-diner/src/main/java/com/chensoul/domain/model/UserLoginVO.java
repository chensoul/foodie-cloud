package com.chensoul.domain.model;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLoginVO implements Serializable {

	private static final long serialVersionUID = 1064585367837864481L;
	private String nickname;
	private String token;
	private String avatar;

}
