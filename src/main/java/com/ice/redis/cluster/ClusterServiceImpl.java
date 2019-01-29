package com.ice.redis.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClusterServiceImpl implements ClusterService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void set(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
	}

	@Override
	public String get(String key) {
		return redisTemplate.hasKey(key) ? redisTemplate.opsForValue().get(key).toString() : null;
	}
}
