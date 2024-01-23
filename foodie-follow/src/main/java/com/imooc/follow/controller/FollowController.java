package com.imooc.follow.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.follow.service.FollowService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 关注/取关控制层
 */
@RestController
public class FollowController {

    @Resource
    private FollowService followService;

    /**
     * 获取粉丝列表
     *
     * @param dinerId
     * @return
     */
    @GetMapping("followers/{dinerId}")
    public ResultInfo findFollowers(@PathVariable final Integer dinerId) {
        return ResultInfoUtil.buildSuccess(
                this.followService.findFollowers(dinerId));
    }

    /**
     * 共同关注列表
     *
     * @param dinerId
     * @param access_token
     * @return
     */
    @GetMapping("commons/{dinerId}")
    public ResultInfo findCommonsFriends(@PathVariable final Integer dinerId,
                                         final String access_token) {
        return this.followService.findCommonsFriends(dinerId, access_token);
    }

    /**
     * 关注/取关
     *
     * @param followDinerId 关注的食客ID
     * @param isFollowed    是否关注 1=关注 0=取消
     * @param access_token  登录用户token
     * @return
     */
    @PostMapping("/{followDinerId}")
    public ResultInfo follow(@PathVariable final Integer followDinerId,
                             @RequestParam final int isFollowed,
                             final String access_token) {
        final ResultInfo resultInfo = this.followService.follow(followDinerId,
                isFollowed, access_token);
        return resultInfo;
    }

}
