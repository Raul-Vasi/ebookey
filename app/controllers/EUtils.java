package controllers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EUtils {

    public static String getArticleUrl(String url) {
	try {
	    Document doc = Jsoup.connect(url).get();
	    Elements article = doc.select("a.article");
	    return article.text();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static String getMetasUrl(String url) {
	try {
	    Document doc = Jsoup.connect(url).get();
	    Elements metas = doc.select("a.metadata");
	    return metas.text();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}

    }

}
