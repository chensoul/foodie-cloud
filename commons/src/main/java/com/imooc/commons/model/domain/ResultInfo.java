package com.imooc.commons.model.domain;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * 公共返回对象
 */
@Getter
@Setter
public class ResultInfo<T> implements Serializable {

	private static final long serialVersionUID = 2031964589881765148L;
	private Integer code;
	private String message;
	private T data;

}
