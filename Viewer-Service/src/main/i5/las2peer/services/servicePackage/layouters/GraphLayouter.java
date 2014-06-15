package i5.las2peer.services.servicePackage.layouters;

import i5.las2peer.services.servicePackage.graph.CustomGraph;

public interface GraphLayouter {
	
	/**
	 * Applies the layout to the given graph;
	 */
	public abstract void doLayout(CustomGraph graph);
	
}
