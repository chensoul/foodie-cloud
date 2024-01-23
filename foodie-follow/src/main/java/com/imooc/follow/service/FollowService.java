package com.imooc.follow.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.constant.RedisKeyConstant;
import com.imooc.commons.exception.ParameterException;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.pojo.Follow;
import com.imooc.commons.model.vo.ShortDinerInfo;
import com.imooc.commons.model.vo.SignInDinerInfo;
import com.imooc.commons.utils.AssertUtil;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.follow.mapper.FollowMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 关注/取关业务逻辑层
 */
@Service
public class FollowService {

    @Value("${service.name.foodie-oauth-server}")
    private String oauthServerName;
    @Value("${service.name.foodie-diners-server}")
    private String dinersServerName;
    @Value("${service.name.foodie-feeds-server}")
    private String feedsServerName;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private FollowMapper followMapper;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 获取粉丝列表
     *
     * @param dinerId
     * @return
     */
    public Set<Integer> findFollowers(final Integer dinerId) {
        AssertUtil.isNotNull(dinerId, "请选择要查看的用户");
        final Set<Integer> followers = this.redisTemplate.opsForSet()
                .members(RedisKeyConstant.followers.getKey() + dinerId);
        return followers;
    }

    /**
     * 共同关注列表
     *
     * @param dinerId
     * @param accessToken
     * @param path
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultInfo findCommonsFriends(final Integer dinerId, final String accessToken) {
        // 是否选择了查看对象
        AssertUtil.isTrue(dinerId == null || dinerId < 1,
                "请选择要查看的人");
        // 获取登录用户信息
        final SignInDinerInfo dinerInfo = this.loadSignInDinerInfo(accessToken);
        // 获取登录用户的关注信息
        final String loginDinerKey = RedisKeyConstant.following.getKey() + dinerInfo.getId();
        // 获取登录用户查看对象的关注信息
        final String dinerKey = RedisKeyConstant.following.getKey() + dinerId;
        // 计算交集
        final Set<Integer> dinerIds = this.redisTemplate.opsForSet().intersect(loginDinerKey, dinerKey);
        // 没有
        if (dinerIds == null || dinerIds.isEmpty()) {
            return ResultInfoUtil.buildSuccess(new ArrayList<ShortDinerInfo>());
        }
        // 调用食客服务根据 ids 查询食客信息
        final ResultInfo resultInfo = this.restTemplate.getForObject(this.dinersServerName + "findByIds?access_token={accessToken}&ids={ids}",
                ResultInfo.class, accessToken, StrUtil.join(",", dinerIds));
        if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
            return resultInfo;
        }
        // 处理结果集
        final List<LinkedHashMap> dinnerInfoMaps = (ArrayList) resultInfo.getData();
        final List<ShortDinerInfo> dinerInfos = dinnerInfoMaps.stream()
                .map(diner -> BeanUtil.fillBeanWithMap(diner, new ShortDinerInfo(), true))
                .collect(Collectors.toList());

        return ResultInfoUtil.buildSuccess(dinerInfos);
    }

    /**
     * 关注/取关
     *
     * @param followDinerId 关注的食客ID
     * @param isFoolowed    是否关注 1=关注 0=取关
     * @param accessToken   登录用户token
     * @return
     */
    public ResultInfo follow(final Integer followDinerId, final int isFoolowed,
                             final String accessToken) {
        // 是否选择了关注对象
        AssertUtil.isTrue(followDinerId == null || followDinerId < 1,
                "请选择要关注的人");
        // 获取登录用户信息 (封装方法)
        final SignInDinerInfo dinerInfo = this.loadSignInDinerInfo(accessToken);
        // 获取当前登录用户与需要关注用户的关注信息
        final Follow follow = this.followMapper.selectFollow(dinerInfo.getId(), followDinerId);

        // 如果没有关注信息，且要进行关注操作 -- 添加关注
        if (follow == null && isFoolowed == 1) {
            // 添加关注信息
            final int count = this.followMapper.save(dinerInfo.getId(), followDinerId);
            // 添加关注列表到 Redis
            if (count == 1) {
                this.addToRedisSet(dinerInfo.getId(), followDinerId);
                // 保存 Feed
                this.sendSaveOrRemoveFeed(followDinerId, accessToken, 1);
            }
            return ResultInfoUtil.build(ApiConstant.SUCCESS_CODE,
                    "关注成功", "关注成功");
        }

        // 如果有关注信息，且目前处于关注状态，且要进行取关操作 -- 取关关注
        if (follow != null && follow.getIsValid() == 1 && isFoolowed == 0) {
            // 取关
            final int count = this.followMapper.update(follow.getId(), isFoolowed);
            // 移除 Redis 关注列表
            if (count == 1) {
                this.removeFromRedisSet(dinerInfo.getId(), followDinerId);
                // 移除 Feed
                this.sendSaveOrRemoveFeed(followDinerId, accessToken, 0);
            }
            return ResultInfoUtil.build(ApiConstant.SUCCESS_CODE,
                    "成功取关", "成功取关");
        }

        // 如果有关注信息，且目前处于取关状态，且要进行关注操作 -- 重新关注
        if (follow != null && follow.getIsValid() == 0 && isFoolowed == 1) {
            // 重新关注
            final int count = this.followMapper.update(follow.getId(), isFoolowed);
            // 添加关注列表到 Redis
            if (count == 1) {
                this.addToRedisSet(dinerInfo.getId(), followDinerId);
                // 添加 Feed
                this.sendSaveOrRemoveFeed(followDinerId, accessToken, 1);
            }
            return ResultInfoUtil.build(ApiConstant.SUCCESS_CODE,
                    "关注成功", "关注成功");
        }

        return ResultInfoUtil.buildSuccess("操作成功");
    }

