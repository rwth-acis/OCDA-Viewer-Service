package i5.las2peer.services.ocdViewer.utils;

import i5.las2peer.services.ocd.graphs.Cover;
import i5.las2peer.services.ocd.graphs.CustomGraph;
import i5.las2peer.services.ocd.graphs.GraphType;
import i5.las2peer.services.ocdViewer.layouters.GraphLayouter;
import i5.las2peer.services.ocdViewer.layouters.GraphLayouterFactory;
import i5.las2peer.services.ocdViewer.layouters.GraphLayoutType;
import i5.las2peer.services.ocdViewer.painters.CoverPainter;
import i5.las2peer.services.ocdViewer.painters.CoverPainterFactory;
import i5.las2peer.services.ocdViewer.painters.CoverPaintingType;

import java.awt.Color;
import java.util.List;

import y.base.Edge;
import y.base.EdgeCursor;
import y.base.Node;
import y.base.NodeCursor;
import y.view.Arrow;
import y.view.DefaultGraph2DRenderer;
import y.view.EdgeLabel;
import y.view.EdgeRealizer;
import y.view.Graph2DView;
import y.view.NodeLabel;
import y.view.NodeRealizer;
import y.view.ShapeNodeRealizer;
import y.view.SmartNodeLabelModel;

public class LayoutHandler {

	private CoverPainterFactory coverPainterFactory = new CoverPainterFactory();
	
	private GraphLayouterFactory graphLayouterFactory = new GraphLayouterFactory();
	
	public void doLayout(CustomGraph graph, GraphLayoutType layoutType, boolean doLabelNodes, boolean doLabelEdges, 
			double minNodeSize, double maxNodeSize) throws InstantiationException, IllegalAccessException {
		setLayoutDefaults(graph, minNodeSize, maxNodeSize);
		labelGraph(graph, doLabelNodes, doLabelEdges);
		GraphLayouter layouter = graphLayouterFactory.getInstance(layoutType);
		layouter.doLayout(graph);
		setViewDefaults(new Graph2DView(graph));
	}
	
	/**
	 * Sets the layout defaults for a graph.
	 * @param graph
	 */
	private void setLayoutDefaults(CustomGraph graph, double minNodeSize, double maxNodeSize) {
		NodeCursor nodes = graph.nodes();
		Node node;
		/*
		 * Node size scaling factor
		 */
		double minDegree = graph.getMinWeightedInDegree();
		double maxDegree = graph.getMaxWeightedInDegree();
		double scalingFactor = (maxNodeSize - minNodeSize) / (maxDegree - minDegree);
		while(nodes.ok()) {
			node = nodes.node();
			ShapeNodeRealizer nRealizer = new ShapeNodeRealizer(graph.getRealizer(node));
			graph.setRealizer(node, nRealizer);
			nRealizer.setShapeType(ShapeNodeRealizer.ELLIPSE);
			double curNodeSize = minNodeSize + (graph.getWeightedInDegree(node) - minDegree) * scalingFactor;
			nRealizer.setSize(curNodeSize, curNodeSize);
			nodes.next();
		}
		EdgeCursor edges = graph.edges();
		Edge edge;
		while(edges.ok()) {
			edge = edges.edge();
			EdgeRealizer eRealizer = graph.getRealizer(edge);
			if(graph.isOfType(GraphType.DIRECTED)) {
				eRealizer.setArrow(Arrow.STANDARD);
			}
			edges.next();
		}
	}
	
	public void doLayout(Cover cover, GraphLayoutType layoutType, boolean doLabelNodes, boolean doLabelEdges, 
			double minNodeSize, double maxNodeSize, CoverPaintingType paintingType) throws InstantiationException, IllegalAccessException {
		CustomGraph graph = cover.getGraph();
		setLayoutDefaults(graph, minNodeSize, maxNodeSize);
		labelGraph(graph, doLabelNodes, doLabelEdges);
		GraphLayouter layouter = graphLayouterFactory.getInstance(layoutType);
		layouter.doLayout(graph);
		CoverPainter painter = coverPainterFactory.getInstance(paintingType);
		painter.doPaint(cover);
		paintNodes(cover);
		setViewDefaults(new Graph2DView(graph));
	}
	
	private void setViewDefaults(Graph2DView view) {
		DefaultGraph2DRenderer renderer = new DefaultGraph2DRenderer();
		view.setGraph2DRenderer(renderer);
		renderer.setDrawEdgesFirst(true);
		view.fitContent();
	}
	
	private void labelGraph(CustomGraph graph, boolean doLabelNodes, boolean doLabelEdges) {
		if(doLabelNodes) {
			NodeCursor nodes = graph.nodes();
			while (nodes.ok()) {
				Node node = nodes.node();
				// gets node realizer
				NodeRealizer nRealizer = graph.getRealizer(node);
				// adds name label
				NodeLabel nameLabel = nRealizer.createNodeLabel();
				nameLabel.setText(graph.getNodeName(node));
				SmartNodeLabelModel nameModel = new SmartNodeLabelModel();
				nameLabel.setLabelModel(nameModel, nameModel.createDiscreteModelParameter(SmartNodeLabelModel.POSITION_CENTER));
				nRealizer.addLabel(nameLabel);
				nodes.next();
			}
		}
		if(doLabelEdges) {
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
	
	private void paintNodes(Cover cover) {
		CustomGraph graph = cover.getGraph();
		NodeCursor nodes = graph.nodes();
		float[] curColorCompArray = new float[4];
		float[] colorCompArray;
		Node node;
		while(nodes.ok()) {
			colorCompArray = new float[4];
			node = nodes.node();
			List<Integer> communityIndices = cover.getCommunityIndices(node);
			for(int index : communityIndices) {
				Color comColor = cover.getCommunityColor(index);
				comColor.getRGBComponents(curColorCompArray);
				for(int i=0; i<4; i++) {
					colorCompArray[i] += curColorCompArray[i] * cover.getBelongingFactor(node, index);
				}
			}
			NodeRealizer nRealizer = graph.getRealizer(node);
			nRealizer.setFillColor(new Color(colorCompArray[0], colorCompArray[1], colorCompArray[2], colorCompArray[3]));
			nodes.next();
		}
	}

}
