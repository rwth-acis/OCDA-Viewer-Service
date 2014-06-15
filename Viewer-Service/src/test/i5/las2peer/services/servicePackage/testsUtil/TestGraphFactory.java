package i5.las2peer.services.servicePackage.testsUtil;

import i5.las2peer.services.servicePackage.graph.Cover;
import i5.las2peer.services.servicePackage.graph.CustomGraph;
import i5.las2peer.services.servicePackage.graph.GraphDataProvider;
import i5.las2peer.services.servicePackage.graphInputAdapters.GraphInputAdapter;
import i5.las2peer.services.servicePackage.graphInputAdapters.GraphInputAdapterFactory;
import i5.las2peer.services.servicePackage.layouters.CircularGraphLayouter;
import i5.las2peer.services.servicePackage.layouters.DefaultCoverLayouter;
import i5.las2peer.services.servicePackage.layouters.GraphLayouter;
import i5.las2peer.services.servicePackage.layouters.OrganicGraphLayouter;

import java.util.HashMap;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

import y.base.Edge;
import y.base.EdgeMap;
import y.base.Node;
import y.base.NodeCursor;
import y.base.NodeMap;
import y.view.EdgeLabel;
import y.view.EdgeRealizer;
import y.view.NodeLabel;
import y.view.NodeRealizer;
import y.view.SmartNodeLabelModel;

public class TestGraphFactory {
	
	public static CustomGraph getTinyCircleGraph() {
		// Creates new graph
		CustomGraph graph = new CustomGraph();
		// Registers node map for node names
		NodeMap nodeNames = graph.createNodeMap();
		graph.addDataProvider(GraphDataProvider.NODE_NAMES, nodeNames);
		// Creates membership map
		Matrix memberships = new Basic2DMatrix(10, 2);
		// Creates 10 nodes.
		Node n[] = new Node[10];  
		for (int i = 0; i < 10; i++) {
			n[i] = graph.createNode();
			// adds node map entries 
			nodeNames.set(n[i], "id: " + i);
			memberships.set(i, i%2, 1);
			// gets node realizer
			NodeRealizer nRealizer = graph.getRealizer(n[i]);
			// adds name label
			NodeLabel nameLabel = nRealizer.createNodeLabel();
			nameLabel.setText(nodeNames.get(n[i]).toString());
			SmartNodeLabelModel nameModel = new SmartNodeLabelModel();
			nameLabel.setLabelModel(nameModel, nameModel.createDiscreteModelParameter(SmartNodeLabelModel.POSITION_CENTER));
			nRealizer.addLabel(nameLabel);
		}
		// Sets one overlapping (0) and one non-member node (1)
		memberships.set(0, 0, 0.5);
		memberships.set(0, 1, 0.5);
		memberships.set(1, 0, 0);
		memberships.set(1, 1, 0);
		// Creates cover
		Cover cover = new Cover(graph, memberships);
		// Registers edge map for weights
		EdgeMap weights = graph.createEdgeMap();
		// Creates 10 edges forming a cycle
		Edge e[] = new Edge[10];
		graph.addDataProvider(GraphDataProvider.WEIGHTS, weights);
		for (int i = 0; i < 10; i++) {
			e[i] = graph.createEdge(n[i], n[(i+1)%10]);
			weights.set(e[i], 1);
			// gets edge realizer
			EdgeRealizer eRealizer = graph.getRealizer(e[i]);
			// adds weight label
			EdgeLabel weightLabel = eRealizer.createEdgeLabel();
			weightLabel.setText(weights.get(e[i]).toString());
			eRealizer.addLabel(weightLabel);
		}
		// Creates node and edge layout
		CircularGraphLayouter graphLayouter = new CircularGraphLayouter();
		graphLayouter.doLayout(graph);
		// Creates cover layout
		DefaultCoverLayouter coverLayouter = new DefaultCoverLayouter();
		coverLayouter.doLayout(cover);
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
