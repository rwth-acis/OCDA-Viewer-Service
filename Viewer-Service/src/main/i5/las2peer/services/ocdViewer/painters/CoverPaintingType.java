package i5.las2peer.services.ocdViewer.painters;

import java.security.InvalidParameterException;
import java.util.Locale;

/**
 * CoverPainter registry.
 * Used for factory instantiation, persistence or other context.
 * @author Sebastian
 *
 */
public enum CoverPaintingType {

	/*
	 * Each enum constant is instantiated with a corresponding CoverPainter class object and a UNIQUE id.
	 * Once the framework is in use ids must not be changed to avoid corrupting the persisted data.
	 */
	PREDEFINED_COLORS (PredefinedColorsCoverPainter.class, 0),
	RANDOM_COLORS (RandomColorsCoverPainter.class, 1);
	
	private final int id;
	
	private final Class<? extends CoverPainter> painterClass;
	
	private CoverPaintingType(Class<? extends CoverPainter> painterClass, int id) {
		this.painterClass = painterClass;
		this.id = id;
	}
	
	protected Class<? extends CoverPainter> getPainterClass() {
		return this.painterClass;
	}
	
	public int getId() {
		return id;
	}
	
	public static CoverPaintingType lookupType(int id) {
        for (CoverPaintingType type : CoverPaintingType.values()) {
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
