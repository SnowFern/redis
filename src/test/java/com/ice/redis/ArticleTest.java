package com.ice.redis;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ice.redis.article.ArticleService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArticleTest {

	@Autowired
	private ArticleService articleService;

	/**
	 * 测试用例：用户发布文章
	 */
	@Test
	public void apostArticle() {
		String userId = "001"; // 用户ID 001
		String title = "The road to west";
		String content = "About body and mental health";
		String link = "www.miguo.com";
		// 发布文章，返回文章ID
		String articleId = articleService.postArticle(title, content, link, userId);
		System.out.println("刚发布了一篇文章，文章ID为: " + articleId);
		System.out.println("文章所有属性值内容如下:");
		Map<Object, Object> articleData = articleService.hgetAll("article:" + articleId);
		for (Map.Entry<Object, Object> entry : articleData.entrySet()) {
			System.out.println("  " + entry.getKey() + ": " + entry.getValue());
		}
		System.out.println();
	}

	/**
	 * 测试用例：用户对文章投票
	 */
	@Test
	public void barticleVote() {
		String userId = "004";
		String articleId = "1";
		System.out.println("开始对文章" + "article:" + articleId + "进行投票啦~~~~~");
		// cang用户给James的文章投票
		articleService.articleVote(userId, "article:" + articleId);// article:1
		// 投票完后，查询当前文章的投票数votes
		String votes = articleService.hget("article:" + articleId, "votes");

		System.out.println("article:" + articleId + "这篇文章的投票数从redis查出来结果为: " + votes);
	}
	
	/**
	* 测试用例：获取文章列表并打印出来
	*/
	@Test
	public void cgetArticles(){
		int page = 1;
		String key = "score:info";
        System.out.println("查询当前的文章列表集合为：");
        List<Map<Object, Object>> articles = articleService.getArticles(page,key);
        for (Map<Object, Object> article : articles){
            System.out.println("  id: " + article.get("id"));
            for (Map.Entry<Object, Object> entry : article.entrySet()){
                if (entry.getKey().equals("id")){
                    continue;
                }
                System.out.println("    " + entry.getKey() + ": " + entry.getValue());
            }
        }
	}
}
