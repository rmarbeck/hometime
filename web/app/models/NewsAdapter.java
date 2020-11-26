package models;

import java.util.Date;
import java.util.List;

import fr.hometime.utils.NewsAdapterHelper;

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
		this.categories = NewsAdapterHelper.initCategories(news);
		this.previewUrl = NewsAdapterHelper.initPreviewUrl(news);
		this.previewAlt = NewsAdapterHelper.initPreviewAlt(news);
	}
	
	public static NewsAdapter of(News news) {
		return new NewsAdapter(news);
	}
	
	public static List<NewsAdapter> findAll() {
		return NewsAdapterHelper.findAll();
	}
}
