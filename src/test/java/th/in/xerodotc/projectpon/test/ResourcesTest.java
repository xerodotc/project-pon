/**
 * ResourcesTest.java
 *
 * For testing resources loading.
 *
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.test;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

public class ResourcesTest {

	@Test
	public void testLoadFont() throws Exception {
		String resPath = "res/fonts/advocut.ttf";
		InputStream istream = ResourcesTest.class.getClassLoader().getResourceAsStream(resPath);
		if (istream == null) {
			istream = new FileInputStream(new File(resPath));
		}
		assertNotNull(istream);
	}

	@Test
	public void testLoadImages() throws Exception {
		String[] resPaths = {"res/img/item_expand.png", "res/img/item_shrink.png",
			"res/img/item_wall.png", "res/img/item_sticky.png", "res/img/item_blind.png",
			"res/img/item_invert.png", "res/img/help.png", "res/img/about.png"};

		for (String resPath : resPaths) {
			InputStream istream = ResourcesTest.class.getClassLoader().getResourceAsStream(resPath);
			if (istream == null) {
				istream = new FileInputStream(new File(resPath));
			}
			assertNotNull(istream);
		}
	}
}
