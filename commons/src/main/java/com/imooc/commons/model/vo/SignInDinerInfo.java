package com.imooc.commons.model.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInDinerInfo implements Serializable {
	private static final long serialVersionUID = 2970945705660154725L;
	private Integer id;
	private String username;
	private String nickname;
	private String phone;
	private String email;
	private String avatarUrl;
	private String roles;

}
