package i5.las2peer.services.servicePackage.layouters;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.util.List;

import org.junit.Test;

public class DefaultCoverLayouterTest {

	private static final int COLOR_COUNT = 5;
	
	@Test
	public void testColorSelection() {
		DefaultCoverLayouter layouter = new DefaultCoverLayouter();
		List<Color> colors = layouter.getColorCollection(COLOR_COUNT);
		assertEquals(colors.size(), COLOR_COUNT);
		for(int i=0; i<COLOR_COUNT; i++) {
				System.out.println(colors.get(i).toString());
		}
	}

}
