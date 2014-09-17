package i5.las2peer.services.ocdViewer.layouters;

import java.security.InvalidParameterException;
import java.util.Locale;

/**
 * GraphLayouter registry.
 * Used for factory instantiation, persistence or other context.
 * @author Sebastian
 *
 */
public enum GraphLayoutType {

	/*
	 * Each enum constant is instantiated with a corresponding GraphLayouter class object and a UNIQUE id.
	 * Once the framework is in use ids must not be changed to avoid corrupting the persisted data.
	 */
	ORGANIC (OrganicGraphLayouter.class, 0);
	
	private final Class<? extends GraphLayouter> layouterClass;
	
	private int id;
	
	private GraphLayoutType(Class<? extends GraphLayouter> layouterClass, int id) {
		this.layouterClass = layouterClass;
		this.id = id;
	}
	
	protected Class<? extends GraphLayouter> getLayouterClass() {
		return this.layouterClass;
	}
	
	public int getId() {
		return id;
	}
	
	public static GraphLayoutType lookupType(int id) {
        for (GraphLayoutType type : GraphLayoutType.values()) {
            if (id == type.getId()) {
                return type;
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
