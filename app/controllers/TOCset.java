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
	    unicodingTile(titel);
	    book.getMetadata().addTitle(titel);
	    book.addSection(titel, new Resource(in, titel + ".html"));

	    for (int i = 1; i < titles.size(); i++) {
		Element t = titles.get(i);
		String chapter = t.text();
		String currentId = titles.get(i).select("a[id]").attr("id").toString();
		Resource r = new Resource(titel + ".html" + "#" + currentId);
		r.setMediaType(new MediaType("application/xhtml+xml", "html"));
		book.addSection(chapter, r);
	    }

	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private String unicodingTile(String titel1) {
	titel = titel.replaceAll("[_[^\\w\\ ] ]", "");
	return titel;
    }

}
