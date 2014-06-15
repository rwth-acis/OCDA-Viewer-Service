package i5.las2peer.services.servicePackage.layouters;

import i5.las2peer.services.servicePackage.graph.CustomGraph;
import i5.las2peer.services.servicePackage.graph.GraphDataProvider;
import y.layout.circular.CircularLayouter;

public class CircularGraphLayouter implements GraphLayouter {

	@Override
	public void doLayout(CustomGraph graph) {
		CircularLayouter layouter = new CircularLayouter();
		layouter.doLayout(graph);
		graph.getDataProvider(GraphDataProvider.COMMUNITIES);
	}
	
}
