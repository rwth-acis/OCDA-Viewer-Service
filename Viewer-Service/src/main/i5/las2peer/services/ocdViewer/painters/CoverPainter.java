package i5.las2peer.services.ocdViewer.painters;

import i5.las2peer.services.ocd.graphs.Cover;

/**
 * The common interface for all cover painters
 * Define and set the community colors of a cover.
 * @author Sebastian
 *
 */
public interface CoverPainter {
	
	/**
	 * Sets the community colors of a cover;
	 * @param cover The cover.
	 */
	public abstract void doPaint(Cover cover);
}
