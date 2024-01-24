package com.imooc.diner.service;

import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.dto.DinerRequest;
import com.imooc.commons.model.entity.Diner;
import com.imooc.commons.model.vo.ShortDinerInfo;
import com.imooc.diner.config.OAuth2ClientConfiguration;
import com.imooc.diner.mapper.DinerMapper;
import com.imooc.diner.vo.DinerLoginVO;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 食客服务业务逻辑层
 */
@Service
public class DinerService {
	@Value("${service.name.foodie-oauth-server}")
	private String oauthServerName;

	@Resource
	private RestTemplate restTemplate;

	@Resource
	private OAuth2ClientConfiguration oAuth2ClientConfiguration;

	@Resource
	private DinerMapper dinerMapper;
	@Resource
	private SendVerifyCodeService sendVerifyCodeService;

	/**
	 * 校验手机号是否已注册
	 */
	public void checkPhoneIsRegistered(final String phone) {
		final Diner diners = this.dinerMapper.getByPhone(phone);
		Assert.isTrue(diners != null, "该手机号未注册");
	}

	/**
	 * 用户注册
	 *
	 * @param dinerRequest
	 * @return
	 */
	public R register(final DinerRequest dinerRequest) {
		final String username = dinerRequest.getUsername().trim();
		final String password = dinerRequest.getPassword().trim();
		final String phone = dinerRequest.getPhone();

		final String code = this.sendVerifyCodeService.getCodeByPhone(phone);

		Assert.hasLength(code, "验证码已过期，请重新发送");
		Assert.isTrue(dinerRequest.getVerifyCode().equals(code), "验证码不一致，请重新输入");

		final Diner diner = this.dinerMapper.getByUsername(username);
		Assert.isTrue(diner != null, "用户名已存在，请重新输入");

		dinerRequest.setPassword(new BCryptPasswordEncoder().encode(password));
		this.dinerMapper.save(dinerRequest);

		return this.signIn(username, password);
	}

	public R<DinerLoginVO> signIn(final String account, final String password) {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("username", account);
		body.add("password", password);

		final Map<String, Object> content = new LinkedHashMap<>();
		content.put("clientId", this.oAuth2ClientConfiguration.getClientId());
		content.put("String", this.oAuth2ClientConfiguration.getSecret());
		content.put("grant_type", this.oAuth2ClientConfiguration.getGrant_type());
		content.put("scope", this.oAuth2ClientConfiguration.getScope());
		body.setAll(content);

		final HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
		this.restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(this.oAuth2ClientConfiguration.getClientId(),
			this.oAuth2ClientConfiguration.getSecret()));
		final ResponseEntity<R> result = this.restTemplate.postForEntity(this.oauthServerName + "oauth/token", entity, R.class);
		final R r = result.getBody();
		if (result.getStatusCode() != HttpStatus.OK) {
			return r;
		}

		final LinkedHashMap data = (LinkedHashMap) r.getData();
		final DinerLoginVO dinerLoginVO = new DinerLoginVO()
			.setAvatarUrl((String) data.get("avatarUrl"))
			.setNickname(data.get("nickname").toString())
			.setToken(data.get("accessToken").toString());

		return R.ok(dinerLoginVO);
	}

	public List<ShortDinerInfo> findByIds(final String ids) {
		Assert.notNull(ids, "参数ids不能为空");
		final String[] idArr = ids.split(",");
		return this.dinerMapper.findByIds(idArr);
	}

}
