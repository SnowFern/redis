package com.ice.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ice.redis.redpack.RedpackService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedpackTest {

	@Autowired
	private RedpackService redpackService;

	/**
	 * 测试用例：用户发布文章
	 */
	@Test
	public void testRedpack() {
		redpackService.genHongBao();
		redpackService.getHongBao();
	}
}
