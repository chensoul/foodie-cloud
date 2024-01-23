package com.imooc.diners.service;

import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.AssertUtil;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.diners.config.OAuth2ClientConfiguration;
import com.imooc.diners.domain.OAuthDinerInfo;
import com.imooc.diners.vo.LoginDinerInfo;
import java.util.LinkedHashMap;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 食客服务业务逻辑层
 */
@Service
public class DinersService {

	@Resource
	private RestTemplate restTemplate;
	@Value("${service.name.foodie-oauth-server}")
	private String oauthServerName;
	@Resource
	private OAuth2ClientConfiguration oAuth2ClientConfiguration;

	/**
	 * 登录
	 *
	 * @param account  帐号：用户名或手机或邮箱
	 * @param password 密码
	 * @return
	 */
	public ResultInfo signIn(final String account, final String password) {
		// 参数校验
		AssertUtil.isNotEmpty(account, "请输入登录帐号");
		AssertUtil.isNotEmpty(password, "请输入登录密码");
		// 构建请求头
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		// 构建请求体（请求参数）
		final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("username", account);
		body.add("password", password);
		body.setAll(BeanUtils.beanToMap(this.oAuth2ClientConfiguration));
		final HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
		// 设置 Authorization
		this.restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(this.oAuth2ClientConfiguration.getClientId(),
			this.oAuth2ClientConfiguration.getSecret()));
		// 发送请求
		final ResponseEntity<ResultInfo> result = this.restTemplate.postForEntity(this.oauthServerName + "oauth/token", entity, ResultInfo.class);
		// 处理返回结果
		AssertUtil.isTrue(result.getStatusCode() != HttpStatus.OK, "登录失败");
		final ResultInfo resultInfo = result.getBody();
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			resultInfo.setData(resultInfo.getMessage());
			return resultInfo;
		}
		final OAuthDinerInfo dinerInfo = BeanUtils.mapToBean((LinkedHashMap) resultInfo.getData(), OAuthDinerInfo.class);

		final LoginDinerInfo loginDinerInfo = new LoginDinerInfo();
		loginDinerInfo.setToken(dinerInfo.getAccessToken());
		loginDinerInfo.setAvatarUrl(dinerInfo.getAvatarUrl());
		loginDinerInfo.setNickname(dinerInfo.getNickname());
		return ResultInfoUtil.buildSuccess(loginDinerInfo);
	}

}
