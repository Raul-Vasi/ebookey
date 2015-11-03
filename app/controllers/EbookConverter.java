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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

/**
 * Der <i>EbookConverter</i> konvertiert <code>HTML</code> Seiten in
 * <code>Epubs</code>
 * <p>
 * 
 * @author Raul Vasi
 *
 */
public class EbookConverter {

    Book book = new Book();
    ModsParser parser = null;

    String source;

    /**
     * 
     * 
     * @param absolutePathToHtmlDir
     *            Der abosolute lokale Pfad zur <code>HTML</code> Seite als
     *            {@code String}. Beispiel: <blockquote>
     * 
     * 
     *            EbookConverter conv = new EbookConverter("/home/user/HTML");;
     * 
     * 
     *            </blockquote>
     *            <p>
     * @param parser
     */
    public EbookConverter(String absolutePathToHtmlDir, ModsParser parser) {
	source = absolutePathToHtmlDir;
	this.parser = parser;
    }

    /**
     * 
     * 
     * @param absolutePathToHtmlDir
     *            Der abosolute lokale Pfad zur <code>HTML</code> Seite als
     *            {@code String}. Beispiel: <blockquote>
     * 
     * 
     *            EbookConverter conv = new EbookConverter("/home/user/HTML");;
     * 
     * 
     *            </blockquote>
     *            <p>
     */

    public EbookConverter(String absolutePathToHtmlDir) {
	source = absolutePathToHtmlDir;
    }

    // --------------------------------------------------------------------------------------------------------------------
    void createBook(File dir) {
	File[] files = dir.listFiles();
	if (files != null) {
	    for (int i = 0; i < files.length; i++) {
		if (files[i].isDirectory()) {
		    createBook(files[i]);
		} else {
		    addToBook(files[i]);
		}
	    }
	}
    }

    // --------------------------------------------------------------------------------------------------------------------
    void addToBook(File file) {
	try {
	    String filename = file.toString();
	    if (filename.endsWith("html")) {
		addChapter(file);
	    } else {
		addResource(file);
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    // --------------------------------------------------------------------------------------------------------------------
    void addResource(File file) {
	try (InputStream input = new FileInputStream(file)) {
	    Path base = Paths.get(source);
	    String filePathName = base.relativize(Paths.get(file.getAbsolutePath())).toString();
	    book.addResource(new Resource(input, filePathName));
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    // --------------------------------------------------------------------------------------------------------------------
    void addChapter(File file) {
	TOCset tocs = new TOCset();
	tocs.insertTOCs(file, book);
	parser.insertAllMetas(book);
    }

    // --------------------------------------------------------------------------------------------------------------------
    /**
     * Konvertiert alle gesamelten Daten/META-Daten zu einem <i>epub</i>.
     * 
     * @param targetFile
     *            Den Absoluten Pfad und Namen der zu erzeugenden Datei.
     * 
     * @return Denn Ausgangspfad der erzeugten Epub.
     */
    public File convert(String targetFile) {
	try {
	    File destination = new File(targetFile);
	    createBook(new File(source));
	    createToc();
	    new EpubWriter().write(book, new FileOutputStream(destination));
	    return destination;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private void createToc() {
	System.out.println(book.getTitle());

    }

}
// --------------------------------------------------------------------------------------------------------------------