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
package ij.gui;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.plugin.frame.Recorder;
import ij.process.FloatPolygon;

import java.awt.Graphics;
import java.awt.Rectangle;

// TODO: Auto-generated Javadoc
/** Elliptical region of interest. */
public class EllipseRoi extends PolygonRoi {
	
	/** The Constant vertices. */
	private static final int vertices = 72;
	
	/** The default ratio. */
	private static double defaultRatio = 0.6;
	
	/** The ystart. */
	private double xstart, ystart;
	
	/** The aspect ratio. */
	private double aspectRatio = defaultRatio;
	
	/** The handle. */
	private int[] handle = {0, vertices/4, vertices/2, vertices/2+vertices/4};

	/**
	 * Instantiates a new ellipse roi.
	 *
	 * @param x1 the x 1
	 * @param y1 the y 1
	 * @param x2 the x 2
	 * @param y2 the y 2
	 * @param aspectRatio the aspect ratio
	 */
	public EllipseRoi(double x1, double y1, double x2, double y2, double aspectRatio) {
		super(new float[vertices], new float[vertices], vertices, FREEROI);
		if (aspectRatio<0.0) aspectRatio = 0.0;
		if (aspectRatio>1.0) aspectRatio = 1.0;
		this.aspectRatio = aspectRatio;
		makeEllipse(x1, y1, x2, y2);
		state = NORMAL;
	}

	/**
	 * Instantiates a new ellipse roi.
	 *
	 * @param sx the sx
	 * @param sy the sy
	 * @param imp the imp
	 */
	public EllipseRoi(int sx, int sy, ImagePlus imp) {
		super(sx, sy, imp);
		type = FREEROI;
		xstart = ic.offScreenXD(sx);
		ystart = ic.offScreenYD(sy);
	}

	/* (non-Javadoc)
	 * @see ij.gui.PolygonRoi#draw(java.awt.Graphics)
	 */
	public void draw(Graphics g) {
		super.draw(g);
		int size2 = HANDLE_SIZE/2;
		if (!overlay) {
			for (int i=0; i<handle.length; i++)
				drawHandle(g, xp2[handle[i]]-size2, yp2[handle[i]]-size2);
		}
	}

	/* (non-Javadoc)
	 * @see ij.gui.PolygonRoi#grow(int, int)
	 */
	protected void grow(int sx, int sy) {
		double x1 = xstart;
		double y1 = ystart;
		double x2 = ic.offScreenXD(sx);
		double y2 = ic.offScreenYD(sy);
		makeEllipse(x1, y1, x2, y2);
		imp.draw();
	}
		
	/**
	 * Make ellipse.
	 *
	 * @param x1 the x 1
	 * @param y1 the y 1
	 * @param x2 the x 2
	 * @param y2 the y 2
	 */
	void makeEllipse(double x1, double y1, double x2, double y2) {
		double centerX = (x1 + x2)/2.0;
		double centerY = (y1 + y2)/2.0;
		double dx = x2 - x1;
		double dy = y2 - y1;
		double major = Math.sqrt(dx*dx + dy*dy);
		double minor = major*aspectRatio;
		double phiB = Math.atan2(dy, dx);         
		double alpha = phiB*180.0/Math.PI;
		nPoints = 0;
		for (int i=0; i<vertices; i++) {
			double degrees = i*360.0/vertices;
			double beta1 = degrees/180.0*Math.PI;
			dx = Math.cos(beta1)*major/2.0;
			dy = Math.sin(beta1)*minor/2.0;
			double beta2 = Math.atan2(dy, dx);
			double rad = Math.sqrt(dx*dx + dy*dy);
			double beta3 = beta2+ alpha/180.0*Math.PI;
			double dx2 = Math.cos(beta3)*rad;
			double dy2 = Math.sin(beta3)*rad;
			xpf[nPoints] = (float)(centerX+dx2);
			ypf[nPoints] = (float)(centerY+dy2);
			nPoints++;
		}
		makePolygonRelative();
		cachedMask = null;
	}

	/**
	 * Make polygon relative.
	 */
	void makePolygonRelative() {
		FloatPolygon poly = new FloatPolygon(xpf, ypf, nPoints);
		Rectangle r = poly.getBounds();
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
		bounds = poly.getFloatBounds();
		float xbase = (float)bounds.getX();
		float ybase = (float)bounds.getY();
		for (int i=0; i<nPoints; i++) {
			xpf[i] = xpf[i]-xbase;
			ypf[i] = ypf[i]-ybase;
		}
	}
	
	/* (non-Javadoc)
	 * @see ij.gui.PolygonRoi#handleMouseUp(int, int)
	 */
	protected void handleMouseUp(int screenX, int screenY) {
		if (state==CONSTRUCTING) {
            addOffset();
			finishPolygon();
			if (Recorder.record) {
				double x1 = xpf[handle[2]]+x;
				double y1 = ypf[handle[2]]+y;
				double x2 = xpf[handle[0]]+x;
				double y2 = ypf[handle[0]]+y;
 				if (Recorder.scriptMode())
					Recorder.recordCall("imp.setRoi(new EllipseRoi("+x1+","+y1+","+x2+","+y2+","+IJ.d2s(aspectRatio,2)+"));");
				else
					Recorder.record("makeEllipse", (int)x1, (int)y1, (int)x2, (int)y2, aspectRatio);
			}
        }
		state = NORMAL;
	}
	
