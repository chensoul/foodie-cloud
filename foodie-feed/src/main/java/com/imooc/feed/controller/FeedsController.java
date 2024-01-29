package com.imooc.feed.controller;

import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.entity.Feed;
import com.imooc.commons.model.vo.FeedVO;
import com.imooc.feed.service.FeedService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeedsController {

	@Resource
	private FeedService feedService;

	/**
	 * 分页获取关注的 Feed 数据
	 *
	 * @param page
	 * @param access_token
	 * @return
	 */
	@GetMapping("{page}")
	public R selectForPage(@PathVariable final Integer page, final String access_token) {
		final List<FeedVO> feedVOS = this.feedService.selectForPage(page, access_token);
		return R.ok(feedVOS);
	}

	/**
	 * 变更 Feed
	 *
	 * @return
	 */
	@PostMapping("updateFollowingFeed/{followingUserId}")
	public R addFollowingFeed(@PathVariable final Long followingUserId,
							  final String access_token, @RequestParam final int type) {
		this.feedService.addFollowingFeed(followingUserId, access_token, type);
		return R.ok();
	}

	/**
	 * 删除 Feed
	 *
	 * @param id
	 * @param access_token
	 * @return
	 */
	@DeleteMapping("{id}")
	public R delete(@PathVariable final Long id, final String access_token) {
		this.feedService.delete(id, access_token);
		return R.ok();
	}

	/**
	 * 添加 Feed
	 *
	 * @param feed
	 * @param access_token
	 * @return
	 */
	@PostMapping
	public R<String> create(@RequestBody final Feed feed, final String access_token) {
		this.feedService.create(feed, access_token);
		return R.ok("添加成功");
	}

}
