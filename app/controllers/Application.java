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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * @author Raul Vasi
 *
 */
public class Application extends Controller {

    /**
     * Die Web Seit wird als "tmp.html" lokal abgespeichert, in Ebook
     * konvertiert und im HTML Header geschrieben.
     * 
     * @param url
     *            die Abosolute URL
     * @param metadataUrl
     * 
     * @return File Objekt als epub
     */
    public Result index(String url, String metadataUrl) {
	try {
	    if (url != null) {
		WebDownloader dwnl = new WebDownloader(url);
		dwnl.defineSubDirectory(getNanoTime());
		File dir = dwnl.download("tmp.html");
		EbookConverter conv;

		if (metadataUrl != null) {
		    conv = new EbookConverter(dir.getAbsolutePath(), new ModsParser(metadataUrl));
		} else {
		    conv = new EbookConverter(dir.getAbsolutePath());
		}

		DateFormat dfmt = new SimpleDateFormat("yyyyMMddhhmmss");
		File result = conv.convert(dir + "/rauls.epub");
		response().setHeader("Content-Disposition",
			"inline; filename=\"" + dfmt.format(new Date()) + "ebook.epub");
		response().setHeader("Content-Type", "ebook/epub");

		return ok(result);
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	return ok(views.html.index.render(""));
    }

    synchronized private long getNanoTime() {
	return System.nanoTime();
    }

}
