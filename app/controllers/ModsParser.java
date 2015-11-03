package controllers;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;

/**
 * @author Raul Vasi
 *
 */
public class ModsParser {

    Document doc;

    /**
     * Die Metadaten als Mods format werden in einem Document geschrieben.
     * 
     * @param metadataUrl
     *            URL Adresse von denn MetaDaten.
     */
    public ModsParser(String metadataUrl) {
	try {
	    doc = Jsoup.connect(metadataUrl).get();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Die Metadaten aus dem Document werden ge-parset und ins ebook eingefügt
     * 
     * @param book
     */
    public void insertAllMetas(Book book) {

	addTitel(doc, book);
	addAuthors(doc, book);
	addDate(doc, book);
	addSubject(doc, book);
    }

    private void addSubject(Document doc, Book book) {
	List<String> subjects = new ArrayList<String>();
	subjects.add(doc.select("subject").text());
	book.getMetadata().setSubjects(subjects);
	System.out.println(book.getMetadata().getSubjects());
    }

    private void addDate(Document doc, Book book) {
	Date date = new Date(doc.select("date").text());
	book.getMetadata().addDate(date);
    }

    private void addTitel(Document doc, Book book) {
	book.getMetadata().addTitle(doc.title());
    }

    private void addAuthors(Document doc, Book book) {
	for (Element e : doc.getElementsByTag("name")) {
	    String firstname = e.select("namepart[type=given]").text();
	    String lastname = e.select("namepart[type=family]").text();
	    book.getMetadata().addAuthor(new Author(firstname, lastname));
	}
    }

}
