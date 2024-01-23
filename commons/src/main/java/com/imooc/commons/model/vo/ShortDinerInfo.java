package com.imooc.commons.model.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortDinerInfo implements Serializable {
	private static final long serialVersionUID = 6594806670302600745L;
	public Integer id;
	private String nickname;
	private String avatarUrl;

}
