package com.imooc.diner.domain;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthDinerInfo implements Serializable {

	private static final long serialVersionUID = -34009354168886204L;
	private String nickname;
	private String avatarUrl;
	private String accessToken;
	private Integer expireIn;
	private List<String> scopes;
	private String refreshToken;

}
