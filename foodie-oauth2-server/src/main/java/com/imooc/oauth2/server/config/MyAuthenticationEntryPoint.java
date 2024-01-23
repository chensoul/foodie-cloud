package com.imooc.oauth2.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.ResultInfoUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 认证失败处理
 */
@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Resource
	private ObjectMapper objectMapper;

	@Override
	public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException {
		// 返回 JSON
		response.setContentType("application/json;charset=utf-8");
		// 状态码 401
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		// 写出
		final PrintWriter out = response.getWriter();
		String errorMessage = authException.getMessage();
		if (StringUtils.isBlank(errorMessage)) errorMessage = "登录失效!";
		final ResultInfo result = ResultInfoUtil.buildError(ApiConstant.ERROR_CODE,
			errorMessage);
		out.write(this.objectMapper.writeValueAsString(result));
		out.flush();
		out.close();
	}

}
