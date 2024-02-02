package com.chensoul;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = {PointApplication.class})
@AutoConfigureMockMvc
public class PointApplicationTests {
	@Autowired
	protected MockMvc mockMvc;

}
