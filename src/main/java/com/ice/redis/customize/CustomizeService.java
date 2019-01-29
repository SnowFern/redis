package com.ice.redis.customize;

public interface CustomizeService {

	String set(String key, String value);

	String get(String key);
}
