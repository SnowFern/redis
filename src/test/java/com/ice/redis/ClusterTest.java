package com.ice.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ice.redis.cluster.ClusterService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClusterTest {

	@Autowired
	private ClusterService clusterService;

	/**
	 * 测试用例：用户发布文章
	 */
	@Test
	public void get() {
		String key = "fern";
		Integer value = 26;
		clusterService.set(key, value);
		System.out.println(clusterService.get(key));
	}
}
