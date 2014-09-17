package i5.las2peer.services.ocdViewer.utils;

import i5.las2peer.services.ocd.adapters.AdapterException;
import i5.las2peer.services.ocd.graphs.Cover;
import i5.las2peer.services.ocd.graphs.CustomGraph;
import i5.las2peer.services.ocd.utils.RequestHandler;
import i5.las2peer.services.ocdViewer.adapters.visualGraphOutput.VisualGraphOutputAdapter;
import i5.las2peer.services.ocdViewer.adapters.visualGraphOutput.VisualGraphOutputAdapterFactory;
import i5.las2peer.services.ocdViewer.adapters.visualGraphOutput.VisualGraphOutputFormat;

import java.io.StringWriter;
import java.io.Writer;

public class ViewerRequestHandler extends RequestHandler {
	
	private VisualGraphOutputAdapterFactory visualGraphOutputAdapterFactory = new VisualGraphOutputAdapterFactory();
	
	public String writeGraph(CustomGraph graph, VisualGraphOutputFormat outputFormat) throws AdapterException, InstantiationException, IllegalAccessException {
		VisualGraphOutputAdapter adapter = visualGraphOutputAdapterFactory.getInstance(outputFormat);
    	Writer writer = new StringWriter();
    	adapter.setWriter(writer);
		adapter.writeGraph(graph);
		return writer.toString();
	}
	
	public String writeCover(Cover cover, VisualGraphOutputFormat outputFormat) throws AdapterException, InstantiationException, IllegalAccessException {
		return writeGraph(cover.getGraph(), outputFormat);
	}

}
