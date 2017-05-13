package i5.las2peer.services.ocdViewer;

import i5.las2peer.api.Context;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import i5.las2peer.security.UserAgent;

import i5.las2peer.services.ocd.adapters.coverInput.CoverInputFormat;
import i5.las2peer.services.ocd.adapters.coverOutput.CoverOutputFormat;
import i5.las2peer.services.ocd.adapters.graphInput.GraphInputFormat;
import i5.las2peer.services.ocd.adapters.graphOutput.GraphOutputFormat;
import i5.las2peer.services.ocd.graphs.Cover;
import i5.las2peer.services.ocd.graphs.CoverCreationLog;
import i5.las2peer.services.ocd.graphs.CoverCreationType;
import i5.las2peer.services.ocd.graphs.CoverId;
import i5.las2peer.services.ocd.graphs.CustomGraph;
import i5.las2peer.services.ocd.graphs.CustomGraphId;
import i5.las2peer.services.ocd.graphs.GraphCreationLog;
import i5.las2peer.services.ocd.graphs.GraphCreationType;
import i5.las2peer.services.ocd.graphs.GraphProcessor;
import i5.las2peer.services.ocd.graphs.GraphType;
import i5.las2peer.services.ocd.metrics.OcdMetricLog;
import i5.las2peer.services.ocd.utils.Error;
import i5.las2peer.services.ocd.utils.ExecutionStatus;
import i5.las2peer.services.ocd.utils.RequestHandler;
import i5.las2peer.services.ocdViewer.adapters.visualOutput.VisualOutputFormat;
import i5.las2peer.services.ocdViewer.layouters.GraphLayoutType;
import i5.las2peer.services.ocdViewer.painters.CoverPaintingType;
import i5.las2peer.services.ocdViewer.utils.LayoutHandler;
import i5.las2peer.services.ocdViewer.utils.ViewerRequestHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;




/**
 * 
 * LAS2peer Service Class
 * 
 * Provides the RESTful interface of the viewer service. Intended for the visualization of covers and graphs,
 * particularly also with respect to the OCD (overlapping community detection service).
 * 
 * The viewer service can be used in combination with the OCD service on a single database.
 * If both services are accessing the same database then all requests that are not directly visualization-related
 * (such as graph or cover import / removal / output) should be performed by the OCD service to ensure proper
 * synchronization of running tasks and proper handling of algorithms / benchmarks / metrics in general.
 * This service only maintains the compatibility with the input / output formats of the OCD service
 * but discards some algorithm / benchmark / metric related data.
 * 
 * @author Sebastian
 *
 */

@ServicePath("ocdViewer")
@Api
@SwaggerDefinition(
		info = @Info(
				title = "OCDViewer",
				version = "1.0",
				description = "A RESTful service for overlapping community detection visualization.",
				termsOfService = "sample-tos.io",
				contact = @Contact(
						name = "Sebastian Krott",
						email = "sebastian.krott@rwth-aachen.de"
				),
				license = @License(
						name = "Apache License 2",
						url = "http://www.apache.org/licenses/LICENSE-2.0"
				)
		))

public class ServiceClass extends RESTService {
	
	/////////////////////////////
	/// Service initialization.
	////////////////////////////
	
	@Override
	protected void initResources() {
		getResourceConfig().register(RootResource.class);		
	}  

	public ServiceClass() {
		setFieldValues();
	}
	
	static {
		RequestHandler reqHandler = new RequestHandler();
		reqHandler.log(Level.INFO, "Overlapping Community Detection Viewer Service started.");
	}
	
	///////////////////////////////////////////////////////////
	//////// ATTRIBUTES
	///////////////////////////////////////////////////////////
	
	/**
	 * The request handler used for simple request-related tasks.
	 */
	private ViewerRequestHandler requestHandler = new ViewerRequestHandler();
	
	/**
	 * The layout handler used for layouting graphs and covers.
	 */
	private LayoutHandler layoutHandler = new LayoutHandler();
	
	
	//////////////////////////////////////////////////////////////////
	///////// REST Service Methods
	//////////////////////////////////////////////////////////////////

	@Path("/")
	public static class RootResource {
		
	
	// instantiate the logger class
	private final L2pLogger logger = L2pLogger.getInstance(ServiceClass.class.getName());
	
	// get access to the service class
	private final ServiceClass service = (ServiceClass) Context.getCurrent().getService();
	
	// get the request handler
	private final ViewerRequestHandler requestHandler = service.requestHandler;	
	
	private final LayoutHandler layoutHandler = service.layoutHandler;

	
	
	//////////////////////////////////////////////////////////////////
	///////// GENERAL
	//////////////////////////////////////////////////////////////////	
	
    
    /**
     * Simple function to validate a user login.
     * Basically it only serves as a "calling point" and does not really validate a user
     * (since this is done previously by LAS2peer itself, the user does not reach this method
     * if he or she is not authenticated).
     * @return A confirmation XML.
     */
    @GET
    @Path("validate")
    public Response validateLogin()
    {
    	try {
    		return Response.ok(requestHandler.writeConfirmationXml()).build();
    	}
    	catch (Exception e) {
    		requestHandler.log(Level.SEVERE, "", e);
    		return requestHandler.writeError(Error.INTERNAL, "Internal system error.");
    	}
    }

	//////////////////////////////////////////////////////////////////
	///////// VISUALIZATION
	//////////////////////////////////////////////////////////////////
    
