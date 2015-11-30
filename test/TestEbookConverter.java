import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import controllers.EbookConverter;
import controllers.ModsParser;
import controllers.WebDownloader;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import play.Play;
import play.test.FakeApplication;
import play.test.Helpers;

@SuppressWarnings({ "javadoc" })
/**
 * 
 * @author Raul Vasi
 *
 */
public class TestEbookConverter {

    public static FakeApplication app;

    @BeforeClass
    public static void startApp() {
	app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
	Helpers.start(app);
    }

    @AfterClass
    public static void stopApp() {
	Helpers.stop(app);
    }

    @Test
    public void testClass() {
	String inputDirectory = Thread.currentThread().getContextClassLoader().getResource("epub").getPath();
	EbookConverter conv = new EbookConverter(inputDirectory);
	File result = conv.convert("/tmp/rauls.epub");
	// result.deleteOnExit();
	Assert.assertEquals(true, result.exists());
    }

    @Test
    public void test_ebook_result() {
	try {
	    String inputDirectory = Thread.currentThread().getContextClassLoader().getResource("Epublib").getPath();
	    EbookConverter conv = new EbookConverter(inputDirectory);
	    File result = conv.convert("/tmp/rauls.epub");
	    result.deleteOnExit();
	    Book readBook = new EpubReader().readEpub(new FileInputStream(result));

	    System.out.println(readBook.getTitle());
	    System.out.println(readBook.getResources().getAll());
	    System.out.println(readBook.getResources().getByHref("Epublib – a java epub library_files/ga.js"));
	    System.out.println(readBook.getResources()
		    .getByHref("Epublib – a java epub library_files/200px-Epub_logo_color-109x150.png"));
	    System.out.println(
		    readBook.getResources().getByHref("Epublib – a java epub library_files/200px-Epub_logo_color.png"));
	    System.out.println(readBook.getResources().getByHref(
		    "Epublib – a java epub library_files/Alices-Adventures-in-Wonderland_2011-01-30_18-17-30-300x181.png"));
	    System.out.println(
		    readBook.getResources().getByHref("Epublib – a java epub library_files/external-tracking.js"));
	    System.out.println(readBook.getResources().getByHref("Epublib – a java epub library_files/init.js"));
	    System.out.println(
		    readBook.getResources().getByHref("Epublib – a java epub library_files/jquery-migrate.js"));
	    System.out.println(readBook.getResources().getByHref("Epublib – a java epub library_files/jquery.js"));
	    System.out.println(readBook.getResources().getByHref("Epublib – a java epub library_files/style.css"));
	    System.out.println(
		    readBook.getResources().getByHref("Epublib – a java epub library_files/wp-emoji-release.js"));

	    // ----------------ASSERTS------------------

	    Assert.assertEquals(true,
		    readBook.getResources().getByHref("Epublib – a java epub library_files/ga.js") != null);
	    Assert.assertEquals(true, readBook.getResources()
		    .getByHref("Epublib – a java epub library_files/200px-Epub_logo_color-109x150.png") != null);
	    Assert.assertEquals(true, readBook.getResources()
		    .getByHref("Epublib – a java epub library_files/200px-Epub_logo_color.png") != null);
	    Assert.assertEquals(true, readBook.getResources().getByHref(
		    "Epublib – a java epub library_files/Alices-Adventures-in-Wonderland_2011-01-30_18-17-30-300x181.png") != null);
	    Assert.assertEquals(true, readBook.getResources()
		    .getByHref("Epublib – a java epub library_files/external-tracking.js") != null);
	    Assert.assertEquals(true,
		    readBook.getResources().getByHref("Epublib – a java epub library_files/init.js") != null);
	    Assert.assertEquals(true,
		    readBook.getResources().getByHref("Epublib – a java epub library_files/jquery-migrate.js") != null);
	    Assert.assertEquals(true,
		    readBook.getResources().getByHref("Epublib – a java epub library_files/jquery.js") != null);
	    Assert.assertEquals(true,
		    readBook.getResources().getByHref("Epublib – a java epub library_files/style.css") != null);
	    Assert.assertEquals(true, readBook.getResources()
		    .getByHref("Epublib – a java epub library_files/wp-emoji-release.js") != null);

	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Test
    public void test_withoutModsMetadata() throws FileNotFoundException, IOException {
	WebDownloader dwnl = new WebDownloader("https://www.jvrb.org/past-issues/11.2014/4075/fulltext/fedoraxml_body");
	dwnl.defineSubDirectory(System.nanoTime());
	File dir = dwnl.download("index.html");
	System.out.println(dir.getAbsolutePath());
	dir.deleteOnExit();
	EbookConverter conv = new EbookConverter(dir.getAbsolutePath());

	File result = conv.convert(dir + "/rauls.epub");
	result.deleteOnExit();
	Assert.assertEquals(true, result.exists());
	Assert.assertEquals(true, dir.exists());
    }

    @Test
    public void test_withModsMetadata() throws FileNotFoundException, IOException {
	WebDownloader dwnl = new WebDownloader("https://www.jvrb.org/past-issues/11.2014/4075/fulltext/fedoraxml_body");
	dwnl.defineSubDirectory(System.nanoTime());
	File dir = dwnl.download("index.html");
	System.out.println(dir.getAbsolutePath());
	// dir.deleteOnExit();
	EbookConverter conv = new EbookConverter(dir.getAbsolutePath(),
		new ModsParser("https://alkyoneus.hbz-nrw.de/dev/jahrgang-2015/ausgabe-1/2295/metadata/xml"));

	File result = conv.convert(dir + "/rauls.epub");
	// result.deleteOnExit();
	Assert.assertEquals(true, result.exists());
	Assert.assertEquals(true, dir.exists());
	System.out.println(result.toString());
    }

    @Test
    public void testDownloader() throws IOException {
	WebDownloader dwnl = new WebDownloader("https://www.jvrb.org/past-issues/11.2014/4075/fulltext/fedoraxml_body");
	File dir = dwnl.download("tmp.html");
	dir.deleteOnExit();
    }

    @Test
    public void testDownloader2() throws IOException {
	WebDownloader dwnl = new WebDownloader(
		"https://alkyoneus.hbz-nrw.de/dev/jahrgang-2015/ausgabe-1/2295/metadata/xml");
	File dir = dwnl.download("tmp.html");
	dir.deleteOnExit();
    }

    @Test
    public void confTest() throws IOException {
	Properties properties = new Properties();
	properties.load(Play.application().resourceAsStream("config.properties"));
	System.out.println(properties.getProperty("fedoraUrl"));
	System.out.println(Play.application().getFile("").getAbsolutePath());

    }

    @SuppressWarnings("unused")
    @Test
    public void testThreads() throws IOException, InterruptedException {
	Thread t1 = new Thread() {
	    public void run() {
		try {
		    WebDownloader dwnl = new WebDownloader(
			    "https://www.jvrb.org/past-issues/11.2014/4075/fulltext/fedoraxml_body");
		    File dir = dwnl.download("tmp.html");
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};
	Thread t2 = new Thread() {
	    public void run() {
		try {
		    WebDownloader dwnl = new WebDownloader(
			    "https://www.jvrb.org/past-issues/11.2014/4075/fulltext/fedoraxml_body");
		    File dir = dwnl.download("tmp.html");
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};
	Thread t3 = new Thread() {
	    public void run() {
		try {
		    WebDownloader dwnl = new WebDownloader(
			    "https://www.jvrb.org/past-issues/11.2014/4075/fulltext/fedoraxml_body");
		    File dir = dwnl.download("tmp.html");
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};
	Thread t4 = new Thread() {
	    public void run() {
		try {
		    WebDownloader dwnl = new WebDownloader(
			    "https://www.jvrb.org/past-issues/11.2014/4075/fulltext/fedoraxml_body");
		    File dir = dwnl.download("tmp.html");
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};
	Thread t5 = new Thread() {
	    public void run() {
		try {
		    WebDownloader dwnl = new WebDownloader(
			    "https://www.jvrb.org/past-issues/11.2014/4075/fulltext/fedoraxml_body");
		    File dir = dwnl.download("tmp.html");
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};
	Thread t6 = new Thread() {
	    public void run() {
		try {
		    WebDownloader dwnl = new WebDownloader(
			    "https://www.jvrb.org/past-issues/11.2014/4075/fulltext/fedoraxml_body");
		    File dir = dwnl.download("tmp.html");
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};

	t1.start();
	t2.start();
	t3.start();
	t4.start();
	t5.start();
	t6.start();

    }

    @Test
    public void extractMetasTest() throws IOException {
	@SuppressWarnings("unused")
	String url = "https://alkyoneus.hbz-nrw.de/dev/jahrgang-2015/ausgabe-1/2295/#fulltext";
	// Document doc = Jsoup.connect(url).get();
	// Elements links = doc.select("[href]");
	// for (Element link : links) {
	// String l = link.attr("abs:href");
	// if (l.endsWith("metadata")) {
	// System.out.println(">>>>>>>>>>>>>>>>" + l);
	//
	// }
	// }

	Document doc = Jsoup.connect("https://alkyoneus.hbz-nrw.de/dev/jahrgang-2015/ausgabe-1/2295/metadata/xml")
		.get();

	// String ldoc = doc.select("pre:contains(Mods)").text();
	// doc = Jsoup.parse(ldoc);

	// Schleife über alle Names
	for (Element e : doc.getElementsByTag("name")) {
	    String vorname = e.select("namepart[type=given] ").text();
	    vorname += ", " + e.select("namepart[type=family] ").text();
	    System.out.println(vorname);
	}

    }

    @Test
    public void getMetasTest() {
	String inputDirectory = Thread.currentThread().getContextClassLoader().getResource("epub").getPath();
	EbookConverter conv = new EbookConverter(inputDirectory,
		new ModsParser("https://alkyoneus.hbz-nrw.de/dev/jahrgang-2015/ausgabe-1/2295/metadata/xml"));

	File result = conv.convert("/tmp/rauls.epub");
	Assert.assertEquals(true, result.exists());
    }

    @Test
    public void addEnocdingTest() throws MalformedURLException, IOException {
	String url = "https://www.jvrb.org/past-issues/11.2014/4075/fulltext/fedoraxml_body";
	Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
	Element e = doc.select("head").first();
	e.prepend("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
	// doc.select("head").add(e);
    }

    @Test
    public void extractLinks_Test() {
	try {
	    String url = "https://alkyoneus.hbz-nrw.de/dev/jahrgang-2015/ausgabe-1/2295/ebookey";
	    Document doc = Jsoup.connect(url).get();
	    Elements e = doc.select("a");
	    for (int i = 0; i < e.size(); i++) {
		if (e.get(i).text().contains("http")) {
		    if (e.get(i).text().contains("metadata")) {
			System.out.println("Metas->-> " + e.get(i).text());
		    } else {
			System.out.println("Fulltext->-> " + e.get(i).text());
		    }
		} else {
		    System.out.println("Nur epup: " + e.get(i).text());
		}
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Test
    public void extractLinks_Test1() {
	try {
	    String url = "https://alkyoneus.hbz-nrw.de/dev/jahrgang-2015/ausgabe-1/2295/ebookey";
	    Document doc = Jsoup.connect(url).get();
	    Elements article = doc.select("a.article");
	    System.out.println(article.text());
	    Elements metas = doc.select("a.metadata");
	    System.out.println(metas.text());
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

}
