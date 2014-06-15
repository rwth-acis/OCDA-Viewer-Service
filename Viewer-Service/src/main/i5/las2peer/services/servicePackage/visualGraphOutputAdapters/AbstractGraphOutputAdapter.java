package i5.las2peer.services.servicePackage.visualGraphOutputAdapters;


public abstract class AbstractGraphOutputAdapter implements GraphOutputAdapter {

	protected String filename;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
