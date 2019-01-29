package com.ice.redis.article;

import java.util.List;
import java.util.Map;

public interface ArticleService {
	String postArticle(String title, String content, String link, String userId);

	Map<Object, Object> hgetAll(String key);

	void articleVote(String userId, String articleId);

	String hget(String key, String votes);

	List<Map<Object, Object>> getArticles(int page, String order);
}
