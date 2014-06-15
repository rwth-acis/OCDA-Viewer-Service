package i5.las2peer.services.servicePackage.layouters;

import i5.las2peer.services.servicePackage.graph.Cover;
import i5.las2peer.services.servicePackage.graph.CustomGraph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import y.base.Node;
import y.base.NodeCursor;
import y.view.NodeRealizer;

public class DefaultCoverLayouter implements CoverLayouter {

	// Color which is assigned to overlapping nodes
	private final static Color OVERLAPPING_NODE_COLOR = new Color(128,128,128);
	// Color which is assigned to nodes without a community
	private final static Color NON_MEMBER_NODE_COLOR = new Color(255, 255, 255);
	
	/**
	 * Colors the nodes of the corresponding graph according to the cover's communities
	 */
	@Override
	public void doLayout(Cover cover) {
		CustomGraph graph = cover.getGraph();
		// gets a list with a unique color for each community
		List<Color> colors = getColorCollection(cover.communityCount());
		// assigns each node its color
		NodeCursor it = graph.nodes();
		for(int i=0; i<graph.nodeCount(); i++) {
			Node node = it.node();
			NodeRealizer nRealizer = graph.getRealizer(node);
			List<Integer> communities = cover.getCommunityIndices(node);
			if(communities.size() > 1) {
				// node is overlapping
				nRealizer.setFillColor(OVERLAPPING_NODE_COLOR);
			}
			else if(communities.size() == 1) {
				// node is colored according to community
				Color communityColor = colors.get(communities.get(0));
				nRealizer.setFillColor(communityColor);
			}
			else {
				// node is not a member of any community
				nRealizer.setFillColor(NON_MEMBER_NODE_COLOR);
			}
			it.next();
		}
	}

	// gets num distinct colors with equidistant hues using the HSL color model
	protected List<Color> getColorCollection(int num) {
		List<Color> colors = new ArrayList<Color>();
		for(int i = 0; i < num; i++) {
			// calculates the unique hue
		    float hue = (float)i / (float)num;
		    // sets remaining values and creates an RGB color
		    float saturation = 90;
		    float lightness = 50;
		    Color color = Color.getHSBColor(hue, saturation, lightness);
		    colors.add(color);
		}
		return colors;
	}
	
	
}
