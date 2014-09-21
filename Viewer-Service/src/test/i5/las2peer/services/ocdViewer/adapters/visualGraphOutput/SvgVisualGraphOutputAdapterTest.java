package i5.las2peer.services.ocdViewer.adapters.visualGraphOutput;

import i5.las2peer.services.ocd.adapters.AdapterException;
import i5.las2peer.services.ocd.graphs.CustomGraph;
import i5.las2peer.services.ocdViewer.adapters.visualOutput.SvgVisualOutputAdapter;
import i5.las2peer.services.ocdViewer.layouters.GraphLayoutType;
import i5.las2peer.services.ocdViewer.testsUtil.ViewerTestConstants;
import i5.las2peer.services.ocdViewer.testsUtil.ViewerTestGraphFactory;
import i5.las2peer.services.ocdViewer.utils.LayoutHandler;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;


public class SvgVisualGraphOutputAdapterTest {
	
	@Test
	public void testOnTinyCircleGraph() throws IOException, AdapterException, InstantiationException, IllegalAccessException {
		CustomGraph graph = ViewerTestGraphFactory.getTinyCircleGraph();
		LayoutHandler handler = new LayoutHandler();
		handler.doLayout(graph, GraphLayoutType.ORGANIC, true, false, 20, 45);
		SvgVisualOutputAdapter adapter = new SvgVisualOutputAdapter();
		adapter.setWriter(new FileWriter(ViewerTestConstants.tinyCircleSvgOutputPath));
		adapter.writeGraph(graph);
	}
	
	@Test
	public void testOnTwoCommunitiesGraph() throws AdapterException, IOException, InstantiationException, IllegalAccessException {
		CustomGraph graph = ViewerTestGraphFactory.getTwoCommunitiesGraph();
		LayoutHandler handler = new LayoutHandler();
		handler.doLayout(graph, GraphLayoutType.ORGANIC, true, false, 20, 45);
		SvgVisualOutputAdapter adapter = new SvgVisualOutputAdapter();
		adapter.setWriter(new FileWriter(ViewerTestConstants.twoCommunitiesSvgOutputPath));
		adapter.writeGraph(graph);
	}
	
	@Test
	public void testSvgOnSawmillGraph() throws AdapterException, IOException, InstantiationException, IllegalAccessException {
		CustomGraph graph = ViewerTestGraphFactory.getSawmillGraph();
		SvgVisualOutputAdapter adapter = new SvgVisualOutputAdapter();
		LayoutHandler handler = new LayoutHandler();
		handler.doLayout(graph, GraphLayoutType.ORGANIC, true, false, 20, 45);
		adapter.setWriter(new FileWriter(ViewerTestConstants.sawmillSvgOutputPath));
		adapter.writeGraph(graph);
	}


}
