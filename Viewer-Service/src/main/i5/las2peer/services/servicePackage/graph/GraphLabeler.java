package i5.las2peer.services.servicePackage.graph;

import y.base.Edge;
import y.base.EdgeCursor;
import y.base.Node;
import y.base.NodeCursor;
import y.view.EdgeLabel;
import y.view.EdgeRealizer;
import y.view.NodeLabel;
import y.view.NodeRealizer;
import y.view.SmartNodeLabelModel;

public class GraphLabeler {

	public void labelGraph(CustomGraph graph) {
		NodeCursor nodes = graph.nodes();
		while (nodes.ok()) {
			Node node = nodes.node();
			// gets node realizer
			NodeRealizer nRealizer = graph.getRealizer(node);
			// adds name label
			NodeLabel nameLabel = nRealizer.createNodeLabel();
			nameLabel.setText(graph.getNodeName(node));
			SmartNodeLabelModel nameModel = new SmartNodeLabelModel();
			nameLabel
					.setLabelModel(
							nameModel,
							nameModel
									.createDiscreteModelParameter(SmartNodeLabelModel.POSITION_CENTER));
			nRealizer.addLabel(nameLabel);
			nodes.next();
		}
		EdgeCursor edges = graph.edges();
		while (edges.ok()) {
			Edge edge = edges.edge();
			// gets edge realizer
			EdgeRealizer eRealizer = graph.getRealizer(edge);
			// adds weight label
			EdgeLabel weightLabel = eRealizer.createEdgeLabel();
			weightLabel.setText(Double.toString(graph.getEdgeWeight(edge)));
			eRealizer.addLabel(weightLabel);
			edges.next();
		}
	}

}
