package com.ice.redis.article;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 文章发布使用redis技术
 */
@Service
public class ArticleServiceImpl implements ArticleService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * 文章提交发布
	 * 
	 * @param 标题 内容 链接 用户ID
	 * @return 文章的ID
	 */
	public String postArticle(String title, String content, String link, String userId) {
		// 基于redis: key--->唯一标识－－－实体－－－属性值
		// mysql: 主键：自增

		// 文章ID，自增 UUID 主键 1－－－id 主键
//		String articleId = String.valueOf(jedis.incr("article:")); // articleId=1
		String articleId = String.valueOf(redisTemplate.opsForValue().increment("article:", 1));

		// 用来记录投票 key voted:1 set key value set
		String voted = "voted:" + articleId; // voted:1

		// 将投票的用户记录到voted:1键集合来……001
		redisTemplate.opsForSet().add(voted, userId);

		// 设置失效时间
		redisTemplate.expire(voted, 5, TimeUnit.MINUTES);

		// 删数据之前,是不是要转移一下
		long now = System.currentTimeMillis() / 1000; // 时间
		// long score = 0l;
		// 生成文章ID 二维数据 hash
		String article = "article:" + articleId;// article:1
		// article:1
		HashMap<String, Object> articleData = new HashMap<String, Object>();
		articleData.put("title", title);
		articleData.put("link", link);
		articleData.put("user", userId);
		articleData.put("now", now);
		articleData.put("votes", 1);
		// 发布一篇文章，hmset article:1 title 标题 link googlecom user username
		redisTemplate.opsForHash().putAll(article, articleData);
		// zadd user:zan 200 james james的点赞数1, 返回操作成功的条数1
		redisTemplate.opsForZSet().add("score:info", article, now + 400);

		// 根据文章发布时间排序文章的有序集合
		redisTemplate.opsForZSet().add("time:", article, now);
		return articleId;
	}

	/**
	 * 文章投票
	 * 
	 * @param 用户ID 文章ID（article:001） //001
	 */
	public void articleVote(String userId, String article) {

		// 计算投票截止时间
		long cutoff = (System.currentTimeMillis() / 1000) - Constants.ONE_WEEK_IN_SECONDS;

		// 检查是否还可以对文章进行投票,如果该文章的发布时间比截止时间小，则已过期，不能进行投票
		if (redisTemplate.opsForZSet().score("time:", article) < cutoff) {
			return;
		}
		// 获取文章主键id
		String articleId = article.substring(article.indexOf(':') + 1); //// article:1 1

		// 将投票的用户加入到键为voted:1的集合中，表示该用户已投过票了 voted:1 set集合里来
		// 0 并不1
		if (redisTemplate.opsForSet().add("voted:" + articleId, userId) == 1) {
			// 分值加400
			redisTemplate.opsForZSet().incrementScore("score:info", article, Constants.VOTE_SCORE);

			// 投票数加1
			redisTemplate.opsForHash().increment(article, "votes", 1l);
		}
	}

	/**
	 * 文章列表查询（分页）
	 * 
	 * @param page key
	 * @return redis查询结果
	 */
	public List<Map<Object, Object>> getArticles(int page, String key) {
		int start = (page - 1) * Constants.ARTICLES_PER_PAGE;
		int end = start + Constants.ARTICLES_PER_PAGE - 1;
		// 倒序查询出投票数最高的文章，zset有序集合，分值递减
		Set<Object> ids = redisTemplate.opsForZSet().reverseRange(key, start, end);
		List<Map<Object, Object>> articles = new ArrayList<Map<Object, Object>>();
		for (Object id : ids) {
			Map<Object, Object> articleData = redisTemplate.opsForHash().entries(String.valueOf(id));
			articleData.put("id", id);
			articles.add(articleData);
		}

		return articles;
	}

	public String hget(String key, String feild) {
		return (String) redisTemplate.opsForHash().get(key, feild);
	}

	public Map<Object, Object> hgetAll(String key) {
		return redisTemplate.opsForHash().entries(key);
	}

}
