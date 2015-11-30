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

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;
import nl.siegmann.epublib.domain.Identifier;

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
	addIdentifier(doc, book);
    }

    private void addIdentifier(Document doc2, Book book) {
	String value = doc.select("identifier[type=uri]").text();
	Identifier id = new Identifier("uri", value);
	book.getMetadata().addIdentifier(id);
    }

    private void addSubject(Document doc, Book book) {
	List<String> subjects = new ArrayList<String>();
	subjects.add(doc.select("subject").text());
	book.getMetadata().setSubjects(subjects);
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
