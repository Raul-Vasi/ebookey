import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.fluentlenium.core.Fluent;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import controllers.Application;
import controllers.EbookConverter;
import controllers.WebDownloader;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import play.Play;
import play.libs.F.Callback;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestBrowser;

@SuppressWarnings({ "javadoc", "deprecation" })
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
	String inputDirectory = Thread.currentThread().getContextClassLoader()
		.getResource("epub").getPath();
	EbookConverter conv = new EbookConverter(inputDirectory);
	File result = conv.convert("/tmp/rauls.epub");
	result.deleteOnExit();
	Assert.assertEquals(true, result.exists());
    }

    @Test
    public void test_ebook_result() {
	try {
	    String inputDirectory = Thread.currentThread()
		    .getContextClassLoader().getResource("epub").getPath();
	    EbookConverter conv = new EbookConverter(inputDirectory);
	    File result = conv.convert("/tmp/rauls.epub");
	    result.deleteOnExit();
	    Book readBook = new EpubReader().readEpub(new FileInputStream(
		    result));

	    System.out.println(readBook.getTitle());
	    System.out.println(readBook.getResources().getAll());
	    System.out.println(readBook.getResources().getByHref(
		    "Epublib – a java epub library_files/ga.js"));
	    System.out
		    .println(readBook
			    .getResources()
			    .getByHref(
				    "Epublib – a java epub library_files/200px-Epub_logo_color-109x150.png"));
	    System.out
		    .println(readBook
			    .getResources()
			    .getByHref(
				    "Epublib – a java epub library_files/200px-Epub_logo_color.png"));
	    System.out
		    .println(readBook
			    .getResources()
			    .getByHref(
				    "Epublib – a java epub library_files/Alices-Adventures-in-Wonderland_2011-01-30_18-17-30-300x181.png"));
	    System.out
		    .println(readBook
			    .getResources()
			    .getByHref(
				    "Epublib – a java epub library_files/external-tracking.js"));
	    System.out.println(readBook.getResources().getByHref(
		    "Epublib – a java epub library_files/init.js"));
	    System.out.println(readBook.getResources().getByHref(
		    "Epublib – a java epub library_files/jquery-migrate.js"));
	    System.out.println(readBook.getResources().getByHref(
		    "Epublib – a java epub library_files/jquery.js"));
	    System.out.println(readBook.getResources().getByHref(
		    "Epublib – a java epub library_files/style.css"));
	    System.out.println(readBook.getResources().getByHref(
		    "Epublib – a java epub library_files/wp-emoji-release.js"));

	    // ----------------ASSERTS------------------

	    Assert.assertEquals(
		    true,
		    readBook.getResources().getByHref(
			    "Epublib – a java epub library_files/ga.js") != null);
	    Assert.assertEquals(
		    true,
		    readBook.getResources()
			    .getByHref(
				    "Epublib – a java epub library_files/200px-Epub_logo_color-109x150.png") != null);
	    Assert.assertEquals(
		    true,
		    readBook.getResources()
			    .getByHref(
				    "Epublib – a java epub library_files/200px-Epub_logo_color.png") != null);
	    Assert.assertEquals(
		    true,
		    readBook.getResources()
			    .getByHref(
				    "Epublib – a java epub library_files/Alices-Adventures-in-Wonderland_2011-01-30_18-17-30-300x181.png") != null);
	    Assert.assertEquals(
		    true,
		    readBook.getResources()
			    .getByHref(
				    "Epublib – a java epub library_files/external-tracking.js") != null);
	    Assert.assertEquals(
		    true,
		    readBook.getResources().getByHref(
			    "Epublib – a java epub library_files/init.js") != null);
	    Assert.assertEquals(
		    true,
		    readBook.getResources()
			    .getByHref(
				    "Epublib – a java epub library_files/jquery-migrate.js") != null);
	    Assert.assertEquals(
		    true,
		    readBook.getResources().getByHref(
			    "Epublib – a java epub library_files/jquery.js") != null);
	    Assert.assertEquals(
		    true,
		    readBook.getResources().getByHref(
			    "Epublib – a java epub library_files/style.css") != null);
	    Assert.assertEquals(
		    true,
		    readBook.getResources()
			    .getByHref(
				    "Epublib – a java epub library_files/wp-emoji-release.js") != null);

	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Test
    public void testAll() throws FileNotFoundException, IOException {
	WebDownloader dwnl = new WebDownloader(
		"http://www.tutorialspoint.com/java/java_string_substring.htm");
	File dir = dwnl.download("tmp.html");
	dir.deleteOnExit();
	EbookConverter conv = new EbookConverter(dir.getAbsolutePath());
	File result = conv.convert(File.createTempFile("ebook", ".epub")
		.getAbsolutePath());
	result.deleteOnExit();
	Assert.assertEquals(true, result.exists());
	Assert.assertEquals(true, dir.exists());
	System.out.println(result.toString());
    }

    @Test
    public void testDownloader() {
	WebDownloader dwnl = new WebDownloader(
		"http://www.tutorialspoint.com/java/java_string_substring.htm");
	File dir = dwnl.download("tmp.html");
	dir.deleteOnExit();
    }

    @Test
    public void confTest() throws IOException {
	Properties properties = new Properties();
	properties.load(Play.application()
		.resourceAsStream("config.properties"));
	System.out.println(properties.getProperty("fedoraUrl"));
	System.out.println(Play.application().getFile("").getAbsolutePath());

    }

}
