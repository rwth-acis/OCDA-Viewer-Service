package i5.las2peer.services.servicePackage.layouters;

import i5.las2peer.services.servicePackage.graph.Cover;

public interface CoverLayouter {
	
	/**
	 * Applies the layout to the given graph;
	 */
	public abstract void doLayout(Cover cover);
}
