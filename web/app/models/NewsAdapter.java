package models;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NewsAdapter {
	public Long id;
	
	public String title;
	
	public String body;
	
	public String type;
	
	public Date date;
	
	public List<String> categories;
	
	public List<String> previewUrl;
	
	public List<String> previewAlt;

	public String readMoreUrl;
	
	public boolean active = true;
	
	public String privateInfos;
	
	public NewsAdapter(News news) {
		this.id = news.id;
		this.title = news.title;
		this.body = news.body;
		this.type = news.type.name();
		this.date = news.date;
		this.readMoreUrl = news.readMoreUrl;
		this.active = news.active;
		this.privateInfos = news.privateInfos;
		this.categories = mapStrings(news.categories, NewsCategory::toString);
		this.previewUrl = mapStrings(news.previewUrl, NewsUrl::toString);
		this.previewAlt = mapStrings(news.previewAlt, NewsAlt::toString);
	}
	
	public static NewsAdapter of(News news) {
		return new NewsAdapter(news);
	}
	
	public static List<NewsAdapter> findAll() {
		return News.findAll().stream().map(NewsAdapter::of).collect(Collectors.toList());
	}
	
	private <T> List<String> mapStrings(List<T> values, Function<T, String> mapper) {
		return values.stream().map(mapper).collect(Collectors.toList());
	}
}
