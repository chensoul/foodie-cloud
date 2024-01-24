package com.imooc.order.handler;

import com.imooc.commons.model.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public static R handlerParameterException(final IllegalArgumentException ex) {
		log.error("参数异常", ex);

		return R.error(ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public static R handlerException(final Exception ex) {
		log.error("未知异常", ex);
		return R.error();
	}

}
