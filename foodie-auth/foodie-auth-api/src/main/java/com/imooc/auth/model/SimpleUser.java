package com.imooc.auth.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleUser implements Serializable {
	private static final long serialVersionUID = 6594806670302600745L;
	public Long id;
	private String nickname;
	private String avatar;

}
