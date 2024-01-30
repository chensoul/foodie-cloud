package com.imooc.auth.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imooc.commons.model.entity.BaseEntity;
import lombok.Data;

/**
 * 食客实体类
 */
@Data
public class User extends BaseEntity {
	private static final long serialVersionUID = 413520075761143357L;
	private String username;
	private String nickname;
	@JsonIgnore
	private String password;
	private String phone;
	private String email;
	private String avatar;
	private String roles;
}