    /**
     * 发送请求添加或者移除关注人的Feed列表
     *
     * @param followDinerId 关注好友的ID
     * @param accessToken   当前登录用户token
     * @param type          0=取关 1=关注
     */
    private void sendSaveOrRemoveFeed(final Integer followDinerId, final String accessToken, final int type) {
        final String feedsUpdateUrl = this.feedsServerName + "updateFollowingFeeds/"
                                      + followDinerId + "?access_token=" + accessToken;
        // 构建请求头
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 构建请求体（请求参数）
        final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("type", type);
        final HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        this.restTemplate.postForEntity(feedsUpdateUrl, entity, ResultInfo.class);
    }

    /**
     * 添加关注列表到 Redis
     *
     * @param dinerId
     * @param followDinerId
     */
    private void addToRedisSet(final Integer dinerId, final Integer followDinerId) {
        this.redisTemplate.opsForSet().add(RedisKeyConstant.following.getKey() + dinerId, followDinerId);
        this.redisTemplate.opsForSet().add(RedisKeyConstant.followers.getKey() + followDinerId, dinerId);
    }

    /**
     * 移除 Redis 关注列表
     *
     * @param dinerId
     * @param followDinerId
     */
    private void removeFromRedisSet(final Integer dinerId, final Integer followDinerId) {
        this.redisTemplate.opsForSet().remove(RedisKeyConstant.following.getKey() + dinerId, followDinerId);
        this.redisTemplate.opsForSet().remove(RedisKeyConstant.followers.getKey() + followDinerId, dinerId);
    }

    /**
     * 获取登录用户信息
     *
     * @param accessToken
     * @return
     */
    private SignInDinerInfo loadSignInDinerInfo(final String accessToken) {
        // 必须登录
        AssertUtil.mustLogin(accessToken);
        final String url = this.oauthServerName + "user/me?access_token={accessToken}";
        final ResultInfo resultInfo = this.restTemplate.getForObject(url, ResultInfo.class, accessToken);
        if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
            throw new ParameterException(resultInfo.getCode(), resultInfo.getMessage());
        }
        final SignInDinerInfo dinerInfo = BeanUtil.fillBeanWithMap((LinkedHashMap) resultInfo.getData(),
                new SignInDinerInfo(), false);
        if (dinerInfo == null) {
            throw new ParameterException(ApiConstant.NO_LOGIN_CODE, ApiConstant.NO_LOGIN_MESSAGE);
        }
        return dinerInfo;
    }

}
