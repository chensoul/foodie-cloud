package com.imooc.point.controller;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Lists;
import com.imooc.point.pointApplicationTests;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class DinerPointControllerTest extends pointApplicationTests {

	// 初始化 2W 条积分记录
	@Test
	void addpoint() throws Exception {
		final List<Map<Integer, Integer[]>> dinerInfos = Lists.newArrayList();
		for (int i = 1; i <= 2000; i++) {
			for (int j = 0; j < 10; j++) {
				super.mockMvc.perform(MockMvcRequestBuilders.post("/")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.param("dinerId", i + "")
					.param("point", RandomUtil.randomNumbers(2))
					.param("type", "0")
				).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
			}
		}
	}

}
