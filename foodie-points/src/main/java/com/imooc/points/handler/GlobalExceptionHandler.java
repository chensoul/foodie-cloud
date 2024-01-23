package com.imooc.points.handler;

import com.imooc.commons.exception.ParameterException;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.ResultInfoUtil;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 将输出的内容写入 ResponseBody 中
@Slf4j
public class GlobalExceptionHandler {

	@Resource
	private HttpServletRequest request;

	@ExceptionHandler(ParameterException.class)
	public ResultInfo<Map<String, String>> handlerParameterException(final ParameterException ex) {
		final String path = this.request.getRequestURI();
		final ResultInfo<Map<String, String>> resultInfo =
			ResultInfoUtil.buildError(ex.getErrorCode(), ex.getMessage());
		return resultInfo;
	}

	@ExceptionHandler(Exception.class)
	public ResultInfo<Map<String, String>> handlerException(final Exception ex) {
		log.info("未知异常：{}", ex);
		final String path = this.request.getRequestURI();
		final ResultInfo<Map<String, String>> resultInfo =
			ResultInfoUtil.buildError();
		return resultInfo;
	}

}