    /**
     * Returns a visual representation of a cover.
     * @param graphIdStr The id of the graph that the cover is based on.
     * @param coverIdStr The id of the cover.
     * @param graphLayoutTypeStr The name of the layout type defining which graph layouter to use.
     * @param coverPaintingTypeStr The name of the cover painting type defining which cover painter to use.
     * @param visualOutputFormatStr The name of the required output format.
     * @param doLabelNodesStr Optional query parameter. Defines whether nodes will receive labels with their names (TRUE) or not (FALSE).
     * @param doLabelEdgesStr Optional query parameter. Defines whether edges will receive labels with their weights (TRUE) or not (FALSE).
     * @param minNodeSizeStr Optional query parameter. Defines the minimum size of a node. Must be greater than 0.
     * @param maxNodeSizeStr Optional query parameter. Defines the maximum size of a node. Must be at least as high as the defined minimum size.
     * @return The visualization.
     * Or an error xml.
     */
    @GET
    @Path("visualization/cover/{coverId}/graph/{graphId}/outputFormat/{VisualOutputFormat}/layout/{GraphLayoutType}/paint/{CoverPaintingType}")
    public Response getCoverVisualization(@PathParam("graphId") String graphIdStr, @PathParam("coverId") String coverIdStr,
    		@PathParam("GraphLayoutType") String graphLayoutTypeStr,
    		@PathParam("CoverPaintingType") String coverPaintingTypeStr,
    		@PathParam("VisualOutputFormat") String visualOutputFormatStr,
    		@DefaultValue("TRUE") @QueryParam("doLabelNodes") String doLabelNodesStr,
    		@DefaultValue("FALSE") @QueryParam("doLabelEdges") String doLabelEdgesStr,
    		@DefaultValue("20") @QueryParam("minNodeSize") String minNodeSizeStr,
    		@DefaultValue("45") @QueryParam("maxNodeSize") String maxNodeSizeStr) {
    	try {
    		long graphId;
    		String username = ((UserAgent) Context.getCurrent().getMainAgent()).getLoginName();
	    	try {
    			graphId = Long.parseLong(graphIdStr);
    		}
    		catch (Exception e) {
    			requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID, "Graph id is not valid.");
    		}
	    	long coverId;
	    	try {
	    		coverId = Long.parseLong(coverIdStr);
    		}
    		catch (Exception e) {
    			requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID, "Cover id is not valid.");
    		}
	    	double minNodeSize;
	    	try {
	    		minNodeSize = Double.parseDouble(minNodeSizeStr);
	    		if(minNodeSize < 0) {
	    			throw new IllegalArgumentException();
	    		}
    		}
    		catch (Exception e) {
    			requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID, "Min node size is not valid.");
    		}
	    	double maxNodeSize;
	    	try {
	    		maxNodeSize = Double.parseDouble(maxNodeSizeStr);
	    		if(maxNodeSize < minNodeSize) {
	    			throw new IllegalArgumentException();
	    		}
    		}
    		catch (Exception e) {
    			requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID, "Max node size is not valid.");
    		}
	    	VisualOutputFormat format;
	    	GraphLayoutType layout;
	    	boolean doLabelNodes;
	    	boolean doLabelEdges;
	    	try {
	    		layout = GraphLayoutType.valueOf(graphLayoutTypeStr);
	    	}  catch (Exception e) {
	    		requestHandler.log(Level.WARNING, "", e);
	    		return requestHandler.writeError(Error.PARAMETER_INVALID, "Specified layout does not exist.");
	    	}
	    	CoverPaintingType painting;
	    	try {
	    		painting = CoverPaintingType.valueOf(coverPaintingTypeStr);
	    	}  catch (Exception e) {
	    		requestHandler.log(Level.WARNING, "", e);
	    		return requestHandler.writeError(Error.PARAMETER_INVALID, "Specified layout does not exist.");
	    	}
	    	try {
	    		format = VisualOutputFormat.valueOf(visualOutputFormatStr);
	    	}  catch (Exception e) {
	    		requestHandler.log(Level.WARNING, "", e);
	    		return requestHandler.writeError(Error.PARAMETER_INVALID, "Specified visual graph output format does not exist.");
	    	}
	    	try {
	    		doLabelNodes = requestHandler.parseBoolean(doLabelNodesStr);
	    	}  catch (Exception e) {
	    		requestHandler.log(Level.WARNING, "", e);
	    		return requestHandler.writeError(Error.PARAMETER_INVALID, "Label nodes is not a boolean value.");
	    	}
	    	try {
	    		doLabelEdges = requestHandler.parseBoolean(doLabelEdgesStr);
	    	}  catch (Exception e) {
	    		requestHandler.log(Level.WARNING, "", e);
	    		return requestHandler.writeError(Error.PARAMETER_INVALID, "Label edges is not a boolean value.");
	    	}
	    	EntityManager em = requestHandler.getEntityManager();
	    	CustomGraphId gId = new CustomGraphId(graphId, username);
	    	CoverId cId = new CoverId(coverId, gId);
	    	EntityTransaction tx = em.getTransaction();
	    	Cover cover;
	    	try {
				tx.begin();
				cover = em.find(Cover.class, cId);
		    	if(cover == null) {
		    		requestHandler.log(Level.WARNING, "user: " + username + ", " + "Cover does not exist: cover id " + coverId + ", graph id " + graphId);
					return requestHandler.writeError(Error.PARAMETER_INVALID, "Cover does not exist: cover id " + coverId + ", graph id " + graphId);
		    	}
				tx.commit();
			} catch( RuntimeException e ) {
				if( tx != null && tx.isActive() ) {
					tx.rollback();
				}
				throw e;
			}
			em.close();
	    	layoutHandler.doLayout(cover, layout, doLabelNodes, doLabelEdges, minNodeSize, maxNodeSize, painting);
	    	return Response.ok(requestHandler.writeCover(cover, format)).build();
    	}
    	catch (Exception e) {
    		requestHandler.log(Level.SEVERE, "", e);
    		return requestHandler.writeError(Error.INTERNAL, "Internal system error.");
    	}
    }

    /**
     * Returns a visual representation of a graph.
     * @param graphIdStr The id of the graph.
     * @param graphLayoutTypeStr The name of the layout type defining which graph layouter to use.
     * @param visualOutputFormatStr The name of the required output format.
     * @param doLabelNodesStr Optional query parameter. Defines whether nodes will receive labels with their names (TRUE) or not (FALSE).
     * @param doLabelEdgesStr Optional query parameter. Defines whether edges will receive labels with their weights (TRUE) or not (FALSE).
     * @param minNodeSizeStr Optional query parameter. Defines the minimum size of a node. Must be greater than 0.
     * @param maxNodeSizeStr Optional query parameter. Defines the maximum size of a node. Must be at least as high as the defined minimum size.
     * @return The visualization.
     * Or an error xml.
     */
    @GET
    @Path("visualization/graph/{graphId}/outputFormat/{VisualOutputFormat}/layout/{GraphLayoutType}")
    public Response getGraphVisualization(@PathParam("graphId") String graphIdStr, @PathParam("GraphLayoutType") String graphLayoutTypeStr,
    		@PathParam("VisualOutputFormat") String visualOutputFormatStr,
    		@DefaultValue("TRUE") @QueryParam("doLabelNodes") String doLabelNodesStr,
    		@DefaultValue("FALSE") @QueryParam("doLabelEdges") String doLabelEdgesStr,
    		@DefaultValue("20") @QueryParam("minNodeSize") String minNodeSizeStr,
    		@DefaultValue("45") @QueryParam("maxNodeSize") String maxNodeSizeStr) {
    	try {
    		long graphId;
    		String username = ((UserAgent) Context.getCurrent().getMainAgent()).getLoginName();
	    	try {
    			graphId = Long.parseLong(graphIdStr);
    		}
    		catch (Exception e) {
    			requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID, "Graph id is not valid.");
    		}
	    	double minNodeSize;
	    	try {
	    		minNodeSize = Double.parseDouble(minNodeSizeStr);
	    		if(minNodeSize < 0) {
	    			throw new IllegalArgumentException();
	    		}
    		}
    		catch (Exception e) {
    			requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID, "Min node size is not valid.");
    		}
	    	double maxNodeSize;
	    	try {
	    		maxNodeSize = Double.parseDouble(maxNodeSizeStr);
	    		if(maxNodeSize < minNodeSize) {
	    			throw new IllegalArgumentException();
	    		}
    		}
    		catch (Exception e) {
    			requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID, "Max node size is not valid.");
    		}
	    	VisualOutputFormat format;
	    	GraphLayoutType layout;
	    	boolean doLabelNodes;
	    	boolean doLabelEdges;
	    	try {
	    		layout = GraphLayoutType.valueOf(graphLayoutTypeStr);
	    	}  catch (Exception e) {
	    		requestHandler.log(Level.WARNING, "", e);
	    		return requestHandler.writeError(Error.PARAMETER_INVALID, "Specified layout does not exist.");
	    	}
	    	try {
	    		format = VisualOutputFormat.valueOf(visualOutputFormatStr);
	    	}  catch (Exception e) {
	    		requestHandler.log(Level.WARNING, "", e);
	    		return requestHandler.writeError(Error.PARAMETER_INVALID, "Specified visual graph output format does not exist.");
	    	}
	    	try {
	    		doLabelNodes = requestHandler.parseBoolean(doLabelNodesStr);
	    	}  catch (Exception e) {
	    		requestHandler.log(Level.WARNING, "", e);
	    		return requestHandler.writeError(Error.PARAMETER_INVALID, "Label nodes is not a boolean value.");
	    	}
	    	try {
	    		doLabelEdges = requestHandler.parseBoolean(doLabelEdgesStr);
	    	}  catch (Exception e) {
	    		requestHandler.log(Level.WARNING, "", e);
	    		return requestHandler.writeError(Error.PARAMETER_INVALID, "Label edges is not a boolean value.");
	    	}
	    	EntityManager em = requestHandler.getEntityManager();
	    	CustomGraphId id = new CustomGraphId(graphId, username);
	    	EntityTransaction tx = em.getTransaction();
	    	CustomGraph graph;
	    	try {
				tx.begin();
				graph = em.find(CustomGraph.class, id);
		    	if(graph == null) {
		    		requestHandler.log(Level.WARNING, "user: " + username + ", " + "Graph does not exist: graph id " + graphId);
					return requestHandler.writeError(Error.PARAMETER_INVALID, "Graph does not exist: graph id " + graphId);
		    	}
				tx.commit();
			} catch( RuntimeException e ) {
				if( tx != null && tx.isActive() ) {
					tx.rollback();
				}
				throw e;
			}
			em.close();
	    	layoutHandler.doLayout(graph, layout, doLabelNodes, doLabelEdges, minNodeSize, maxNodeSize);
	    	return Response.ok(requestHandler.writeGraph(graph, format)).build();
    	}
    	catch (Exception e) {
    		requestHandler.log(Level.SEVERE, "", e);
    		return requestHandler.writeError(Error.INTERNAL, "Internal system error.");
    	}
    }
    
	//////////////////////////////////////////////////////////////////////////
	//////////// VIEWER-SPECIFIC ENUM LISTINGS
	//////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all graph layout type names.
	 * 
	 * @return The types in a names xml. Or an error xml.
	 */
	@GET
	@Path("graphs/layout/names")
	public Response getLayoutTypeNames() {
		try {
			return Response.ok(requestHandler.writeEnumNames(GraphLayoutType.class)).build();
		} catch (Exception e) {
			requestHandler.log(Level.SEVERE, "", e);
			return requestHandler.writeError(Error.INTERNAL,
					"Internal system error.");
		}
	}
	
	/**
	 * Returns all cover painting type names.
	 * 
	 * @return The types in a names xml. Or an error xml.
	 */
	@GET
	@Path("graphs/painting/names")
	public Response getPaintingTypeNames() {
		try {
			return Response.ok(requestHandler.writeEnumNames(CoverPaintingType.class)).build();
		} catch (Exception e) {
			requestHandler.log(Level.SEVERE, "", e);
			return requestHandler.writeError(Error.INTERNAL,
					"Internal system error.");
		}
	}
	
	/**
	 * Returns all visual output format names.
	 * 
	 * @return The formats in a names xml. Or an error xml.
	 */
	@GET
	@Path("visualization/formats/output/names")
	public Response getVisualizationFormatNames() {
		try {
			return Response.ok(requestHandler.writeEnumNames(VisualOutputFormat.class)).build();
		} catch (Exception e) {
			requestHandler.log(Level.SEVERE, "", e);
			return requestHandler.writeError(Error.INTERNAL,
					"Internal system error.");
		}
	}
    
	//////////////////////////////////////////////////////////////////////////
	////////////GRAPHS
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 * Imports a graph.
	 * 
	 * @param nameStr
	 *            The name for the graph.
	 * @param creationTypeStr
	 *            The type of the creation method used to create the graph.
	 * @param graphInputFormatStr
	 *            The name of the graph input format.
	 * @param doMakeUndirectedStr
	 *            Optional query parameter. Defines whether directed edges shall
	 *            be turned into undirected edges (TRUE) or not.
	 * @param contentStr
	 *            The graph input.
	 * @return A graph id xml.
	 * Or an error xml.
	 */
	@POST
	@Path("graph/name/{name}/creationmethod/{GraphCreationType}/inputFormat/{GraphInputFormat}")
	public Response createGraph(
			@PathParam("name") String nameStr,
			@PathParam("GraphCreationType") String creationTypeStr,
			@PathParam("GraphInputFormat") String graphInputFormatStr,
			@DefaultValue("FALSE") @QueryParam("doMakeUndirected") String doMakeUndirectedStr,
			String contentStr) {
		try {
			String username = ((UserAgent) Context.getCurrent().getMainAgent()).getLoginName();
			GraphInputFormat format;
			CustomGraph graph;
			try {
				format = GraphInputFormat.valueOf(graphInputFormatStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Specified input format does not exist.");
			}
			GraphCreationType benchmarkType;
			try {
				benchmarkType = GraphCreationType.valueOf(creationTypeStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Specified input format does not exist.");
			}
			try {
				graph = requestHandler.parseGraph(contentStr, format);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler
						.writeError(Error.PARAMETER_INVALID,
								"Input graph does not correspond to the specified format.");
			}
			boolean doMakeUndirected;
			try {
				doMakeUndirected = requestHandler
						.parseBoolean(doMakeUndirectedStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Do make undirected ist not a boolean value.");
			}
			graph.setUserName(username);
			graph.setName(URLDecoder.decode(nameStr, "UTF-8"));
			GraphCreationLog log = new GraphCreationLog(benchmarkType,
					new HashMap<String, String>());
			log.setStatus(ExecutionStatus.COMPLETED);
			graph.setCreationMethod(log);
			GraphProcessor processor = new GraphProcessor();
			processor.determineGraphTypes(graph);
			if (doMakeUndirected) {
				Set<GraphType> graphTypes = graph.getTypes();
				if (graphTypes.remove(GraphType.DIRECTED)) {
					processor.makeCompatible(graph, graphTypes);
				}
			}
			EntityManager em = requestHandler.getEntityManager();
			EntityTransaction tx = em.getTransaction();
			try {
				tx.begin();
				em.persist(graph);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			em.close();
			return Response.ok(requestHandler.writeId(graph)).build();
		} catch (Exception e) {
			requestHandler.log(Level.SEVERE, "", e);
			return requestHandler.writeError(Error.INTERNAL,
					"Internal system error.");
		}
	}

	/**
	 * Returns the ids (or meta information) of multiple graphs.
	 * 
	 * @param firstIndexStr
	 *            Optional query parameter. The result list index of the first
	 *            id to return. Defaults to 0.
	 * @param lengthStr
	 *            Optional query parameter. The number of ids to return.
	 *            Defaults to Long.MAX_VALUE.
	 * @param includeMetaStr
	 *            Optional query parameter. If TRUE, instead of the ids the META
	 *            XML of each graph is returned. Defaults to FALSE.
	 * @param executionStatusesStr
	 *            Optional query parameter. If set only those graphs are
	 *            returned whose creation method has one of the given
	 *            ExecutionStatus names. Multiple status names are separated
	 *            using the "-" delimiter.
	 * @return The graphs. Or an error xml.
	 */
	@GET
	@Path("graphs")
	public Response getGraphs(
			@DefaultValue("0") @QueryParam("firstIndex") String firstIndexStr,
			@DefaultValue("") @QueryParam("length") String lengthStr,
			@DefaultValue("FALSE") @QueryParam("includeMeta") String includeMetaStr,
			@DefaultValue("") @QueryParam("executionStatuses") String executionStatusesStr) {
		try {
			String username = ((UserAgent) Context.getCurrent().getMainAgent()).getLoginName();
			List<CustomGraph> queryResults;
			List<Integer> executionStatusIds = new ArrayList<Integer>();
			if (executionStatusesStr != "") {
				try {
					List<String> executionStatusesStrList = requestHandler
							.parseQueryMultiParam(executionStatusesStr);
					for (String executionStatusStr : executionStatusesStrList) {
						ExecutionStatus executionStatus = ExecutionStatus
								.valueOf(executionStatusStr);
						executionStatusIds.add(executionStatus.getId());
					}
				} catch (Exception e) {
					requestHandler.log(Level.WARNING, "user: " + username, e);
					return requestHandler.writeError(Error.PARAMETER_INVALID,
							"Specified execution status does not exist.");
				}
			} else {
				for (ExecutionStatus executionStatus : ExecutionStatus.values()) {
					executionStatusIds.add(executionStatus.getId());
				}
			}
			EntityManager em = requestHandler.getEntityManager();
			String queryStr = "SELECT g FROM CustomGraph g" + " JOIN g."
					+ CustomGraph.CREATION_METHOD_FIELD_NAME + " b"
					+ " WHERE g." + CustomGraph.USER_NAME_FIELD_NAME
					+ " = :username" + " AND b."
					+ GraphCreationLog.STATUS_ID_FIELD_NAME
					+ " IN :execStatusIds";
			TypedQuery<CustomGraph> query = em.createQuery(queryStr,
					CustomGraph.class);
			try {
				int firstIndex = Integer.parseInt(firstIndexStr);
				query.setFirstResult(firstIndex);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"First index is not valid.");
			}
			try {
				if (lengthStr != "") {
					int length = Integer.parseInt(lengthStr);
					query.setMaxResults(length);
				}
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Length is not valid.");
			}
			boolean includeMeta;
			try {
				includeMeta = requestHandler.parseBoolean(includeMetaStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "", e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Include meta is not a boolean value.");
			}
			query.setParameter("username", username);
			query.setParameter("execStatusIds", executionStatusIds);
			queryResults = query.getResultList();
			em.close();
			String responseStr;
			if (includeMeta) {
				responseStr = requestHandler.writeGraphMetas(queryResults);
			} else {
				responseStr = requestHandler.writeGraphIds(queryResults);
			}
			return Response.ok(responseStr).build();
		} catch (Exception e) {
			requestHandler.log(Level.SEVERE, "", e);
			return requestHandler.writeError(Error.INTERNAL,
					"Internal system error.");
		}
	}

	/**
	 * Returns a graph in a specified output format.
	 * 
	 * @param graphIdStr
	 *            The graph id.
	 * @param graphOuputFormatStr
	 *            The name of the graph output format.
	 * @return The graph output. Or an error xml.
	 */
	@GET
	@Produces("text/plain")
	@Path("graph/{graphId}/outputFormat/{GraphOutputFormat}")
	public Response getGraph(@PathParam("graphId") String graphIdStr,
			@PathParam("GraphOutputFormat") String graphOuputFormatStr) {
		try {
			long graphId;
			String username = ((UserAgent) Context.getCurrent().getMainAgent()).getLoginName();
			GraphOutputFormat format;
			try {
				graphId = Long.parseLong(graphIdStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Graph id is not valid.");
			}
			try {
				format = GraphOutputFormat.valueOf(graphOuputFormatStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Specified graph output format does not exist.");
			}
			EntityManager em = requestHandler.getEntityManager();
			CustomGraphId id = new CustomGraphId(graphId, username);
			EntityTransaction tx = em.getTransaction();
			CustomGraph graph;
			try {
				tx.begin();
				graph = em.find(CustomGraph.class, id);
				if (graph == null) {
					requestHandler.log(Level.WARNING, "user: " + username
							+ ", " + "Graph does not exist: graph id "
							+ graphId);
					return requestHandler.writeError(Error.PARAMETER_INVALID,
							"Graph does not exist: graph id " + graphId);
				}
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			em.close();
			return Response.ok(requestHandler.writeGraph(graph, format)).build();
		} catch (Exception e) {
			requestHandler.log(Level.SEVERE, "", e);
			return requestHandler.writeError(Error.INTERNAL,
					"Internal system error.");
		}
	}

	/**
	 * Deletes a graph. All covers based on the graph are removed as well. If a
	 * benchmark is currently calculating the graph the execution is terminated.
	 * If an algorithm is currently calculating a cover based on the graph it is
	 * terminated. If a metric is currently running on a cover based on the grap
	 * it is terminated.
	 * 
	 * @param graphIdStr
	 *            The graph id.
	 * @return A confirmation xml. Or an error xml.
	 */
	@DELETE
	@Path("graph/{graphId}")
	public Response deleteGraph(@PathParam("graphId") String graphIdStr) {
		try {
			long graphId;
			String username = ((UserAgent) Context.getCurrent().getMainAgent()).getLoginName();
			try {
				graphId = Long.parseLong(graphIdStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Graph id is not valid.");
			}
			EntityManager em = requestHandler.getEntityManager();
			CustomGraphId id = new CustomGraphId(graphId, username);
			CustomGraph graph;
			EntityTransaction tx = em.getTransaction();
			try {
				tx.begin();
				graph = em.find(CustomGraph.class, id);
				if (graph == null) {
					requestHandler.log(Level.WARNING, "user: " + username
							+ ", " + "Graph does not exist: graph id "
							+ graphId);
					return requestHandler.writeError(Error.PARAMETER_INVALID,
							"Graph does not exist: graph id " + graphId);
				}
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			List<Cover> queryResults;
			String queryStr = "SELECT c from Cover c" + " JOIN c."
					+ Cover.GRAPH_FIELD_NAME + " g" + " WHERE g."
					+ CustomGraph.USER_NAME_FIELD_NAME + " = :username"
					+ " AND g." + CustomGraph.ID_FIELD_NAME + " = " + graphId;
			TypedQuery<Cover> query = em.createQuery(queryStr, Cover.class);
			query.setParameter("username", username);
			queryResults = query.getResultList();
			for (Cover cover : queryResults) {
				tx = em.getTransaction();
				try {
					tx.begin();
					em.remove(cover);
					tx.commit();
				} catch (RuntimeException e) {
					if (tx != null && tx.isActive()) {
						tx.rollback();
					}
					throw e;
				}
			}
			try {
				tx = em.getTransaction();
				tx.begin();
				em.remove(graph);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			return Response.ok(requestHandler.writeConfirmationXml()).build();
		} catch (Exception e) {
			requestHandler.log(Level.SEVERE, "", e);
			return requestHandler.writeError(Error.INTERNAL,
					"Internal system error.");
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	// //////////COVERS
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Imports a cover for an existing graph.
	 * 
	 * @param graphIdStr
	 *            The id of the graph that the cover is based on.
	 * @param nameStr
	 *            A name for the cover.
	 * @param creationTypeStr
	 *            The name of the creation method the cover was created by.
	 * @param coverInputFormatStr
	 *            The name of the input format.
	 * @param contentStr
	 *            The cover input.
	 * @return A cover id xml. Or an error xml.
	 */
	@POST
	@Path("cover/graph/{graphId}/name/{name}/creationmethod/{CoverCreationType}/inputFormat/{CoverInputFormat}")
	public Response createCover(@PathParam("graphId") String graphIdStr,
			@PathParam("name") String nameStr,
			@PathParam("CoverCreationType") String creationTypeStr,
			@PathParam("CoverInputFormat") String coverInputFormatStr,
			String contentStr) {
		try {
			String username = ((UserAgent) Context.getCurrent().getMainAgent()).getLoginName();
			long graphId;
			try {
				graphId = Long.parseLong(graphIdStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Graph id is not valid.");
			}
			CoverInputFormat format;
			try {
				format = CoverInputFormat.valueOf(coverInputFormatStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Specified cover input format does not exist.");
			}
			CoverCreationType algorithmType;
			try {
				algorithmType = CoverCreationType.valueOf(creationTypeStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Specified algorithm does not exist.");
			}
			Set<GraphType> graphTypes;
			graphTypes = new HashSet<GraphType>();
			CoverCreationLog log = new CoverCreationLog(algorithmType,
					new HashMap<String, String>(), graphTypes);
			log.setStatus(ExecutionStatus.COMPLETED);
			EntityManager em = requestHandler.getEntityManager();
			EntityTransaction tx = em.getTransaction();
			CustomGraphId id = new CustomGraphId(graphId, username);
			Cover cover;
			try {
				tx.begin();
				CustomGraph graph = em.find(CustomGraph.class, id);
				if (graph == null) {
					requestHandler.log(Level.WARNING, "user: " + username
							+ ", " + "Graph does not exist: graph id "
							+ graphId);
					return requestHandler.writeError(Error.PARAMETER_INVALID,
							"Graph does not exist: graph id " + graphId);
				}
				try {
					cover = requestHandler
							.parseCover(contentStr, graph, format);
				} catch (Exception e) {
					requestHandler.log(Level.WARNING, "user: " + username, e);
					return requestHandler
							.writeError(Error.PARAMETER_INVALID,
									"Input cover does not correspond to the specified format.");
				}
				cover.setCreationMethod(log);
				cover.setName(URLDecoder.decode(nameStr, "UTF-8"));
				em.persist(cover);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			em.close();
			return Response.ok(requestHandler.writeId(cover)).build();
		} catch (Exception e) {
			requestHandler.log(Level.SEVERE, "", e);
			return requestHandler.writeError(Error.INTERNAL,
					"Internal system error.");
		}
	}

	/**
	 * Returns the ids (or meta information) of multiple covers.
	 * 
	 * @param firstIndexStr
	 *            Optional query parameter. The result list index of the first
	 *            id to return. Defaults to 0.
	 * @param lengthStr
	 *            Optional query parameter. The number of ids to return.
	 *            Defaults to Long.MAX_VALUE.
	 * @param includeMetaStr
	 *            Optional query parameter. If TRUE, instead of the ids the META
	 *            XML of each graph is returned. Defaults to FALSE.
	 * @param executionStatusesStr
	 *            Optional query parameter. If set only those covers are
	 *            returned whose creation method status corresponds to one of
	 *            the given ExecutionStatus names. Multiple status names are
	 *            separated using the "-" delimiter.
	 * @param metricExecutionStatusesStr
	 *            Optional query parameter. If set only those covers are
	 *            returned that have a corresponding metric log with a status
	 *            corresponding to one of the given ExecutionStatus names.
	 *            Multiple status names are separated using the "-" delimiter.
	 * @param graphIdStr
	 * 			  Optional query parameter. If set only those covers are returned
	 * 			  that are based on the corresponding graph.
	 * @return The covers. Or an error xml.
	 */
	@GET
	@Path("covers")
	public Response getCovers(
			@DefaultValue("0") @QueryParam("firstIndex") String firstIndexStr,
			@DefaultValue("") @QueryParam("length") String lengthStr,
			@DefaultValue("FALSE") @QueryParam("includeMeta") String includeMetaStr,
			@DefaultValue("") @QueryParam("executionStatuses") String executionStatusesStr,
			@DefaultValue("") @QueryParam("metricExecutionStatuses") String metricExecutionStatusesStr,
			@DefaultValue("") @QueryParam("graphId") String graphIdStr) {
		try {
			String username = ((UserAgent) Context.getCurrent().getMainAgent()).getLoginName();
			long graphId = 0;
			if (graphIdStr != "") {
				try {
					graphId = Long.parseLong(graphIdStr);
				} catch (Exception e) {
					requestHandler.log(Level.WARNING, "user: " + username, e);
					return requestHandler.writeError(Error.PARAMETER_INVALID,
							"Graph id is not valid.");
				}
			}
			List<Integer> executionStatusIds = new ArrayList<Integer>();
			if (executionStatusesStr != "") {
				try {
					List<String> executionStatusesStrList = requestHandler
							.parseQueryMultiParam(executionStatusesStr);
					for (String executionStatusStr : executionStatusesStrList) {
						ExecutionStatus executionStatus = ExecutionStatus
								.valueOf(executionStatusStr);
						executionStatusIds.add(executionStatus.getId());
					}
				} catch (Exception e) {
					requestHandler.log(Level.WARNING, "user: " + username, e);
					return requestHandler.writeError(Error.PARAMETER_INVALID,
							"Specified execution status does not exist.");
				}
			} else {
				for (ExecutionStatus executionStatus : ExecutionStatus.values()) {
					executionStatusIds.add(executionStatus.getId());
				}
			}
			List<Integer> metricExecutionStatusIds = new ArrayList<Integer>();
			if (metricExecutionStatusesStr != "") {
				try {
					List<String> metricExecutionStatusesStrList = requestHandler
							.parseQueryMultiParam(metricExecutionStatusesStr);
					for (String executionStatusStr : metricExecutionStatusesStrList) {
						ExecutionStatus executionStatus = ExecutionStatus
								.valueOf(executionStatusStr);
						metricExecutionStatusIds.add(executionStatus.getId());
					}
				} catch (Exception e) {
					requestHandler.log(Level.WARNING, "user: " + username, e);
					return requestHandler
							.writeError(Error.PARAMETER_INVALID,
									"Specified metric execution status does not exist.");
				}
			}
			List<Cover> queryResults;
			EntityManager em = requestHandler.getEntityManager();
			/*
			 * Query
			 */
			String queryStr = "SELECT c from Cover c" + " JOIN c."
					+ Cover.GRAPH_FIELD_NAME + " g" + " JOIN c."
					+ Cover.CREATION_METHOD_FIELD_NAME + " a";
			if (metricExecutionStatusesStr != "") {
				queryStr += " JOIN c." + Cover.METRICS_FIELD_NAME + " m";
			}
			queryStr += " WHERE g." + CustomGraph.USER_NAME_FIELD_NAME
					+ " = :username" + " AND a."
					+ CoverCreationLog.STATUS_ID_FIELD_NAME
					+ " IN :execStatusIds";
			if (metricExecutionStatusesStr != "") {
				queryStr += " AND m." + OcdMetricLog.STATUS_ID_FIELD_NAME
						+ " IN :metricExecStatusIds";
			}
			if (graphIdStr != "") {
				queryStr += " AND g." + CustomGraph.ID_FIELD_NAME + " = "
						+ graphId;
			}
			/*
			 * Gets each cover only once.
			 */
			queryStr += " GROUP BY c";
			TypedQuery<Cover> query = em.createQuery(queryStr, Cover.class);
			try {
				int firstIndex = Integer.parseInt(firstIndexStr);
				query.setFirstResult(firstIndex);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"First index is not valid.");
			}
			try {
				if (lengthStr != "") {
					int length = Integer.parseInt(lengthStr);
					query.setMaxResults(length);
				}
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Length is not valid.");
			}
			boolean includeMeta;
			try {
				includeMeta = requestHandler.parseBoolean(includeMetaStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "", e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Include meta is not a boolean value.");
			}
			query.setParameter("username", username);
			query.setParameter("execStatusIds", executionStatusIds);
			if (metricExecutionStatusesStr != "") {
				query.setParameter("metricExecStatusIds",
						metricExecutionStatusIds);
			}
			queryResults = query.getResultList();
			em.close();
			String responseStr;
			if (includeMeta) {
				responseStr = requestHandler.writeCoverMetas(queryResults);
			} else {
				responseStr = requestHandler.writeCoverIds(queryResults);
			}
			return Response.ok(responseStr).build();
		} catch (Exception e) {
			requestHandler.log(Level.SEVERE, "", e);
			return requestHandler.writeError(Error.INTERNAL,
					"Internal system error.");
		}
	}

	/**
	 * Returns a cover in a specified format.
	 * 
	 * @param graphIdStr
	 *            The id of the graph that the cover is based on.
	 * @param coverIdStr
	 *            The cover id.
	 * @param coverOutputFormatStr
	 *            The cover output format.
	 * @return The cover output. Or an error xml.
	 */
	@GET
	@Produces("text/plain")
	@Path("cover/{coverId}/graph/{graphId}/outputFormat/{CoverOutputFormat}")
	public Response getCover(@PathParam("graphId") String graphIdStr,
			@PathParam("coverId") String coverIdStr,
			@PathParam("CoverOutputFormat") String coverOutputFormatStr) {
		try {
			String username = ((UserAgent) Context.getCurrent().getMainAgent()).getLoginName();
			long graphId;
			try {
				graphId = Long.parseLong(graphIdStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Graph id is not valid.");
			}
			long coverId;
			try {
				coverId = Long.parseLong(coverIdStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Cover id is not valid.");
			}
			CoverOutputFormat format;
			try {
				format = CoverOutputFormat.valueOf(coverOutputFormatStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Specified cover output format does not exist.");
			}
			EntityManager em = requestHandler.getEntityManager();
			CustomGraphId gId = new CustomGraphId(graphId, username);
			CoverId cId = new CoverId(coverId, gId);
			/*
			 * Finds cover
			 */
			EntityTransaction tx = em.getTransaction();
			Cover cover;
			try {
				tx.begin();
				cover = em.find(Cover.class, cId);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			if (cover == null) {
				requestHandler.log(Level.WARNING, "user: " + username + ", "
						+ "Cover does not exist: cover id " + coverId
						+ ", graph id " + graphId);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Cover does not exist: cover id " + coverId
								+ ", graph id " + graphId);
			}
			return Response.ok(requestHandler.writeCover(cover, format)).build();
		} catch (Exception e) {
			requestHandler.log(Level.SEVERE, "", e);
			return requestHandler.writeError(Error.INTERNAL,
					"Internal system error.");
		}
	}

	/**
	 * Deletes a cover. If the cover is still being created by an algorithm, the
	 * algorithm is terminated. If the cover is still being created by a ground
	 * truth benchmark, the benchmark is terminated and the corresponding graph
	 * is deleted as well. If metrics are running on the cover, they are
	 * terminated.
	 * 
	 * @param coverIdStr
	 *            The cover id.
	 * @param graphIdStr
	 *            The graph id of the graph corresponding the cover.
	 * @return A confirmation xml. Or an error xml.
	 */
	@DELETE
	@Path("cover/{coverId}/graph/{graphId}")
	public Response deleteCover(@PathParam("coverId") String coverIdStr,
			@PathParam("graphId") String graphIdStr) {
		try {
			String username = ((UserAgent) Context.getCurrent().getMainAgent()).getLoginName();
			long graphId;
			try {
				graphId = Long.parseLong(graphIdStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Graph id is not valid.");
			}
			long coverId;
			try {
				coverId = Long.parseLong(coverIdStr);
			} catch (Exception e) {
				requestHandler.log(Level.WARNING, "user: " + username, e);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Cover id is not valid.");
			}
			EntityManager em = requestHandler.getEntityManager();
			CustomGraphId gId = new CustomGraphId(graphId, username);
			CoverId cId = new CoverId(coverId, gId);
			/*
			 * Checks whether cover is being calculated by a ground truth
			 * benchmark and if so deletes the graph instead.
			 */
			EntityTransaction tx = em.getTransaction();
			Cover cover;
			try {
				tx.begin();
				cover = em.find(Cover.class, cId);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			if (cover == null) {
				requestHandler.log(Level.WARNING, "user: " + username + ", "
						+ "Cover does not exist: cover id " + coverId
						+ ", graph id " + graphId);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Cover does not exist: cover id " + coverId
								+ ", graph id " + graphId);
			}
			/*
			 * Deletes the cover.
			 */
			tx = em.getTransaction();
			try {
				tx.begin();
				cover = em.find(Cover.class, cId);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			if (cover == null) {
				requestHandler.log(Level.WARNING, "user: " + username + ", "
						+ "Cover does not exist: cover id " + coverId
						+ ", graph id " + graphId);
				return requestHandler.writeError(Error.PARAMETER_INVALID,
						"Cover does not exist: cover id " + coverId
								+ ", graph id " + graphId);
			}
			/*
			 * Removes cover
			 */
			tx = em.getTransaction();
			try {
				tx.begin();
				em.remove(cover);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			em.close();
			return Response.ok(requestHandler.writeConfirmationXml()).build();
		} catch (Exception e) {
			requestHandler.log(Level.SEVERE, "", e);
			return requestHandler.writeError(Error.INTERNAL,
					"Internal system error.");
		}
	}
	
//////////////////////////////////////////////////////////////////////////
//////////// GENERAL ENUM LISTINGS
//////////////////////////////////////////////////////////////////////////
	
	/**
     * Returns all graph creation type names.
     * @return The types in a names xml.
     * Or an error xml.
     */
    @GET
    @Path("graphs/creationmethods/names")
    public Response getGraphCreationMethodNames()
    {
    	try {
			return Response.ok(requestHandler.writeEnumNames(GraphCreationType.class)).build();
    	}
    	catch (Exception e) {
    		requestHandler.log(Level.SEVERE, "", e);
    		return requestHandler.writeError(Error.INTERNAL, "Internal system error.");
    	}
    }
    
    /**
     * Returns all graph input format names.
     * @return The formats in a names xml.
     * Or an error xml.
     */
    @GET
    @Path("graphs/formats/input/names")
    public Response getGraphInputFormatNames()
    {
    	try {
			return Response.ok(requestHandler.writeEnumNames(GraphInputFormat.class)).build();
    	}
    	catch (Exception e) {
    		requestHandler.log(Level.SEVERE, "", e);
    		return requestHandler.writeError(Error.INTERNAL, "Internal system error.");
    	}
    }
    
    /**
     * Returns all graph output format names.
     * @return The formats in a names xml.
     * Or an error xml.
     */
    @GET
    @Path("graphs/formats/output/names")
    public Response getGraphOutputFormatNames()
    {
    	try {
			return Response.ok(requestHandler.writeEnumNames(GraphOutputFormat.class)).build();
    	}
    	catch (Exception e) {
    		requestHandler.log(Level.SEVERE, "", e);
    		return requestHandler.writeError(Error.INTERNAL, "Internal system error.");
    	}
    }
    
    /**
     * Returns all cover output format names.
     * @return The formats in a names xml.
     * Or an error xml.
     */
    @GET
    @Path("covers/formats/output/names")
    public Response getCoverOutputFormatNames()
    {
    	try {
			return Response.ok(requestHandler.writeEnumNames(CoverOutputFormat.class)).build();
    	}
    	catch (Exception e) {
    		requestHandler.log(Level.SEVERE, "", e);
    		return requestHandler.writeError(Error.INTERNAL, "Internal system error.");
    	}
    }
    
    /**
     * Returns all cover input format names.
     * @return The formats in a names xml.
     * Or an error xml.
     */
    @GET
    @Path("covers/formats/input/names")
    public Response getCoverInputFormatNames()
    {
    	try {
			return Response.ok(requestHandler.writeEnumNames(CoverInputFormat.class)).build();
    	}
    	catch (Exception e) {
    		requestHandler.log(Level.SEVERE, "", e);
    		return requestHandler.writeError(Error.INTERNAL, "Internal system error.");
    	}
    }
	}
}
