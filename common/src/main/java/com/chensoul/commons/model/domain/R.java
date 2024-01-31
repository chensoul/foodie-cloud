package com.chensoul.commons.model.domain;

import com.chensoul.commons.constant.Constant;
import java.io.Serializable;
import lombok.Getter;

/**
 * Result Object
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
@Getter
public class R<T> implements Serializable {
	private static final long serialVersionUID = 6551531108468957025L;

	private final Integer code;
	private final String message;
	private final T data;

	private R(final Integer code, final String message, final T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static <T> R<T> ok() {
		return new R<>(0, "OK", null);
	}

	public static <T> R<T> ok(final T object) {
		return new R<>(0, "OK", object);
	}

	public static <T> R<T> error(final String message) {
		return new R<>(500, message, null);
	}


	public static <T> R<T> error() {
		return error(Constant.ERROR_MESSAGE);
	}

}
