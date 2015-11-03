import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import org.junit.Test;

import play.libs.F.Callback;
import play.test.TestBrowser;

/**
 * @author Raul Vasi
 *
 */
public class IntegrationTest {

    /**
     * add your integration test here in this example we just check if the
     * welcome page is being shown
     */
    @Test
    public void test_whitoutMetas() {
	running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
	    public void invoke(TestBrowser browser) {
		browser.goTo(
			"http://localhost:9000/tools/ebookey/?url=https://www.jvrb.org/past-issues/11.2014/4075/fulltext/fedoraxml_body");

	    }
	});
    }

    @Test
    public void test_whitMetas() {
	running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
	    public void invoke(TestBrowser browser) {
		browser.goTo(
			"http://localhost:9000/tools/ebookey/?url=https://www.jvrb.org/past-issues/11.2014/4075/fulltext/fedoraxml_body,"
				+ "?url=https://alkyoneus.hbz-nrw.de/dev/jahrgang-2015/ausgabe-1/2295/metadata/xml");

	    }
	});
    }
}
