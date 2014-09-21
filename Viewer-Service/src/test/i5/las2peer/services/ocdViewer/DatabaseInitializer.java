package i5.las2peer.services.ocdViewer;

import i5.las2peer.services.ocd.adapters.AdapterException;
import i5.las2peer.services.ocd.graphs.Cover;
import i5.las2peer.services.ocd.graphs.CoverId;
import i5.las2peer.services.ocd.graphs.CustomGraph;
import i5.las2peer.services.ocd.graphs.CustomGraphId;
import i5.las2peer.services.ocdViewer.testsUtil.ViewerTestGraphFactory;

import java.io.FileNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Test;

/**
 * This class is designed for database initialization through jUnit tests.
 * All tests will be executed when running the 'database' target of the ant build file.
 */
public class DatabaseInitializer {

	EntityManagerFactory emf = Persistence.createEntityManagerFactory("test");
	
	private final String username = "User";
	
	public CustomGraphId createGraph(CustomGraph graph) {
		graph.setUserName(username);
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.persist(graph);
			tx.commit();
		} catch( RuntimeException e ) {
			if( tx != null && tx.isActive() ) {
				tx.rollback();
			}
			throw e;
		}
		em.close();
		return new CustomGraphId(graph.getId(), username);
	}
	
	public CoverId createCover(Cover cover, String name) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			cover.setName(name);
			em.persist(cover);
			tx.commit();
		} catch( RuntimeException e ) {
			if( tx != null && tx.isActive() ) {
				tx.rollback();
			}
			throw e;
		}
		em.close();
		return new CoverId(cover.getId(), new CustomGraphId(cover.getGraph().getId(), cover.getGraph().getUserName()));
	}
	
	@Test
	public void initDatabase() throws AdapterException, FileNotFoundException {
		Cover cover = ViewerTestGraphFactory.getSlpaDolphinsCover();
		createGraph(cover.getGraph());
		createCover(cover, "SLPA Dolphins");
		cover = ViewerTestGraphFactory.getSlpaSawmillCover();
		createGraph(cover.getGraph());
		createCover(cover, "SLPA Sawmill");
	}

}
