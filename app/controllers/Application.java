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

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import play.Logger;
import play.mvc.Result;

/**
 * @author Raul Vasi
 *
 */
public class Application extends MyController {

    /**
     * Die Web Seit wird als "tmp.html" lokal abgespeichert, in Ebook
     * konvertiert und im HTML Header geschrieben.
     * 
     * @param url
     *            Absolute URL
     * @return File Objekt als epub
     */
    public Result index(String url) {
	Logger.debug("ebookey starts");
	try {
	    String articleUrl = EUtils.getArticleUrl(url);
	    String metadataUrl = EUtils.getMetasUrl(url);
	    String coverUrl = EUtils.getCoverUrl(url);
	    if (articleUrl != null) {
		if (!isWhitelisted(new URL(url).getHost())) {
		    Logger.error("Acces to <" + new URL(url).getHost() + "> is not allowed");
		    return status(403, "ebookey is not allowed to access this url!");
		}
		WebDownloader dwnl = new WebDownloader(articleUrl);
		Logger.info("Downloading " + articleUrl);
		dwnl.defineSubDirectory(getNanoTime());
		File dir = dwnl.download("index.html");
		dwnl.downloadCover(coverUrl);
		EbookConverter conv;

		if (metadataUrl.isEmpty()) {
		    conv = new EbookConverter(dir.getAbsolutePath());
		    conv.setCover(dwnl.cover_file);
		    Logger.info("Convert whitout Metadatas");

		} else {
		    conv = new EbookConverter(dir.getAbsolutePath(), new ModsParser(metadataUrl));
		    conv.setCover(dwnl.cover_file);
		    Logger.info("Convert with Metadatas from " + metadataUrl);
		}

		DateFormat dfmt = new SimpleDateFormat("yyyyMMddhhmmss");
		File result = conv.convert(dir + "/rauls.epub");
		response().setHeader("Content-Disposition",
			"inline; filename=\"" + dfmt.format(new Date()) + "ebook.epub");
		response().setHeader("Content-Type", "ebook/epub");
		return ok(result);
	    } else {
		Logger.error(new URL(url).getHost() + " is not reachable");
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	return ok(views.html.index.render(""));
    }

    private static boolean isWhitelisted(String host) {
	for (String w : whitelist) {
	    if (w.equals(host))
		return true;
	}
	return false;
    }

    synchronized private long getNanoTime() {
	return System.nanoTime();
    }

}
