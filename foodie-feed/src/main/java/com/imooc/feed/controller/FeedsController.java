package com.imooc.feed.controller;

import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.entity.Feed;
import com.imooc.feed.FeedVO;
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
	 * @return
	 */
	@GetMapping("{page}")
	public R selectForPage(@PathVariable final Integer page) {
		final List<FeedVO> feedVOS = this.feedService.selectForPage(page);
		return R.ok(feedVOS);
	}

	/**
	 * 变更 Feed
	 *
	 * @return
	 */
	@PostMapping("updateFollowingFeed/{followingUserId}")
	public R addFollowingFeed(@PathVariable final Long followingUserId, @RequestParam final int type) {
		this.feedService.addFollowingFeed(followingUserId, type);
		return R.ok();
	}

	/**
	 * 删除 Feed
	 *
	 * @param id
	 * @return
	 */
	@DeleteMapping("{id}")
	public R delete(@PathVariable final Long id) {
		this.feedService.delete(id);
		return R.ok();
	}

	/**
	 * 添加 Feed
	 *
	 * @param feed
	 * @return
	 */
	@PostMapping
	public R<String> create(@RequestBody final Feed feed) {
		this.feedService.create(feed);
		return R.ok("添加成功");
	}

}
