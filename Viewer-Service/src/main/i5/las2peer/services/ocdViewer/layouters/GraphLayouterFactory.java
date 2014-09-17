package i5.las2peer.services.ocdViewer.layouters;

import i5.las2peer.services.ocd.utils.SimpleFactory;

public class GraphLayouterFactory implements SimpleFactory<GraphLayouter, GraphLayoutType>{

	@Override
	public GraphLayouter getInstance(GraphLayoutType layoutType) throws InstantiationException, IllegalAccessException {
		return layoutType.getLayouterClass().newInstance();
	}

}
