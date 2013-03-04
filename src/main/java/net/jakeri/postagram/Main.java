package net.jakeri.postagram;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class Main {

	private final InstagramData instagramData ;
	private final PdfGenerator pdfGenerator = new PdfGenerator();
	
	public Main(String accessToken) {
		
		//TODO use guice.
		instagramData = new InstagramData(accessToken);
	}
	
	public void createPDF(String instagramUsername, PaperType type, OutputStream os) {
		int imagesToFetch = type.getCalculatedNrImages();
		System.out.println("Need " + imagesToFetch + " images.");
		List<Image> fetchImages = instagramData.fetchImages(instagramUsername, imagesToFetch);
		
		if (imagesToFetch == fetchImages.size()) {
			pdfGenerator.generate(type, fetchImages, os);
		} else {
			System.out.println("Not enough images. No PDF for you.");
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length != 4) {
			System.out.println("Need 4 arguments, [username] [images width] [filename] [instagram accesstoken]");
			System.exit(2);
		}
		
		try {
			String userName = args[0];
			int imagesWidth = Integer.parseInt(args[1]);
			String fileName  = args[2];
			String accessToken  = args[3];
			FileOutputStream fos = new FileOutputStream(fileName);
			
			Main m = new Main(accessToken);
			m.createPDF(userName, PaperType.createA4space0margin10(imagesWidth), fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
