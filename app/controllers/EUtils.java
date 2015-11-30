/*Copyright (c) 2015 "hbz"

This file is part of ebooky.

ebooky is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package controllers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @author Raul Vasi
 *
 */
public class EUtils {

    /**
     * Die Url-Adresse des Artikels wir ausgeparst und als String weitergegeben
     * 
     * @param url
     * @return URL-Adresse als String
     */
    public static String getArticleUrl(String url) {
	try {
	    Document doc = Jsoup.connect(url).get();
	    Elements article = doc.select("a.article");
	    return article.text();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Die Url-Adresse der Metadaten wir ausgeparst und als String weitergegeben
     * 
     * @param url
     * @return URL-Adresse als String
     */
    public static String getMetasUrl(String url) {
	try {
	    Document doc = Jsoup.connect(url).get();
	    Elements metas = doc.select("a.metadata");
	    return metas.text();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}

    }

    /**
     * Die Url-Adresse des Cover-Bildes wir ausgeparst und als String
     * weitergegeben
     * 
     * @param url
     * @return URL-Adresse als String
     */
    public static String getCoverUrl(String url) {
	try {
	    Document doc = Jsoup.connect(url).get();
	    Elements cover = doc.select("a.cover");
	    return cover.text();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

}
