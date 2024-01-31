package com.chensoul.point;

import lombok.AllArgsConstructor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AllArgsConstructor

public class PointApplicationTests {


	protected MockMvc mockMvc;

}
