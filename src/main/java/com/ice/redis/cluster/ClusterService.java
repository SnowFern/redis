package com.ice.redis.cluster;

public interface ClusterService {

	void set(String key, Object value);

	String get(String key);
}
