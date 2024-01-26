package com.imooc.commons.model.dto;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAddRequest implements Serializable {
	private static final long serialVersionUID = -8506278990851485543L;

	@NotBlank(message = "请输入用户名")
	private String username;

	@NotBlank(message = "请输入密码")
	private String password;

	@NotBlank(message = "请输入手机号")
	private String phone;

	@NotBlank(message = "请输入验证码")
	private String verifyCode;

}
