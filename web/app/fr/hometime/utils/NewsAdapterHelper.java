package fr.hometime.utils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import models.News;
import models.NewsAdapter;
import models.NewsAlt;
import models.NewsCategory;
import models.NewsUrl;

public class NewsAdapterHelper {
	public static List<NewsAdapter> findAll() {
		return News.findAll().stream().map(NewsAdapter::of).collect(Collectors.toList());
	}
	
	public static List<String> initCategories(News news) {
		return mapStrings(news.categories, NewsCategory::toString);
	}
	
	public static List<String> initPreviewUrl(News news) {
		return mapStrings(news.previewUrl, NewsUrl::toString);
	}
	
	public static List<String> initPreviewAlt(News news) {
		return mapStrings(news.previewAlt, NewsAlt::toString);
	}
	
	private static <T> List<String> mapStrings(List<T> values, Function<T, String> mapper) {
		return values.stream().map(mapper).collect(Collectors.toList());
	}
}
