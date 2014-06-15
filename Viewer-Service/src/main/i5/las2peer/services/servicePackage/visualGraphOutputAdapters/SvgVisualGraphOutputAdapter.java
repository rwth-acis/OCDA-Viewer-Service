package i5.las2peer.services.servicePackage.visualGraphOutputAdapters;

import i5.las2peer.services.servicePackage.graph.CustomGraph;

import java.io.IOException;

import y.io.IOHandler;
import yext.svg.io.SVGIOHandler;

public class SvgVisualGraphOutputAdapter extends AbstractGraphOutputAdapter {
	
	public SvgVisualGraphOutputAdapter(String filename) {
		this.filename = filename;
	}
	
	@Override
	public void writeGraph(CustomGraph graph) {
		try {
			// Writes out the graph using the IOHandler
			IOHandler ioh = new SVGIOHandler();
			ioh.write(graph, filename);
		}
		catch (IOException ioEx) {
			// Failure
		}  
	}
}
