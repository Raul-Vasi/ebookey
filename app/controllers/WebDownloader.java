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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.jsoup.Connection.Response;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import nl.siegmann.epublib.util.IOUtil;
import play.Logger;
import play.Play;

/**
 * @author Raul Vasi
 *
 */
public class WebDownloader {

    String url = null;
    String outputpath = "/tmp/";
    Properties prop = new Properties();
    String cover_file;

    /**
     * Konstruktor- Hier wird dir URL Adresse angenommen.
     * 
     * @param url
     * @param coverUrl
     */
    public WebDownloader(String url) {
	this.url = url;
    }

    /**
     * @param coverUrl
     */
    public void downloadCover(String coverUrl) {
	try {
	    String host = new String(new URL(url).getHost());
	    URL url = new URL(coverUrl);
	    cover_file = outputpath + File.separator + host + File.separator + "ebookey_cover"
		    + coverUrl.substring(coverUrl.lastIndexOf("."));
	    Logger.info("Cover file: " + cover_file);
	    try (InputStream is = url.openStream(); OutputStream os = new FileOutputStream(cover_file);) {

		byte[] b = new byte[1024];
		int length;

		while ((length = is.read(b)) != -1) {
		    os.write(b, 0, length);
		}

		is.close();
		os.close();
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Das downloaden der kompletten Webseite über die URL. Die URL samt
     * Resourcen wir in einem Document Objekt gespeichert. Das Document Objekt
     * wird in einem {@link ByteArrayInputStream} geschrieben und anschliessend
     * mit {@linkplain IOUtils} in Dateien angelegt.
     * 
     * @param filename
     * @return downloadLocation
     * @throws IOException
     */
    public File download(String filename) throws IOException {

	try {
	    readprop();

	    String downloadLocation = createDownloadLocation(outputpath);
	    Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
	    doc.getElementsByTag("html").first().attr("xmlns", "http://www.w3.org/1999/xhtml").attr("xml:lang", "en");
	    addEncoding(doc);
	    doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
	    Elements img = doc.getElementsByTag("img");
	    try (ByteArrayInputStream bs = new ByteArrayInputStream(doc.outerHtml().getBytes("UTF-8"))) {
		Logger.info("Filename: " + filename);
		if (filename.endsWith(".html")) {
		    IOUtil.copy(bs, new FileOutputStream(new File(downloadLocation + File.separator + filename)));
		} else {
		    IOUtil.copy(bs,
			    new FileOutputStream(new File(downloadLocation + File.separator + filename + ".html")));
		}
		xmlPretteyPrint(downloadLocation, filename);

		for (Element element : img) {
		    String src = element.absUrl("src");
		    Response resultImageResponse = Jsoup.connect(src).ignoreContentType(true).execute();
		    File file = new File(downloadLocation + "/" + element.attr("src"));
		    Files.createDirectories(Paths.get(file.getParent()));
		    saveAsFile(resultImageResponse, file);
		}
	    }
	    return new File(downloadLocation);
	} catch (Exception e1) {
	    throw new RuntimeException(e1);
	}
    }

    /**
     * @param path
     * @return ein Unterordern der denn Nanozeitwert als Name hat.
     */
    public String defineSubDirectory(Long path) {
	outputpath += path;
	return outputpath;
    }

    private Document addEncoding(Document doc) {
	Element e = doc.select("head").first();
	Node n = doc.createElement("meta");
	n.attr("http-equiv", "Content-Type");
	n.attr("content", "text/html; charset=utf-8");
	e.prepend("<title>" + doc.select("h2").first().ownText() + "</title>");
	return doc;

    }

    private void xmlPretteyPrint(String downloadLocation, String filename) {

	try {
	    CleanerProperties props = new CleanerProperties();
	    props.setTranslateSpecialEntities(true);
	    props.setTransResCharsToNCR(true);
	    props.setOmitComments(true);
	    TagNode tagNode = new HtmlCleaner(props).clean(new File(downloadLocation + File.separator + filename));
	    new PrettyXmlSerializer(props).writeToFile(tagNode, downloadLocation + File.separator + filename, "utf-8");
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private void saveAsFile(Response resultImageResponse, File file) {
	try (FileOutputStream fileOutStream = new FileOutputStream(file)) {
	    fileOutStream.write(resultImageResponse.bodyAsBytes());
	    fileOutStream.close();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private String readprop() {
	try {
	    prop.load(Play.application().resourceAsStream("config.properties"));
	    if (prop.getProperty("outputpath").length() == 0) {
		return outputpath;
	    } else {
		outputpath = prop.getProperty("outputpath");
		return outputpath;
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	} finally {
	    Logger.info("Cache for " + url + "on local: " + outputpath);
	}

    }

    private String createDownloadLocation(String outputpath) throws MalformedURLException, IOException {
	URL myUrl = new URL(url);
	String finalDownloadLocation = outputpath + File.separator + myUrl.getHost();
	Files.createDirectories(Paths.get(finalDownloadLocation));
	return finalDownloadLocation;
    }

}
