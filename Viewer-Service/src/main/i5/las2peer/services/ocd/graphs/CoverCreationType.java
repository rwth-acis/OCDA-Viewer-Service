package i5.las2peer.services.ocd.graphs;

import java.security.InvalidParameterException;
import java.util.Locale;

/**
 * Cover creation method registry. Contains algorithms, ground truth benchmarks and abstract types.
 * Used for factory instantiation, persistence or other context.
 * @author Sebastian
 * 
 * NOTE THAT THIS CLASS WAS DERIVED FROM THE OCD (OVERLAPPING COMMUNITY DETECTION) SERVICE.
 * Since the viewer does not handle any graph creation methods all cover creation types were made abstract
 * and are associated with the CoverCreationMethod interface itself. 
 *
 */
public enum CoverCreationType {

	/*
	 * Each enum constant is instantiated with a corresponding CoverCreationMethod class object (typically a concrete OcdAlgorithm or GroundTruthBenchmark subclass) and a UNIQUE id.
	 * Abstract types that do not correspond to any algorithm are instantiated with the CoverCreationMethod interface itself.
	 * Once the framework is in use ids must not be changed to avoid corrupting the persisted data.
	 */
	/**
	 * Abstract type usable e.g. for importing covers that were calculated externally by other algorithms.
	 * Cannot be used for algorithm instantiation.
	 */
	UNDEFINED (CoverCreationMethod.class, 0),
	/**
	 * Abstract type mainly intended for importing ground truth covers.
	 * Cannot be used for algorithm instantiation.
	 */
	GROUND_TRUTH (CoverCreationMethod.class, 1),
	/**
	 * Type corresponding to the RandomWalkLabelPropagationAlgorithm.
	 */
	RANDOM_WALK_LABEL_PROPAGATION_ALGORITHM (CoverCreationMethod.class, 2),
	/**
	 * Type corresponding to the SpeakerListenerLabelPropagationAlgorithm.
	 */
	SPEAKER_LISTENER_LABEL_PROPAGATION_ALGORITHM (CoverCreationMethod.class, 3),
	/**
	 * Type corresponding to the ExtendedSpeakerListenerLabelPropagationAlgorithm.
	 */
	EXTENDED_SPEAKER_LISTENER_LABEL_PROPAGATION_ALGORITHM(CoverCreationMethod.class, 4),
	/**
	 * Type corresponding to the SskAlgorithm.
	 */
	SSK_ALGORITHM (CoverCreationMethod.class, 5),
	/**
	 * Type corresponding to the LinkCommunitiesAlgorithm.
	 */
	LINK_COMMUNITIES_ALGORITHM (CoverCreationMethod.class, 6),
	/**
	 * Type corresponding to the WeightedLinkCommunitiesAlgorithm.
	 */
	WEIGHTED_LINK_COMMUNITIES_ALGORITHM (CoverCreationMethod.class, 7),
	/**
	 * Type corresponding to the ClizzAlgorithm.
	 */
	CLIZZ_ALGORITHM (CoverCreationMethod.class, 8),
	/**
	 * Type corresponding to the MergingOfOverlappingCommunitiesAlgorithm.
	 */
	MERGING_OF_OVERLAPPING_COMMUNITIES_ALGORITHM(CoverCreationMethod.class, 9),
	/**
	 * Type corresponding to the BinarySearchRandomWalkLabelPropagationAlgorithm.
	 */
	BINARY_SEARCH_RANDOM_WALK_LABEL_PROPAGATION_ALGORITHM(CoverCreationMethod.class, 10),
	/**
	 * Type corresponding to the LfrBenchmark, which is a ground truth benchmark.
	 * Cannot be used for algorithm instantiation.
	 */
	LFR(CoverCreationMethod.class, 11),
	/**
	 * Type corresponding to the NewmanBenchmark, which is a ground truth benchmark.
	 * Cannot be used for algorithm instantiation.
	 */
	NEWMAN(CoverCreationMethod.class, 12);
	
	/**
	 * The class corresponding to the type, typically a concrete OcdAlgorithm or GroundTruthBenchmark subclass.
	 * Abstract types correspond to the CoverCreationMethod interface itself.
	 */
	private final Class<? extends CoverCreationMethod> creationMethodClass;
	
	/**
	 * For persistence and other purposes.
	 */
	private final int id;
	
	/**
	 * Creates a new instance.
	 * @param creationMethodClass Defines the creationMethodClass attribute.
	 * @param id Defines the id attribute.
	 */
	private CoverCreationType(Class<? extends CoverCreationMethod> creationMethodClass, int id) {
		this.creationMethodClass = creationMethodClass;
		this.id = id;
	}
	
	/**
	 * Returns the CoverCreationMethod subclass corresponding to the type.
	 * @return The corresponding class.
	 */
	public Class<? extends CoverCreationMethod> getCreationMethodClass() {
		return this.creationMethodClass;
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
	public static CoverCreationType lookupType(int id) {
        for (CoverCreationType type : CoverCreationType.values()) {
            if (id == type.getId()) {
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
