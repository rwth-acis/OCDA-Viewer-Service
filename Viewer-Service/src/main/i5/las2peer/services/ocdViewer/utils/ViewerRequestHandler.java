package i5.las2peer.services.ocdViewer.utils;

import i5.las2peer.services.ocd.adapters.AdapterException;
import i5.las2peer.services.ocd.graphs.Cover;
import i5.las2peer.services.ocd.graphs.CustomGraph;
import i5.las2peer.services.ocd.utils.RequestHandler;
import i5.las2peer.services.ocdViewer.adapters.visualOutput.VisualOutputAdapter;
import i5.las2peer.services.ocdViewer.adapters.visualOutput.VisualOutputAdapterFactory;
import i5.las2peer.services.ocdViewer.adapters.visualOutput.VisualOutputFormat;

import java.io.StringWriter;
import java.io.Writer;

/**
 * Manages different request-related tasks for the Service Class particularly for the viewer service.
 * Mainly in charge of simple IO tasks and of creating entity managers for persistence purposes.
 * @author Sebastian
 *
 */
public class ViewerRequestHandler extends RequestHandler {
	
	/**
	 * The factory used for creating visual output adapters.
	 */
	private VisualOutputAdapterFactory visualOutputAdapterFactory = new VisualOutputAdapterFactory();
	
	/**
	 * Creates a visual graph output in a specified format.
	 * @param graph The graph.
	 * @param outputFormat The format.
	 * @return The visual graph output.
	 * @throws AdapterException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public String writeGraph(CustomGraph graph, VisualOutputFormat outputFormat) throws AdapterException, InstantiationException, IllegalAccessException {
		VisualOutputAdapter adapter = visualOutputAdapterFactory.getInstance(outputFormat);
    	Writer writer = new StringWriter();
    	adapter.setWriter(writer);
		adapter.writeGraph(graph);
		return writer.toString();
	}
	
	/**
	 * Creates a visual cover output  in a specified format.
	 * @param cover The cover.
	 * @param outputFormat The format.
	 * @return The visual cover output.
	 * @throws AdapterException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public String writeCover(Cover cover, VisualOutputFormat outputFormat) throws AdapterException, InstantiationException, IllegalAccessException {
		return writeGraph(cover.getGraph(), outputFormat);
	}

}
