package com.chensoul.point.controller;

import com.chensoul.point.PointApplicationTests;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class PointControllerTest extends PointApplicationTests {

	// 初始化 2W 条积分记录
	@Test
	void addPoint() throws Exception {
		final List<Map<Integer, Integer[]>> userInfos = Lists.newArrayList();
		for (int i = 1; i <= 2000; i++) {
			for (int j = 0; j < 10; j++) {
				super.mockMvc.perform(MockMvcRequestBuilders.post("/")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.param("userId", i + "")
					.param("point", RandomStringUtils.randomAlphanumeric(2))
					.param("type", "0")
				).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
			}
		}
	}

}
