package i5.las2peer.services.ocdViewer.painters;

import i5.las2peer.services.ocd.graphs.Cover;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses a set of 19 predefined high contrast colors which are also supposed to be easily identifiable
 * for most people with color blindness.
 * @author Sebastian
 *
 */
public class PredefinedColorsCoverPainter implements CoverPainter {

	@Override
	public void doPaint(Cover cover) {
		List<Color> colors = getColorCollection(cover.communityCount());
		for(int i=0; i<cover.communityCount(); i++) {
			cover.setCommunityColor(i, colors.get(i));
		}
	}

	/**
	 * Returns the predefined color collection.
	 * @param amount The amount of colors required.
	 * @return The color collection. In case the amount of required colors is bigger
	 * than the amount of predefined colors, the collection colors will periodically
	 * repeat themselves.
	 */
	protected List<Color> getColorCollection(int amount) {
	    Color[] colorCollection = new Color[19];
	    colorCollection[0] = new Color(0xF4CD4E);
	    colorCollection[1] = new Color(0x592C8F);
	    colorCollection[2] = new Color(0xEB6522);
	    colorCollection[3] = new Color(0x95D6E8);
	    colorCollection[4] = new Color(0xC9272D);
	    colorCollection[5] = new Color(0xC0C083);
	    colorCollection[6] = new Color(0x5CAA46);
	    colorCollection[7] = new Color(0xD988BA);
	    colorCollection[8] = new Color(0x367AB9);
	    colorCollection[9] = new Color(0xE88162);
	    colorCollection[10] = new Color(0x2A3297);
	    colorCollection[11] = new Color(0xEE9F36);
	    colorCollection[12] = new Color(0x862991);
	    colorCollection[13] = new Color(0xECE566);
	    colorCollection[14] = new Color(0x831C15);
	    colorCollection[15] = new Color(0x8EB03E);
	    colorCollection[16] = new Color(0x703116);
	    colorCollection[17] = new Color(0xE5331A);
	    colorCollection[18] = new Color(0x2A3319);
	    List<Color> colors = new ArrayList<Color>();
	    for(int i=0; i<amount; i++) {
	    	colors.add(colorCollection[i%19]);
	    }
	    return colors;
	}
	
}
