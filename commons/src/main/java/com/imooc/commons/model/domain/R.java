package com.imooc.commons.model.domain;

import com.imooc.commons.constant.ApiConstant;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 公共返回对象
 */
@Getter
@Setter
@ToString
public class R<T> implements Serializable {

	private static final long serialVersionUID = 2031964589881765148L;
	private long code;
	private String message;
	private T data;

	public static <T> R<T> ok() {
		final R<T> r = build(ApiConstant.SUCCESS_CODE, ApiConstant.SUCCESS_MESSAGE, null);
		return r;
	}

	public static <T> R<T> ok(final T data) {
		final R<T> r = build(ApiConstant.SUCCESS_CODE, ApiConstant.SUCCESS_MESSAGE, data);
		return r;
	}

	public static <T> R<T> error(final long code, final String message) {
		final R<T> r = build(code, message, null);
		return r;
	}

	public static <T> R<T> error(final String message) {
		return error(ApiConstant.ERROR_CODE, message);
	}

	public static <T> R<T> error() {
		final R<T> r = build(ApiConstant.ERROR_CODE, ApiConstant.ERROR_MESSAGE, null);
		return r;
	}

	private static <T> R<T> build(final long code, String message, final T data) {
		if (message == null) {
			message = ApiConstant.SUCCESS_MESSAGE;
		}
		final R r = new R();
		r.setCode(code);
		r.setMessage(message);
		r.setData(data);
		return r;
	}

}
