package com.imooc.follow.controller;

import com.imooc.commons.model.domain.R;
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
	@GetMapping("follower/{dinerId}")
	public R findFollower(@PathVariable final Long dinerId) {
		return R.ok(
			this.followService.findFollower(dinerId));
	}

	/**
	 * 共同关注列表
	 *
	 * @param dinerId
	 * @param access_token
	 * @return
	 */
	@GetMapping("common/{dinerId}")
	public R findCommonFriend(@PathVariable final Integer dinerId,
							  final String access_token) {
		return R.ok(this.followService.findCommonsFriend(dinerId, access_token));
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
	public R<Void> follow(@PathVariable final Long followDinerId,
						  @RequestParam final int isFollowed,
						  final String access_token) {
		this.followService.follow(followDinerId,
			isFollowed, access_token);
		return R.ok();
	}

}
