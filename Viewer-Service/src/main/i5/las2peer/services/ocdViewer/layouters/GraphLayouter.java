package i5.las2peer.services.ocdViewer.layouters;

import i5.las2peer.services.ocd.graphs.CustomGraph;

public interface GraphLayouter {
	
	/**
	 * Applies the layout to the given graph;
	 */
	public abstract void doLayout(CustomGraph graph);
	
}
