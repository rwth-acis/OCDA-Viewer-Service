package i5.las2peer.services.ocdViewer.painters;

import i5.las2peer.services.ocd.graphs.Cover;

public interface CoverPainter {
	
	/**
	 * Sets the community colors of a cover;
	 */
	public abstract void doPaint(Cover cover);
}
