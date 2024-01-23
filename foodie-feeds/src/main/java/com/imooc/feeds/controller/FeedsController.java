package com.imooc.feeds.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.pojo.Feeds;
import com.imooc.commons.model.vo.FeedsVO;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.feeds.service.FeedsService;
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
	private FeedsService feedsService;

	/**
	 * 分页获取关注的 Feed 数据
	 *
	 * @param page
	 * @param access_token
	 * @return
	 */
	@GetMapping("{page}")
	public ResultInfo selectForPage(@PathVariable final Integer page, final String access_token) {
		final List<FeedsVO> feedsVOS = this.feedsService.selectForPage(page, access_token);
		return ResultInfoUtil.buildSuccess(feedsVOS);
	}

	/**
	 * 变更 Feed
	 *
	 * @return
	 */
	@PostMapping("updateFollowingFeeds/{followingDinerId}")
	public ResultInfo addFollowingFeeds(@PathVariable final Integer followingDinerId,
                                        final String access_token, @RequestParam final int type) {
        this.feedsService.addFollowingFeed(followingDinerId, access_token, type);
		return ResultInfoUtil.buildSuccess("操作成功");
	}

	/**
	 * 删除 Feed
	 *
	 * @param id
	 * @param access_token
	 * @return
	 */
	@DeleteMapping("{id}")
	public ResultInfo delete(@PathVariable final Integer id, final String access_token) {
        this.feedsService.delete(id, access_token);
		return ResultInfoUtil.buildSuccess("删除成功");
	}

	/**
	 * 添加 Feed
	 *
	 * @param feeds
	 * @param access_token
	 * @return
	 */
	@PostMapping
	public ResultInfo<String> create(@RequestBody final Feeds feeds, final String access_token) {
        this.feedsService.create(feeds, access_token);
		return ResultInfoUtil.buildSuccess("添加成功");
	}

}
