package i5.las2peer.services.ocdViewer.adapters.visualGraphOutput;

import i5.las2peer.services.ocd.adapters.AdapterException;
import i5.las2peer.services.ocd.adapters.OutputAdapter;
import y.view.Graph2D;

public interface VisualGraphOutputAdapter extends OutputAdapter {
	
	/**
	 * Writes a graph and closes the writer.
	 * @param graph The graph to write.
	 * @throws AdapterException
	 */
	public void writeGraph(Graph2D graph) throws AdapterException;
	
}
