package com.chensoul.controller;

import com.chensoul.PointApplicationTests;
import org.junit.jupiter.api.Test;

public class PointControllerTest extends PointApplicationTests {

	// 初始化 2W 条积分记录
	@Test
	void addPoint() throws Exception {
		for (int i = 1; i <= 2000; i++)
			for (int j = 0; j < 10; j++) {
				//				super.mockMvc.perform(MockMvcRequestBuilders.post("/point")
//					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
//					.param("userId", i + "")
//					.param("score", RandomStringUtils.randomAlphanumeric(2))
//					.param("type", "0")
//				).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
			}
	}

}
