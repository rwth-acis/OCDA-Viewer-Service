package i5.las2peer.services.servicePackage.testsUtil;

import i5.las2peer.services.servicePackage.graph.CustomGraph;
import i5.las2peer.services.servicePackage.graph.GraphLabeler;
import i5.las2peer.services.servicePackage.graphInputAdapters.GraphInputAdapter;
import i5.las2peer.services.servicePackage.graphInputAdapters.GraphInputAdapterFactory;
import i5.las2peer.services.servicePackage.layouters.CircularGraphLayouter;
import i5.las2peer.services.servicePackage.layouters.GraphLayouter;
import i5.las2peer.services.servicePackage.layouters.OrganicGraphLayouter;

import java.util.HashMap;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

import y.base.Edge;
import y.base.Node;
import y.base.NodeCursor;
import y.view.NodeLabel;
import y.view.NodeRealizer;

public class TestGraphFactory {
	
	public static CustomGraph getTinyCircleGraph() {
		// Initialization
		CustomGraph graph = new CustomGraph();
		Matrix memberships = new Basic2DMatrix(10, 2);
		// Creates 10 nodes.
		Node n[] = new Node[10];  
		for (int i = 0; i < 10; i++) {
			n[i] = graph.createNode();
			graph.setNodeName(n[i], "id: " + i);
			memberships.set(i, i%2, 1);
		}
		// Creates 10 edges forming a cycle
		Edge e[] = new Edge[10];
		for (int i = 0; i < 10; i++) {
			e[i] = graph.createEdge(n[i], n[(i+1)%10]);
			graph.setEdgeWeight(e[i], 1.0);
		}
		// Labels graph
		GraphLabeler labeler = new GraphLabeler();
		labeler.labelGraph(graph);
		// Creates node and edge layout
		CircularGraphLayouter graphLayouter = new CircularGraphLayouter();
		graphLayouter.doLayout(graph);
		return new CustomGraph(graph, new HashMap<Edge, Double>(), new HashMap<Node, String>());
	}
	
	public static CustomGraph getTwoCommunitiesGraph() {
		// Creates new graph
		CustomGraph graph = new CustomGraph();
		// Creates nodes
		Node n[] = new Node[11];  
		for (int i = 0; i < 11; i++) {
			n[i] = graph.createNode();
		}
		// Creates edges
		graph.createEdge(n[0], n[1]);
		graph.createEdge(n[0], n[2]);
		graph.createEdge(n[0], n[3]);
		graph.createEdge(n[0], n[4]);
		graph.createEdge(n[0], n[10]);
		graph.createEdge(n[5], n[6]);
		graph.createEdge(n[5], n[7]);
		graph.createEdge(n[5], n[8]);
		graph.createEdge(n[5], n[9]);
		graph.createEdge(n[5], n[10]);
		graph.createEdge(n[1], n[0]);
		graph.createEdge(n[2], n[0]);
		graph.createEdge(n[3], n[0]);
		graph.createEdge(n[4], n[0]);
		graph.createEdge(n[10], n[0]);
		graph.createEdge(n[6], n[5]);
		graph.createEdge(n[7], n[5]);
		graph.createEdge(n[8], n[5]);
		graph.createEdge(n[9], n[5]);
		graph.createEdge(n[10], n[5]);
		// Creates node and edge layout
		CircularGraphLayouter graphLayouter = new CircularGraphLayouter();
		graphLayouter.doLayout(graph);
		return new CustomGraph(graph, new HashMap<Edge, Double>(), new HashMap<Node, String>());
	}
	
	public static CustomGraph getSawmillGraph() {
		GraphInputAdapterFactory factory = GraphInputAdapterFactory.getFactory();
		GraphInputAdapter adapter =
				factory.getEdgeListUndirectedGraphInputAdapter(TestConstants.getSawmillTxtInputFileName());
		CustomGraph graph = adapter.readGraph();
		NodeCursor nodes = graph.nodes();
		while(nodes.ok()) {
			Node node = nodes.node();
			NodeRealizer nRealizer = graph.getRealizer(node);
			NodeLabel nameLabel = nRealizer.createNodeLabel();
			nameLabel.setText(graph.getNodeName(node));
			nRealizer.setLabel(nameLabel);
			nodes.next();
		}
		// Creates node and edge layout
		GraphLayouter graphLayouter = new OrganicGraphLayouter();
		graphLayouter.doLayout(graph);
		return graph;
	}	
	
}
