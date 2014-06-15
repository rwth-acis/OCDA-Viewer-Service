package i5.las2peer.services.servicePackage.visualGraphOutputAdapters;

import i5.las2peer.services.servicePackage.graph.CustomGraph;
import i5.las2peer.services.servicePackage.testsUtil.TestConstants;
import i5.las2peer.services.servicePackage.testsUtil.TestGraphFactory;

import org.junit.Test;

public class SvgVisualGraphOutputAdapterTest {

	private static String TINY_CIRCLE_GRAPH_FILENAME = TestConstants.getOutputFolder() + "TinyCircleGraph.svg";
	private static String TWO_COMMUNITIES_GRAPH_FILENAME = TestConstants.getOutputFolder() + "TwoCommunitiesGraph.svg";
	private static String SAWMILL_GRAPH_FILENAME = TestConstants.getOutputFolder() + "SawmillGraph.svg";
	
	@Test
	public void testOnTinyCircleGraph() {
		CustomGraph graph = TestGraphFactory.getTinyCircleGraph();
		SvgVisualGraphOutputAdapter adapter = new SvgVisualGraphOutputAdapter(TINY_CIRCLE_GRAPH_FILENAME);
		adapter.writeGraph(graph);
	}
	
	@Test
	public void testOnTwoCommunitiesGraph() {
		CustomGraph graph = TestGraphFactory.getTwoCommunitiesGraph();
		SvgVisualGraphOutputAdapter adapter = new SvgVisualGraphOutputAdapter(TWO_COMMUNITIES_GRAPH_FILENAME);
		adapter.writeGraph(graph);
	}
	
	@Test
	public void testSvgOnSawmillGraph() {
		CustomGraph graph = TestGraphFactory.getSawmillGraph();
		SvgVisualGraphOutputAdapter adapter = new SvgVisualGraphOutputAdapter(SAWMILL_GRAPH_FILENAME);
		adapter.writeGraph(graph);
	}

}
