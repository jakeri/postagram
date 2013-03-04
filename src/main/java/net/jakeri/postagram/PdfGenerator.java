package net.jakeri.postagram;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;

public class PdfGenerator {

	public void generate(PaperType type, List<Image> images, OutputStream os) {

		try {
			PDDocument doc = new PDDocument();

			//Only support for A4 right now.
			PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
			doc.addPage(page);

			//TODO workaround for stupid stream bug in pdfbox
			ArrayList<PDJpeg> pdImages = new ArrayList<PDJpeg>();
			for (Image img : images) {
				
				InputStream is = new URL(img.getUrl()).openStream();
				PDJpeg ximage = new PDJpeg(doc,is);

				pdImages.add(ximage);
				is.close();
			}
			
			PDPageContentStream contentStream = new PDPageContentStream(doc, page);
			int inr = 0;
			for (int y = (type.getCalculatedNry() - 1); y >= 0; y--) {
				for (int x = 0; x < type.getNrx(); x++) {

					contentStream.drawXObject(pdImages.get(inr),
							(x * (type.getCalculatedSize() + type.getSpace()))
									+ type.getMargin(),
							(y * (type.getCalculatedSize() + type.getSpace()))
									+ type.getCalculatedYMargin(),
							type.getCalculatedSize(), type.getCalculatedSize());
					inr++;
				}
			}
			
			contentStream.close();
			doc.save(os);

			doc.close();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		} catch (COSVisitorException e) {
			throw new RuntimeException(e);
		}

	}
}
