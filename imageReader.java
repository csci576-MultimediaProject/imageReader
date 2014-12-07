import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class imageReader {


	static int counter = 0; /* count how many files in a folder */
	
	public static void main(String[] args) {
		// File representing the folder that you select using a FileChooser
		final File dir = new File(args[0]);
		int width = 352;
		int height = 288;
		
		imageReadertest imr = new imageReadertest();

		// array of supported extensions (use a List if you prefer)
		final String[] EXTENSIONS = new String[]{
				"rgb" // and other formats you need
		};
    
		// filter to identify images based on their extensions
		final FilenameFilter IMAGE_FILTER = new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				for (final String ext : EXTENSIONS) {
					if (name.endsWith("." + ext)) {
						return (true);
					}
				}
				return (false);
			}
		};

        if (dir.isDirectory()) { // make sure it's a directory
            for ( final File file : dir.listFiles(IMAGE_FILTER)) {
            	counter++; 
                
            	try {
                	InputStream is = new FileInputStream(file);
                	
                	long len = file.length(); // Calculate input file size
        			//System.out.println("File length : " + len); // Print input file size
        			double framelen = (len / (width * height * 3)); // Calculate input// file's frames
        			//System.out.println("Numbers of frames : " + framelen); // Print// input// file's// frames
        			BufferedImage frameA[] = new BufferedImage[(int) framelen]; // Set// the// frame// array

        			byte[] bytes = new byte[(int) len];

        			int offset = 0;
        			int numRead = 0;
        			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
        				offset += numRead;
        			}
        			
        			for (int i = 0; i < framelen; i++) {
        				BufferedImage img = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        				int ind = 0;
        				for (int y = 0; y < height; y++) {
        					for (int x = 0; x < width; x++) {

        						byte a = 0;
        						byte r = bytes[i * width * height * 3 + ind];
        						byte g = bytes[i * width * height * 3 + ind + height * width];
        						byte b = bytes[i * width * height * 3 + ind + height * width * 2];

        						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
        						img.setRGB(x, y, pix);
        						ind++;
        					}
        				}
        				frameA[i] = img;
        				System.out.println(" width " + i + " : " + frameA[i].getWidth());
                        System.out.println(" height" + i + " : " + frameA[i].getHeight());
        			}

                    // you probably want something more involved here
                    // to display in your UI
                    System.out.println("image: " + file.getName());
                    System.out.println(" size  : " + file.length());
                    imr.display(frameA);
                } catch (final IOException e) {
                    /* handle errors here */
                }
            }
            System.out.println("total file in this folder : " + counter);
        }
    }

	// Function display the image
	public void display(BufferedImage[] img) {
		// Use a label to display the image
		int frameRate = 30;	//frameRate is 30
		JFrame frame = new JFrame();
		frame.setTitle("Original Picture");
		frame.setVisible(true);
		for (int i = 0; i < img.length; i++) {
			JLabel label = new JLabel(new ImageIcon(img[i]));
			frame.getContentPane().add(label, BorderLayout.CENTER);
			frame.pack();

			// Determine the frame rate
			try {
				Thread.sleep(1000 / frameRate);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Determine not to remove last image
			if (i < img.length - 1) {
				frame.getContentPane().removeAll();
			}
		}
	}

	// Function Extract Pixel's each rgb
	public int[] getrgb(BufferedImage img, int x, int y) {

		int loc_x = x;
		int loc_y = y;
		BufferedImage src_img = img;

		int rgb[] = new int[3];

		rgb[0] = (src_img.getRGB(loc_x, loc_y) >> 16) & 0xFF; // handle Red
		rgb[1] = (src_img.getRGB(loc_x, loc_y) >> 8) & 0xFF; // handle Green
		rgb[2] = (src_img.getRGB(loc_x, loc_y)) & 0xFF; // handle Blue

		return rgb;
	}
	
	//Function Histogram
	public int[][] histogram(BufferedImage image){
		
		int[][] hgm = new int[256][3];	//stands for hgm[x axis][r,g,b]
		imageReader imr = new imageReader();
		
		for(int i=0; i < image.getHeight(); i++){
			for(int j=0; j < image.getWidth(); j++){	
				for(int k=0; k < 256; k++){
					if(imr.getrgb(image, j, i)[0]==k){
						hgm[k][0]++;
					}
					if(imr.getrgb(image, j, i)[1]==k){
						hgm[k][1]++;
					}
					if(imr.getrgb(image, j, i)[2]==k){
						hgm[k][2]++;
					}
				}
			}
		}
		
		for(int i=0; i < 256; i++){
			System.out.println("hgmR"+i+":"+hgm[i][0]);
			System.out.println("hgmG"+i+":"+hgm[i][1]);
			System.out.println("hgmB"+i+":"+hgm[i][2]);
		}
		
		return hgm;
	}
	
}
