package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;

/**
 * Table of Contents
 * 
 * @author Raul Vasi
 *
 */
public class TOCset {

    File file;
    String titel;

    /**
     * Konstruktor
     */
    public TOCset() {
    }

    /**
     * File wird ausgelesen und ins Book als TOCreference geschrieben.
     * 
     * @param file
     * @param book
     */
    public void insertTOCs(File file, Book book) {

	try (InputStream in = new FileInputStream(file)) {
	    this.file = file;
	    Document doc = Jsoup.parse(file, "UTF-8");
	    Elements titles = doc.select("h2");
	    titel = titles.get(0).text();
	    book.getMetadata().addTitle(titel);
	    book.addSection(titel, new Resource(in, titel + ".html"));

	    for (int i = 1; i < titles.size(); i++) {
		Element t = titles.get(i);
		String chapter = t.text();
		String currentId = titles.get(i).select("a[id]").attr("id").toString();
		Resource r = new Resource(titel + ".html" + "#" + currentId);
		r.setMediaType(new MediaType("html", "application/xhtml+xml"));
		book.addSection(chapter, r);
	    }

	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

}
