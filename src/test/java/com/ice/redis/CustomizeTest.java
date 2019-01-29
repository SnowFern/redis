package com.ice.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ice.redis.customize.CustomizeService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomizeTest {

	@Autowired
	private CustomizeService customizeService;

	/**
	 * 测试用例：用户发布文章
	 */
	@Test
	public void get() {
		String result = customizeService.set("ice", "fern");
		System.out.println(result);
		String value = customizeService.get("ice");
		System.out.println(value);
	}
}
