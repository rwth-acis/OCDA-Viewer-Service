package i5.las2peer.services.ocdViewer.adapters.visualGraphOutput;

import java.security.InvalidParameterException;
import java.util.Locale;

/**
 * VisualGraphOutputAdapter registry.
 * Used for factory instantiation, persistence or other context.
 * @author Sebastian
 *
 */
public enum VisualGraphOutputFormat {

	/*
	 * Each enum constant is instantiated with a corresponding VisualGraphOutputAdapter class object and a UNIQUE id.
	 * Once the framework is in use ids must not be changed to avoid corrupting the persisted data.
	 */
	SVG (SvgVisualGraphOutputAdapter.class, 0);
	
	private final Class<? extends VisualGraphOutputAdapter> adapterClass;
	
	private final int id;
	
	private VisualGraphOutputFormat(Class<? extends VisualGraphOutputAdapter> adapterClass, int id) {
		this.adapterClass = adapterClass;
		this.id = id;
	}
	
	protected Class<? extends VisualGraphOutputAdapter> getAdapterClass() {
		return this.adapterClass;
	}
	
	public int getId() {
		return id;
	}
	
	public static VisualGraphOutputFormat lookupFormat(int id) {
        for (VisualGraphOutputFormat format : VisualGraphOutputFormat.values()) {
            if (id == format.getId()) {
                return format;
            }
        }
        throw new InvalidParameterException();
	}

	@Override
	public String toString() {
		String name = name();
		name = name.replace('_', ' ');
		name = name.toLowerCase(Locale.ROOT);
		return name;
	}
	
}

