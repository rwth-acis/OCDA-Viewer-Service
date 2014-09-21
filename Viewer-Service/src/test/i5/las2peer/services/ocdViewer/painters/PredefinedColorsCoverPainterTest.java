package i5.las2peer.services.ocdViewer.painters;

import i5.las2peer.services.ocd.adapters.AdapterException;
import i5.las2peer.services.ocd.graphs.Cover;
import i5.las2peer.services.ocdViewer.adapters.visualOutput.SvgVisualOutputAdapter;
import i5.las2peer.services.ocdViewer.layouters.GraphLayoutType;
import i5.las2peer.services.ocdViewer.testsUtil.ViewerTestConstants;
import i5.las2peer.services.ocdViewer.testsUtil.ViewerTestGraphFactory;
import i5.las2peer.services.ocdViewer.utils.LayoutHandler;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

public class PredefinedColorsCoverPainterTest {

	@Test
	public void testOnSawmill() throws AdapterException, IOException, InstantiationException, IllegalAccessException {
		Cover cover = ViewerTestGraphFactory.getSlpaSawmillCover();
		LayoutHandler handler = new LayoutHandler();
		handler.doLayout(cover, GraphLayoutType.ORGANIC, true, false, 20, 45, CoverPaintingType.PREDEFINED_COLORS);
		SvgVisualOutputAdapter adapter = new SvgVisualOutputAdapter();
		adapter.setWriter(new FileWriter(ViewerTestConstants.slpaSawmillSvgOutputPath));
		adapter.writeGraph(cover.getGraph());
	}
	
	@Test
	public void testOnDolphins() throws AdapterException, IOException, InstantiationException, IllegalAccessException {
		Cover cover = ViewerTestGraphFactory.getSlpaDolphinsCover();
		LayoutHandler handler = new LayoutHandler();
		handler.doLayout(cover, GraphLayoutType.ORGANIC, true, false, 20, 45, CoverPaintingType.PREDEFINED_COLORS);
		SvgVisualOutputAdapter adapter = new SvgVisualOutputAdapter();
		adapter.setWriter(new FileWriter(ViewerTestConstants.slpaDolphinsSvgOutputPath));
		adapter.writeGraph(cover.getGraph());
	}

}
