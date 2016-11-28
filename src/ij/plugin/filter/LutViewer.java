/*******************************************************************************
 * Copyright 2015 Kaito Ii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ij.plugin.filter;
import ij.IJ;
import ij.ImagePlus;
import ij.LookUpTable;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.process.ImageProcessor;
import ij.text.TextWindow;

import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/** Displays the active image's look-up table. */
public class LutViewer implements PlugInFilter {

	/** The imp. */
	ImagePlus imp;
	
	/* (non-Javadoc)
	 * @see ij.plugin.filter.PlugInFilter#setup(java.lang.String, ij.ImagePlus)
	 */
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL-DOES_RGB+NO_UNDO+NO_CHANGES;
	}

	/* (non-Javadoc)
	 * @see ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)
	 */
	public void run(ImageProcessor ip) {
		int xMargin = 35;
		int yMargin = 20;
		int width = 256;
		int height = 128;
		int x, y, x1, y1, x2, y2;
		int imageWidth, imageHeight;
		int barHeight = 12;
		boolean isGray;
		double scale;

        ip = imp.getChannelProcessor();
        IndexColorModel cm = (IndexColorModel)ip.getColorModel();
        LookUpTable lut = new LookUpTable(cm);
		int mapSize = lut.getMapSize();
		byte[] reds = lut.getReds();
		byte[] greens = lut.getGreens();
		byte[] blues = lut.getBlues();
        isGray = lut.isGrayscale();

		imageWidth = width + 2*xMargin;
		imageHeight = height + 3*yMargin;
		Image img = IJ.getInstance().createImage(imageWidth, imageHeight);
		Graphics g = img.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, imageWidth, imageHeight);
		g.setColor(Color.black);
		g.drawRect(xMargin, yMargin, width, height);

		scale = 256.0/mapSize;
		if (isGray)
			g.setColor(Color.black);
		else
			g.setColor(Color.red);
		x1 = xMargin;
		y1 = yMargin + height - (reds[0]&0xff)/2;
		for (int i = 1; i<256; i++) {
			x2 = xMargin + i;
			y2 = yMargin + height - (reds[(int)(i/scale)]&0xff)/2;
			g.drawLine(x1, y1, x2, y2);
			x1 = x2;
			y1 = y2;
		}

		if (!isGray) {
			g.setColor(Color.green);
			x1 = xMargin;
			y1 = yMargin + height - (greens[0]&0xff)/2;
			for (int i = 1; i<256; i++) {
				x2 = xMargin + i;
				y2 = yMargin + height - (greens[(int)(i/scale)]&0xff)/2;
				g.drawLine(x1, y1, x2, y2);
				x1 = x2;
				y1 = y2;
			}
		}

		if (!isGray) {
			g.setColor(Color.blue);
			x1 = xMargin;
			y1 = yMargin + height - (blues[0]&0xff)/2;
			for (int i = 1; i<255; i++) {
				x2 = xMargin + i;
				y2 = yMargin + height - (blues[(int)(i/scale)]&0xff)/2;
				g.drawLine(x1, y1, x2, y2);
				x1 = x2;
				y1 = y2;
			}
		}

		x = xMargin;
		y = yMargin + height + 2;
		lut.drawColorBar(g, x, y, 256, barHeight);
		
		y += barHeight + 15;
		g.setColor(Color.black);
		g.drawString("0", x - 4, y);
		g.drawString(""+(mapSize-1), x + width - 10, y);
		g.drawString("255", 7, yMargin + 4);
		g.dispose();
		
        ImagePlus imp = new ImagePlus("Look-Up Table", img);
        //imp.show();
        new LutWindow(imp, new ImageCanvas(imp), ip);
    }

} // LutViewer class

class LutWindow extends ImageWindow implements ActionListener {

	private Button button;
	private ImageProcessor ip;

	LutWindow(ImagePlus imp, ImageCanvas ic, ImageProcessor ip) {
		super(imp, ic);
		this.ip = ip;
		addPanel();
	}

	void addPanel() {
		Panel panel = new Panel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		button = new Button(" List... ");
		button.addActionListener(this);
		panel.add(button);
		add(panel);
		pack();
	}
	
	public void actionPerformed(ActionEvent e) {
		Object b = e.getSource();
		if (b==button)
			list(ip);
	}

	void list(ImageProcessor ip) {
		IndexColorModel icm = (IndexColorModel)ip.getColorModel();
		int size = icm.getMapSize();
		byte[] r = new byte[size];
		byte[] g = new byte[size];
		byte[] b = new byte[size];
		icm.getReds(r); 
		icm.getGreens(g); 
		icm.getBlues(b);
		ArrayList list = new ArrayList();
		String headings = "Index\tRed\tGreen\tBlue";
		for (int i=0; i<size; i++)
			list.add(i+"\t"+(r[i]&255)+"\t"+(g[i]&255)+"\t"+(b[i]&255));
		TextWindow tw = new TextWindow("LUT", headings, list, 250, 400);
	}

} // LutWindow class

