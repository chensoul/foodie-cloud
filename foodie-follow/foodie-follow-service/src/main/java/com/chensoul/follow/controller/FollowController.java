package com.chensoul.follow.controller;

import com.chensoul.commons.model.domain.R;
import com.chensoul.follow.service.FollowService;
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
	 * @param userId
	 * @return
	 */
	@GetMapping("follower/{userId}")
	public R findFollower(@PathVariable final Long userId) {
		return R.ok(
			this.followService.findFollower(userId));
	}

	/**
	 * 共同关注列表
	 *
	 * @param userId
	 * @return
	 */
	@GetMapping("common/{userId}")
	public R findCommonFriend(@PathVariable final Integer userId) {
		return R.ok(this.followService.findCommonsFriend(userId));
	}

	/**
	 * 关注/取关
	 *
	 * @param followUserId 关注的食客ID
	 * @param isFollowed   是否关注 1=关注 0=取消
	 * @return
	 */
	@PostMapping("/{followUserId}")
	public R<Void> follow(@PathVariable final Long followUserId,
						  @RequestParam final int isFollowed) {
		this.followService.follow(followUserId,
			isFollowed);
		return R.ok();
	}

}
