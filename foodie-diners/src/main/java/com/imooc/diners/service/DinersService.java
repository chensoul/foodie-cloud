package com.imooc.diners.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.dto.DinersDTO;
import com.imooc.commons.model.pojo.Diners;
import com.imooc.commons.model.vo.ShortDinerInfo;
import com.imooc.commons.utils.AssertUtil;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.diners.config.OAuth2ClientConfiguration;
import com.imooc.diners.domain.OAuthDinerInfo;
import com.imooc.diners.mapper.DinersMapper;
import com.imooc.diners.vo.LoginDinerInfo;
import java.util.LinkedHashMap;
import java.util.List;
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
	@Resource
	private DinersMapper dinersMapper;
	@Resource
	private SendVerifyCodeService sendVerifyCodeService;

	/**
	 * 根据 ids 查询食客信息
	 *
	 * @param ids 主键 id，多个以逗号分隔，逗号之间不用空格
	 * @return
	 */
	public List<ShortDinerInfo> findByIds(final String ids) {
		AssertUtil.isNotEmpty(ids);
		final String[] idArr = ids.split(",");
		final List<ShortDinerInfo> dinerInfos = this.dinersMapper.findByIds(idArr);
		return dinerInfos;
	}

	/**
	 * 用户注册
	 *
	 * @param dinersDTO
	 * @return
	 */
	public ResultInfo register(final DinersDTO dinersDTO) {
		// 参数非空校验
		final String username = dinersDTO.getUsername();
		AssertUtil.isNotEmpty(username, "请输入用户名");
		final String password = dinersDTO.getPassword();
		AssertUtil.isNotEmpty(password, "请输入密码");
		final String phone = dinersDTO.getPhone();
		AssertUtil.isNotEmpty(phone, "请输入手机号");
		final String verifyCode = dinersDTO.getVerifyCode();
		AssertUtil.isNotEmpty(verifyCode, "请输入验证码");
		// 获取验证码
		final String code = this.sendVerifyCodeService.getCodeByPhone(phone);
		// 验证是否过期
		AssertUtil.isNotEmpty(code, "验证码已过期，请重新发送");
		// 验证码一致性校验
		AssertUtil.isTrue(!dinersDTO.getVerifyCode().equals(code), "验证码不一致，请重新输入");
		// 验证用户名是否已注册
		final Diners diners = this.dinersMapper.selectByUsername(username.trim());
		AssertUtil.isTrue(diners != null, "用户名已存在，请重新输入");
		// 注册
		// 密码加密
		dinersDTO.setPassword(DigestUtil.md5Hex(password.trim()));
		this.dinersMapper.save(dinersDTO);
		// 自动登录
		return this.signIn(username.trim(), password.trim());
	}

	/**
	 * 校验手机号是否已注册
	 */
	public void checkPhoneIsRegistered(final String phone) {
		AssertUtil.isNotEmpty(phone, "手机号不能为空");
		final Diners diners = this.dinersMapper.selectByPhone(phone);
		AssertUtil.isTrue(diners == null, "该手机号未注册");
		AssertUtil.isTrue(diners.getIsValid() == 0, "该用户已锁定，请先解锁");
	}

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
		body.setAll(BeanUtil.beanToMap(this.oAuth2ClientConfiguration));
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
			// 登录失败
			resultInfo.setData(resultInfo.getMessage());
			return resultInfo;
		}
		// 这里的 Data 是一个 LinkedHashMap 转成了域对象 OAuthDinerInfo
		final OAuthDinerInfo dinerInfo = BeanUtil.fillBeanWithMap((LinkedHashMap) resultInfo.getData(),
			new OAuthDinerInfo(), false);
		// 根据业务需求返回视图对象
		final LoginDinerInfo loginDinerInfo = new LoginDinerInfo();
		loginDinerInfo.setToken(dinerInfo.getAccessToken());
		loginDinerInfo.setAvatarUrl(dinerInfo.getAvatarUrl());
		loginDinerInfo.setNickname(dinerInfo.getNickname());
		return ResultInfoUtil.buildSuccess(loginDinerInfo);
	}

}
