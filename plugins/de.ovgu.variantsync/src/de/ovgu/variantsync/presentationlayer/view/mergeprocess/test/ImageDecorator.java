package de.ovgu.variantsync.presentationlayer.view.mergeprocess.test;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

public class ImageDecorator extends CompositeImageDescriptor {
	
	private static final ImageDescriptor crossDescriptor = ImageDescriptor.createFromFile(ImageDecorator.class, "error_obj.gif");
	protected void drawCompositeImage(int width, int height) {
//		To draw a composite image, the base image should be 
		// drawn first (first layer) and then the overlay image 
		// (second layer) x

		// Draw the base image using the base image's image data
		drawImage(getImageData(), 0, 0); 

		// Method to create the overlay image data 
		// Get the image data from the Image store or by other means
		ImageData overlayImageData = crossDescriptor.getImageData();

		// Overlaying the icon in the top left corner i.e. x and y 
		// coordinates are both zero
		int xValue = 0;
		int yValue = 0;
		drawImage (overlayImageData, xValue, yValue); 

	}

	protected Point getSize() {
		return new Point(getImageData().width,getImageData().height);
	}

}
