
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;


public class imageReader {

	static int counter = 0; /* count how many files in a folder */

	public static void main(String[] args) {
		
		final File dir = new File(args[0]);
		int width = 352;
		int height = 288;
		int row = 0;
		imageReader imr = new imageReader();

		/* array of supported extensions */
		final String[] EXTENSIONS = new String[] { 
				"rgb" /* and other formats if needed */
		};

		/* filter to identify images based on their extensions */
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

		if (dir.isDirectory()) { /* count how many files are there in the folder */
			for (final File file : dir.listFiles(IMAGE_FILTER)) {
				counter++;
			}
			//System.out.println("total file in this folder : " + counter);
		}

		if((counter % 10) == 0) /* use counter to control gridlayout format*/
			row = counter/10; 
		else
			row = (counter/10) + 1; 
		
		JFrame frame = new JFrame("Image Collage"); /* new a frame to display the files in the folder */
		//frame.setVisible(true);
		JPanel panel = new JPanel(new GridLayout( row , 10)); /* add a panel to the frame (using gridlayout) */
		
		for (final File file : dir.listFiles(IMAGE_FILTER)) { /*forloop: the total number of files */

			try {
				InputStream is = new FileInputStream(file);

				long len = file.length(); /* Calculate input file size */
				// System.out.println("File length : " + len); /* Print input file size */
				double framelen = (len / (width * height * 3)); /* Calculate inputfile's frames */
				// System.out.println("Numbers of frames : " + framelen); /* Print input file's frames */
				BufferedImage frameA[] = new BufferedImage[(int) framelen]; /* Set the frame array */

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
					//System.out.println(" width " + i + " : " + frameA[i].getWidth());
					//System.out.println(" height" + i + " : " + frameA[i].getHeight());
				}

				// System.out.println("image: " + file.getName());
				// System.out.println(" size  : " + file.length());
				// imr.histogram(frameA[0]);
				
				/*Obtain each frame's histogram*/
				for(int i = 0; i < frameA.length; i++){
					frameHtg[i] = imr.histogram(frameA[i]);
				}
				
				/*Transfer frameHtg from 3-way into 2-way array with value y. */
				int[][] frameHtgT = new int[counter][256];
				for(int i=0; i < 300; i++){
					for(int j=0; j <256; j++){
						frameHtgT[i][j] = frameHtg[i][j][0];
					}
				}
				
				/*Sent data array after histogram to do cluster*/
				Kmeans km = new Kmeans();

				if (frameA.length > 1){ /* if the file is a video, create its first frame as button in Image Collage display */
					JButton frameVideobutton = new JButton(new StretchIcon(frameA[0]));
					frameVideobutton.setBorder(BorderFactory.createEmptyBorder());
					frameVideobutton.setContentAreaFilled(true);
					panel.add(frameVideobutton);
					frame.getContentPane().add(panel, BorderLayout.CENTER);
					frame.setSize(800,700);
					frame.setVisible(true);
					
					frameVideobutton.addActionListener(new ActionListener() { /* if button click, call display function to play the clicked video*/
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							imr.display(frameA);
						}
			        }); 
				}else{
					/* if the file is an image, create it as a button in Image Collage display */
					JButton framebutton = new JButton(new StretchIcon(frameA[0]));
					framebutton.setBorder(BorderFactory.createEmptyBorder());
					framebutton.setContentAreaFilled(true);
					panel.add(framebutton);
					frame.getContentPane().add(panel, BorderLayout.CENTER);
					frame.setSize(800, 700);
					frame.setVisible(true);

					framebutton.addActionListener(new ActionListener() { /* if button click, call display function to display the clicked original image*/
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							imr.display(frameA);
						}
					});

				}
				
			} catch (final IOException e) {
				/* handle errors here */
			}
		}
		

	}
	
	/* Function display the image */
	public void display(BufferedImage[] img) {
		/* Use a label to display the image */
		
		int frameRate = 30; /* frameRate is 30 */
		JFrame frame = new JFrame();
		frame.setTitle(" Original Image/Video ");
		frame.setVisible(true);
		
		for (int i = 0; i < img.length; i++) {
			JLabel label = new JLabel(new ImageIcon(img[i]));
			frame.getContentPane().add(label, BorderLayout.CENTER);
			frame.pack();

			/* Determine the frame rate */
			try {
				Thread.sleep(1000 / frameRate);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/* Determine not to remove last image */
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

	// Function Histogram
	public int[][] histogram(BufferedImage image) {

		int[][] hgm = new int[256][3]; // stands for hgm[x axis][r,g,b]
		int[][] yuv = new int[256][3]; // stands for yuv[x axis][y,u,v]
		imageReader imr = new imageReader();

		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				for (int k = 0; k < 256; k++) {
					if (imr.getrgb(image, j, i)[0] == k) {
						hgm[k][0]++;
					}
					if (imr.getrgb(image, j, i)[1] == k) {
						hgm[k][1]++;
					}
					if (imr.getrgb(image, j, i)[2] == k) {
						hgm[k][2]++;
					}
				}
			}
		}
		
		/*//Check output with rgb format
		for (int i = 0; i < 256; i++) {
			System.out.println("hgmR" + i + ":" + hgm[i][0]);
			System.out.println("hgmG" + i + ":" + hgm[i][1]);
			System.out.println("hgmB" + i + ":" + hgm[i][2]);
		}
		*/
		
		//Calculate with YUV
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				for (int k = 0; k < 256; k++) {
					int y = (int)((imr.getrgb(image, j, i)[0]*0.299)+imr.getrgb(image, j, i)[1]*0.587+imr.getrgb(image, j, i)[2]*0.114);
					int u = (int)((imr.getrgb(image, j, i)[0]*(-0.147))+imr.getrgb(image, j, i)[1]*(-0.289)+imr.getrgb(image, j, i)[2]*0.436);
					int v = (int)((imr.getrgb(image, j, i)[0]*0.615)+imr.getrgb(image, j, i)[1]*(-0.515)+imr.getrgb(image, j, i)[2]*(-0.100));
					if (y == k) {
						yuv[k][0]++;
					}
					if (u == k) {
						yuv[k][1]++;
					}
					if (v == k) {
						yuv[k][2]++;
					}
				}
			}
		}
		
		/*//Check output with yuv format 
		for (int i = 0; i < 256; i++) {
			System.out.println("yuvY" + i + ":" + yuv[i][0]);
			System.out.println("yuvU" + i + ":" + yuv[i][1]);
			System.out.println("yuvV" + i + ":" + yuv[i][2]);
		}
		*/
		
		return yuv;
	}

}