	/* (non-Javadoc)
	 * @see ij.gui.PolygonRoi#moveHandle(int, int)
	 */
	protected void moveHandle(int sx, int sy) {
		double ox = ic.offScreenXD(sx); 
		double oy = ic.offScreenYD(sy);
		double xbase=x, ybase=y;
		if (bounds!=null) {
			xbase = bounds.x;
			ybase = bounds.y;
		}
		double x1 = xpf[handle[2]]+xbase;
		double y1 = ypf[handle[2]]+ybase;
		double x2 = xpf[handle[0]]+xbase;
		double y2 = ypf[handle[0]]+ybase;
		switch(activeHandle) {
			case 0: 
				x2 = ox;
				y2 = oy;
				break;
			case 1: 
				double dx = (xpf[handle[3]]+xbase) - ox;
				double dy = (ypf[handle[3]]+ybase) - oy;
				updateRatio(Math.sqrt(dx*dx+dy*dy), x1, y1, x2, y2);
				break;
			case 2: 
				x1 = ox;
				y1 = oy;
				break;
			case 3: 
				dx = (xpf[handle[1]]+xbase) - ox;
				dy = (ypf[handle[1]]+ybase) - oy;
				updateRatio(Math.sqrt(dx*dx+dy*dy), x1, y1, x2, y2);
				break;
		}
		makeEllipse(x1, y1, x2, y2);
		imp.draw();
	}
	
	/**
	 * Update ratio.
	 *
	 * @param minor the minor
	 * @param x1 the x 1
	 * @param y1 the y 1
	 * @param x2 the x 2
	 * @param y2 the y 2
	 */
	void updateRatio(double minor, double x1, double y1, double x2, double y2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		double major = Math.sqrt(dx*dx+dy*dy);
		aspectRatio = minor/major;
		if (aspectRatio>1.0) aspectRatio = 1.0;
		defaultRatio = aspectRatio;
	}
	
	/* (non-Javadoc)
	 * @see ij.gui.PolygonRoi#isHandle(int, int)
	 */
	public int isHandle(int sx, int sy) {
		int size = HANDLE_SIZE+5;
		int halfSize = size/2;
		int index = -1;
		for (int i=0; i<handle.length; i++) {
			int sx2 = xp2[handle[i]]-halfSize, sy2=yp2[handle[i]]-halfSize;
			if (sx>=sx2 && sx<=sx2+size && sy>=sy2 && sy<=sy2+size) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	/**
	 *  Returns the perimeter of this ellipse.
	 *
	 * @return the length
	 */
	public double getLength() {
		double length = 0.0;
		double dx, dy;
		double w2=1.0, h2=1.0;
		if (imp!=null) {
			Calibration cal = imp.getCalibration();
			w2 = cal.pixelWidth*cal.pixelWidth;
			h2 = cal.pixelHeight*cal.pixelHeight;
		}
		for (int i=0; i<(nPoints-1); i++) {
			dx = xpf[i+1]-xpf[i];
			dy = ypf[i+1]-ypf[i];
			length += Math.sqrt(dx*dx*w2+dy*dy*h2);
		}
		dx = xpf[0]-xpf[nPoints-1];
		dy = ypf[0]-ypf[nPoints-1];
		length += Math.sqrt(dx*dx*w2+dy*dy*h2);
		return length;
	}

	/**
	 *  Returns x1, y1, x2, y2 and aspectRatio as a 5 element array.
	 *
	 * @return the params
	 */
	public double[] getParams() {
		double xbase=x, ybase=y;
		if (bounds!=null) {
			xbase = bounds.x;
			ybase = bounds.y;
		}
		double[] params = new double[5];
		params[0] = xpf[handle[2]]+xbase;
		params[1]  = ypf[handle[2]]+ybase;
		params[2]  = xpf[handle[0]]+xbase;
		params[3]  = ypf[handle[0]]+ybase;
		params[4]  = aspectRatio;
		return params;
	}

	/* (non-Javadoc)
	 * @see ij.gui.Roi#getFeretValues()
	 */
	public double[] getFeretValues() {
		double a[] = super.getFeretValues();
		double pw=1.0, ph=1.0;
		if (imp!=null) {
			Calibration cal = imp.getCalibration();
			pw = cal.pixelWidth;
			ph = cal.pixelHeight;
		}
		double[] p = getParams();
		double dx = (p[2] - p[0])*pw;
		double dy = (p[3] - p[1])*ph;
		double major = Math.sqrt(dx*dx+dy*dy);
		double minor = major*p[4];
		a[0] = major;
		a[2] = (pw==ph)?minor:a[2];
		return a;
	}
	
	/**
	 *  Always returns true.
	 *
	 * @return true, if successful
	 */
	public boolean subPixelResolution() {
		return true;
	}

}