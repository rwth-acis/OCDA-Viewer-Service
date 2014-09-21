package i5.las2peer.services.ocd.metrics;

import java.security.InvalidParameterException;
import java.util.Locale;

/**
 * OcdMetric registry.
 * Used for factory instantiation, persistence or other context.
 */
public enum OcdMetricType {

	/*
	 * Each enum constant is instantiated with a corresponding OcdMetric class object and a UNIQUE id.
	 * Once the framework is in use ids must not be changed to avoid corrupting the persisted data.
	 */
	/**
	 * Abstract type usable e.g. for metrics calculated externally.
	 * Cannot be used for metric instantiation.
	 */
	UNDEFINED (OcdMetric.class, 0),
	/**
	 * Abstract type for the algorithm execution time.
	 * Cannot be used for metric instantiation.
	 * An execution time metric entry is automatically added to any cover calculated by a framework algorithm.
	 */
	EXECUTION_TIME (OcdMetric.class, 1),
	/**
	 * Type corresponding to the ExtendedModularityMetric, a statistical measure.
	 */
	EXTENDED_MODULARITY (OcdMetric.class, 2),
	/**
	 * Type corresponding to the ExtendedNormalizedMutualInformationMetric, a knowledge-driven measure.
	 */
	EXTENDED_NORMALIZED_MUTUAL_INFORMATION (OcdMetric.class, 3),
	/**
	 * Type corresponding to the OmegaIndex, a knowledge-driven measure.
	 */
	OMEGA_INDEX (OcdMetric.class, 4);
	
	/**
	 * For persistence and other purposes.
	 */
	private final int id;
	
	/**
	 * The class corresponding to the type.
	 * Abstract types correspond to the OcdMetric interface itself.
	 */
	private final Class<? extends OcdMetric> metricClass;
	
	/**
	 * Creates a new instance.
	 * @param metricClass Defines the metricClass attribute.
	 * @param id Defines the id attribute.
	 */
	private OcdMetricType(Class<? extends OcdMetric> metricClass, int id) {
		this.metricClass = metricClass;
		this.id = id;
	}
	
	/**
	 * Returns the OcdMetric subclass corresponding to the type.
	 * @return The corresponding class.
	 */
	protected Class<? extends OcdMetric> getMetricClass() {
		return this.metricClass;
	}
	
	/**
	 * Returns the unique id of the type.
	 * @return The id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the type corresponding to an id.
	 * @param id The id.
	 * @return The corresponding type.
	 */
	public static OcdMetricType lookupType(int id) {
        for (OcdMetricType type : OcdMetricType.values()) {
            if (id == type.getId()) {
                return type;
            }
        }
        throw new InvalidParameterException();
	}
	
	/**
	 * Returns the type corresponding to an OcdMetric class.
	 * @param metricClass The class.
	 * @return The corresponding type.
	 */
	public static OcdMetricType lookupType(Class<? extends OcdMetric> metricClass) {
        for (OcdMetricType type : OcdMetricType.values()) {
            if (metricClass == type.getMetricClass()) {
                return type;
            }
        }
        throw new InvalidParameterException();
	}
	
	/**
	 * Returns the name of the type written in lower case letters and with any underscores replaced by space characters.
	 */
	@Override
	public String toString() {
		String name = name();
		name = name.replace('_', ' ');
		name = name.toLowerCase(Locale.ROOT);
		return name;
	}
	
}
