package i5.las2peer.services.ocdViewer;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.GET;
import i5.las2peer.restMapper.annotations.Path;
import i5.las2peer.restMapper.annotations.PathParam;
import i5.las2peer.restMapper.annotations.Produces;
import i5.las2peer.restMapper.annotations.QueryParam;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.security.UserAgent;
import i5.las2peer.services.ocd.graphs.Cover;
import i5.las2peer.services.ocd.graphs.CoverId;
import i5.las2peer.services.ocd.graphs.CustomGraph;
import i5.las2peer.services.ocd.graphs.CustomGraphId;
import i5.las2peer.services.ocd.utils.Error;
import i5.las2peer.services.ocd.utils.RequestHandler;
import i5.las2peer.services.ocdViewer.adapters.visualGraphOutput.VisualGraphOutputFormat;
import i5.las2peer.services.ocdViewer.layouters.GraphLayoutType;
import i5.las2peer.services.ocdViewer.painters.CoverPaintingType;
import i5.las2peer.services.ocdViewer.utils.LayoutHandler;
import i5.las2peer.services.ocdViewer.utils.ViewerRequestHandler;

import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.parsers.ParserConfigurationException;



/**
 * 
 * LAS2peer Service
 * 
 * This is a template for a very basic LAS2peer service
 * that uses the LAS2peer Web-Connector for RESTful access to it.
 * 
 * @author Peter de Lange
 *
 */
@Path("ocdViewer")
@Produces("text/xml")
@Version("0.1")
public class ServiceClass extends Service {
	
	/*
	 * Init service.
	 */
	static {
		RequestHandler reqHandler = new RequestHandler();
		reqHandler.log(Level.INFO, "Overlapping Community Detection Viewer Service started.");
	}
	
	private ViewerRequestHandler requestHandler = new ViewerRequestHandler();
	
	private LayoutHandler layoutHandler = new LayoutHandler();
	
	
	/**
	 * This method is needed for every RESTful application in LAS2peer.
	 * 
	 * @return the mapping
	 */
    public String getRESTMapping()
    {
        String result="";
        try {
            result= RESTMapper.getMethodsAsXML(this.getClass());
        } catch (Exception e) {

            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 
     * Simple function to validate a user login.
     * Basically it only serves as a "calling point" and does not really validate a user
     *(since this is done previously by LAS2peer itself, the user does not reach this method
     * if he or she is not authenticated).
     * @throws ParserConfigurationException 
     * 
     */
    @GET
    @Path("validate")
    public String validateLogin()
    {
    	try {
    		return requestHandler.writeConfirmationXml();
    	}
    	catch (Exception e) {
    		requestHandler.log(Level.SEVERE, "", e);
    		return requestHandler.writeError(Error.INTERNAL, "Internal system error.");
    	}
    }

    @GET
    @Path("visualization/cover/{coverId}/graph/{graphId}/outputFormat/{VisualGraphOutputFormat}/layout/{LayoutType}/paint/{CoverPaintingType}")
    public String getCoverVisualization(@PathParam("graphId") String graphIdStr, @PathParam("coverId") String coverIdStr,
    		@PathParam("LayoutType") String layoutTypeStr,
    		@PathParam("CoverPaintingType") String coverPaintingTypeStr,
    		@PathParam("VisualGraphOutputFormat") String visualGraphOutputFormatStr,
    		@QueryParam(name="doLabelNodes", defaultValue = "TRUE") String doLabelNodesStr,
    		@QueryParam(name="doLabelEdges", defaultValue = "FALSE") String doLabelEdgesStr,
    		@QueryParam(name="minNodeSize", defaultValue = "20") String minNodeSizeStr,
    		@QueryParam(name="maxNodeSize", defaultValue = "45") String maxNodeSizeStr) {
    	try {
    		long graphId;
    		String username = ((UserAgent) getActiveAgent()).getLoginName();
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
	    	VisualGraphOutputFormat format;
	    	GraphLayoutType layout;
	    	boolean doLabelNodes;
	    	boolean doLabelEdges;
	    	try {
	    		layout = GraphLayoutType.valueOf(layoutTypeStr);
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
	    		format = VisualGraphOutputFormat.valueOf(visualGraphOutputFormatStr);
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
	    	return requestHandler.writeCover(cover, format);
    	}
    	catch (Exception e) {
    		requestHandler.log(Level.SEVERE, "", e);
    		return requestHandler.writeError(Error.INTERNAL, "Internal system error.");
    	}
    }
    
    @GET
    @Path("visualization/graph/{graphId}/outputFormat/{VisualGraphOutputFormat}/layout/{LayoutType}")
    public String getGraphVisualization(@PathParam("graphId") String graphIdStr, @PathParam("LayoutType") String layoutTypeStr,
    		@PathParam("VisualGraphOutputFormat") String visualGraphOutputFormatStr,
    		@QueryParam(name="doLabelNodes", defaultValue = "TRUE") String doLabelNodesStr,
    		@QueryParam(name="doLabelEdges", defaultValue = "FALSE") String doLabelEdgesStr,
    		@QueryParam(name="minNodeSize", defaultValue = "20") String minNodeSizeStr,
    		@QueryParam(name="maxNodeSize", defaultValue = "45") String maxNodeSizeStr) {
    	try {
    		long graphId;
    		String username = ((UserAgent) getActiveAgent()).getLoginName();
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
	    	VisualGraphOutputFormat format;
	    	GraphLayoutType layout;
	    	boolean doLabelNodes;
	    	boolean doLabelEdges;
	    	try {
	    		layout = GraphLayoutType.valueOf(layoutTypeStr);
	    	}  catch (Exception e) {
	    		requestHandler.log(Level.WARNING, "", e);
	    		return requestHandler.writeError(Error.PARAMETER_INVALID, "Specified layout does not exist.");
	    	}
	    	try {
	    		format = VisualGraphOutputFormat.valueOf(visualGraphOutputFormatStr);
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
	    	return requestHandler.writeGraph(graph, format);
    	}
    	catch (Exception e) {
    		requestHandler.log(Level.SEVERE, "", e);
    		return requestHandler.writeError(Error.INTERNAL, "Internal system error.");
    	}
    }
    
}
