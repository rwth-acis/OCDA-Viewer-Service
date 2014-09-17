package i5.las2peer.services.ocdViewer.adapters.visualGraphOutput;

import i5.las2peer.services.ocd.utils.SimpleFactory;

public class VisualGraphOutputAdapterFactory implements SimpleFactory<VisualGraphOutputAdapter, VisualGraphOutputFormat>{

	@Override
	public VisualGraphOutputAdapter getInstance(VisualGraphOutputFormat outputFormat) throws InstantiationException, IllegalAccessException {
		return outputFormat.getAdapterClass().newInstance();
	}

}
