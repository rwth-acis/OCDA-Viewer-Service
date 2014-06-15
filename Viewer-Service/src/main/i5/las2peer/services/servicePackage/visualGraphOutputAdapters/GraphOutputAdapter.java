package i5.las2peer.services.servicePackage.visualGraphOutputAdapters;

import i5.las2peer.services.servicePackage.graph.CustomGraph;

public interface GraphOutputAdapter {

	/**
	 * Returns the path of the output file
	 */
	public String getFilename();
	
	/**
	 * Sets the path for the output file.
	 */
	public void setFilename(String filename);
	
	/**
	 * Writes a graph into the output file.
	 */
	public void writeGraph(CustomGraph graph);
	
}
