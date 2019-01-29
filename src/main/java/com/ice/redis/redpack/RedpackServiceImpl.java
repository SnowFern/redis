package com.ice.redis.redpack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

/**
 * 文章发布使用redis技术
 */
@Service
public class RedpackServiceImpl implements RedpackService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void genHongBao() {
		try {
			final CountDownLatch latch = new CountDownLatch(Constants.threadCount);
			Thread thread = null;
			for (int i = 0; i < Constants.threadCount; i++) {
				final int page = i;
				Random rand = new Random();
				thread = new Thread() {
					public void run() {
						// 每个线程要初始化多少个红包
						int per = Constants.honBaoCount / Constants.threadCount;
						JSONObject object = new JSONObject();
						for (int j = page * per; j < (page + 1) * per; j++) { // 从0开始，直到
							object.put("id", "rid_" + j); // 红包ID
							object.put("money", rand.nextInt(100)); // 红包金额
							// lpush key value===lpush hongBaoPoolKey {id:rid_1, money:23}
							redisTemplate.opsForList().leftPush("hongBaoPoolKey", object.toJSONString());
						}
						latch.countDown(); // 发枪器递减
					}
				};
				thread.start();
			}
			latch.await();
			System.out.println("红包初始化完毕");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getHongBao() {
		try {
			final CountDownLatch latch = new CountDownLatch(Constants.threadCount);// 20 //发枪器
			DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>(Constants.getHongBaoScript,
					String.class);
			for (int i = 0; i < Constants.threadCount; i++) { // 20线程
				Thread thread = new Thread() {
					public void run() {
						List<String> keyList = new ArrayList<String>();
						String userid = null;
						while (true) {
							// 模拟一个用户ID使用UUID XXXX
							userid = UUID.randomUUID().toString();
							// 抢红包 eval 可以直接调用我们LUA脚本里的业务代码 object ={rid_1:1, money:9, userid:001}
							keyList.clear();
							keyList.add(Constants.hongBaoPoolKey);
							keyList.add(Constants.hongBaoDetailListKey);
							keyList.add(Constants.userIdRecordKey);
							keyList.add(userid);
							String object = redisTemplate.execute(redisScript, keyList);
//							Object object = redisTemplate.execute(redisScript, StringSerializer.class, StringSerializer.class, keyList, args);
							if (null != object) {
								System.out.println("用户ID号：" + userid + " 抢到红包的详情是 " + object);
							} else {
								Long count = redisTemplate.opsForList().size(Constants.hongBaoPoolKey);
								if (count.equals(0l)) {
									break;
								}
							}
						}
						latch.countDown();
					}
				};
				thread.start();
			}
			latch.await();
			System.out.println("红包抢完了");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
