package i5.las2peer.services.ocdViewer.painters;

import i5.las2peer.services.ocd.utils.SimpleFactory;

public class CoverPainterFactory implements SimpleFactory<CoverPainter, CoverPaintingType>{

	@Override
	public CoverPainter getInstance(CoverPaintingType paintingType) throws InstantiationException, IllegalAccessException {
		return paintingType.getPainterClass().newInstance();
	}

}
