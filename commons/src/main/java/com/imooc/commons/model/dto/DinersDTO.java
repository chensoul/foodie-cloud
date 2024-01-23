package com.imooc.commons.model.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DinersDTO implements Serializable {
	private static final long serialVersionUID = -8506278990851485543L;
	private String username;
	private String password;
	private String phone;
	private String verifyCode;

}
