package com.imooc.commons.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 食客实体类
 */
@Data
public class Diner extends BaseEntity {
	private static final long serialVersionUID = 413520075761143357L;
	private Long id;
	private String username;
	private String nickname;
	@JsonIgnore
	private String password;
	private String phone;
	private String email;
	private String avatarUrl;
	private String roles;

}
