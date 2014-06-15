package i5.las2peer.services.servicePackage.layouters;

import i5.las2peer.services.servicePackage.graph.CustomGraph;
import i5.las2peer.services.servicePackage.graph.GraphDataProvider;
import y.layout.organic.OrganicLayouter;

public class OrganicGraphLayouter implements GraphLayouter {

	@Override
	public void doLayout(CustomGraph graph) {
		OrganicLayouter layouter = new OrganicLayouter();
		layouter.doLayout(graph);
		graph.getDataProvider(GraphDataProvider.COMMUNITIES);
	}

}
