package com.chensoul.feed.infrastructure.webmvc;

import com.chensoul.commons.model.domain.R;
import java.sql.SQLIntegrityConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Global Exception Handler
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * 异常处理方法
	 *
	 * @return
	 */
	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public static R<String> handle(final SQLIntegrityConstraintViolationException ex) {
		log.error(ex.getMessage());

		if (ex.getMessage().contains("Duplicate entry")) {
			final String[] split = ex.getMessage().split(" ");
			final String msg = split[2] + "已存在";
			return R.error(msg);
		}

		return R.error("未知错误");
	}

	/**
	 * 异常处理方法
	 *
	 * @return
	 */
	@ExceptionHandler({IllegalArgumentException.class})
	public static R<String> handle(final IllegalArgumentException ex) {
		log.error("参数异常", ex);

		return R.error(ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public static R handlerException(final Exception ex) {
		log.error("系统异常", ex);
		return R.error("系统异常");
	}
}
