package net.jakeri.postagram;

public class PaperType {

	private final int width;
	private final int height;
	// boolean optimizeWidth = true;

	private final int margin;
	private final int space;
	private final int nrx;

	public PaperType(int width, int height, int margin, int space, int nrx) {
		super();
		this.width = width;
		this.height = height;
		this.margin = margin;
		this.space = space;
		this.nrx = nrx;
	}

	public static PaperType createA4space10(int imagesWidth) {

		return new PaperType(595, 842, 10, 10, imagesWidth);
	}

	public static PaperType createA4space0margin10(int imagesWidth) {
		return new PaperType(595, 842, 10, 0, imagesWidth);
	}

	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getMargin() {
		return margin;
	}

	public int getSpace() {
		return space;
	}

	public int getNrx() {
		return nrx;
	}

	public int getCalculatedSize() {
		return (width - (2*margin) - (nrx * space) - space) / nrx;
	}

	public int getCalculatedNry() {
		return (height - space) / (getCalculatedSize() + space);
	}

	public int getCalculatedNrImages() {
		return nrx * getCalculatedNry();
	}

	public int getCalculatedYMargin() {
		return (height - (getCalculatedNry() * (getCalculatedSize() + space) - space)) / 2;
	}

}
