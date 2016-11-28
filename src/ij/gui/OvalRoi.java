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

import ij.ImagePlus;
import ij.measure.Calibration;
import ij.process.ByteProcessor;
import ij.process.FloatPolygon;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;

// TODO: Auto-generated Javadoc
/**
 *  Oval region of interest.
 */
public class OvalRoi extends Roi {

	/**
	 *  Creates an OvalRoi.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	public OvalRoi(int x, int y, int width, int height) {
		super(x, y, width, height);
		type = OVAL;
	}

	/**
	 *  Creates an OvalRoi using double arguments.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	public OvalRoi(double x, double y, double width, double height) {
		super(x, y, width, height);
		type = OVAL;
	}

	/**
	 *  Starts the process of creating a user-defined OvalRoi.
	 *
	 * @param x the x
	 * @param y the y
	 * @param imp the imp
	 */
	public OvalRoi(int x, int y, ImagePlus imp) {
		super(x, y, imp);
		type = OVAL;
	}

	/**
	 * Instantiates a new oval roi.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 * @param imp the imp
	 * @deprecated 
	 */
		public OvalRoi(int x, int y, int width, int height, ImagePlus imp) {
		this(x, y, width, height);
		setImage(imp);
	}

	/* (non-Javadoc)
	 * @see ij.gui.Roi#moveHandle(int, int)
	 */
	protected void moveHandle(int sx, int sy) {
		double asp;
		if (clipboard!=null) return;
		int ox = ic.offScreenX(sx);
		int oy = ic.offScreenY(sy);
		//IJ.log("moveHandle: "+activeHandle+" "+ox+" "+oy);
		int x1=x, y1=y, x2=x+width, y2=y+height, xc=x+width/2, yc=y+height/2;
		int w2 = (int)(0.14645*width);
		int h2 = (int)(0.14645*height);
		if (width > 7 && height > 7) {
			asp = (double)width/(double)height;
			asp_bk = asp;
		} else
			asp = asp_bk;
		switch (activeHandle) {
			case 0: x=ox-w2; y=oy-h2; break;
			case 1: y=oy; break;
			case 2: x2=ox+w2; y=oy-h2; break;
			case 3: x2=ox; break;			
			case 4: x2=ox+w2; y2=oy+h2; break;
			case 5: y2=oy; break;
			case 6: x=ox-w2; y2=oy+h2; break;
			case 7: x=ox; break;
		}
		//if (x<0) x=0; if (y<0) y=0;
		if (x<x2)
		   width=x2-x;
		else
		  {width=1; x=x2;}
		if (y<y2)
		   height = y2-y;
		else
		   {height=1; y=y2;}
		if(center) {
			switch(activeHandle){
				case 0:
					width=(xc-x)*2;
					height=(yc-y)*2;
					break;
				case 1:
					height=(yc-y)*2;
					break;
				case 2:
					width=(x2-xc)*2;
					x=x2-width;
					height=(yc-y)*2;
					break;
				case 3:
					width=(x2-xc)*2;
					x=x2-width;
					break;
				case 4:
					width=(x2-xc)*2;
					x=x2-width;
					height=(y2-yc)*2;
					y=y2-height;
					break;
				case 5:
					height=(y2-yc)*2;
					y=y2-height;
					break;
				case 6:
					width=(xc-x)*2;
					height=(y2-yc)*2;
					y=y2-height;
					break;
				case 7:
					width=(xc-x)*2;
					break;
			}
			if(x>=x2) {
				width=1;
				x=x2=xc;
			}
			if(y>=y2) {
				height=1;
				y=y2=yc;
			}

		}

		if (constrain) {
			if(activeHandle==1 || activeHandle==5) width=height;
			else height=width;
			
			if(x>=x2) {
				width=1;
				x=x2=xc;
			}
			if (y>=y2) {
				height=1;
				y=y2=yc;
			}
			switch(activeHandle){
				case 0:
					x=x2-width;
					y=y2-height;
					break;
				case 1:
					x=xc-width/2;
					y=y2-height;
					break;
				case 2:
					y=y2-height;
					break;
				case 3:
					y=yc-height/2;
					break;
				case 5:
					x=xc-width/2;
					break;
				case 6:
					x=x2-width;
					break;
				case 7:
					y=yc-height/2;
					x=x2-width;
					break;
			}
			if (center){
				x=xc-width/2;
				y=yc-height/2;
			}
		}

		if (aspect && !constrain) {
			if(activeHandle==1 || activeHandle==5) width=(int)Math.rint((double)height*asp);
			else height=(int)Math.rint((double)width/asp);

			switch (activeHandle){
				case 0:
					x=x2-width;
					y=y2-height;
					break;
				case 1:
					x=xc-width/2;
					y=y2-height;
					break;
				case 2:
					y=y2-height;
					break;
				case 3:
					y=yc-height/2;
					break;
				case 5:
					x=xc-width/2;
					break;
				case 6:
					x=x2-width;
					break;
				case 7:
					y=yc-height/2;
					x=x2-width;
					break;
			}
			if (center){
				x=xc-width/2;
				y=yc-height/2;
			}
			// Attempt to preserve aspect ratio when roi very small:
			if (width<8) {
				if(width<1) width = 1;
				height=(int)Math.rint((double)width/asp_bk);
			}
			if (height<8) {
				if(height<1) height =1;
				width=(int)Math.rint((double)height*asp_bk);
			}
		}

		updateClipRect();
		imp.draw(clipX, clipY, clipWidth, clipHeight);
		oldX=x; oldY=y;
		oldWidth=width; oldHeight=height;
		cachedMask = null;
		bounds = null;
	}

	/* (non-Javadoc)
	 * @see ij.gui.Roi#draw(java.awt.Graphics)
	 */
	public void draw(Graphics g) {
		Color color =  strokeColor!=null? strokeColor:ROIColor;
		if (fillColor!=null) color = fillColor;
		g.setColor(color);
		mag = getMagnification();
		int sw = (int)(width*mag);
		int sh = (int)(height*mag);
		int sx1 = screenX(x);
		int sy1 = screenY(y);
		if (subPixelResolution() && bounds!=null) {
			sw = (int)(bounds.width*mag);
			sh = (int)(bounds.height*mag);
			sx1 = screenXD(bounds.x);
			sy1 = screenYD(bounds.y);
		}
		int sw2 = (int)(0.14645*width*mag);
		int sh2 = (int)(0.14645*height*mag);
		int sx2 = sx1+sw/2;
		int sy2 = sy1+sh/2;
		int sx3 = sx1+sw;
		int sy3 = sy1+sh;
		Graphics2D g2d = (Graphics2D)g;
		if (stroke!=null) 
			g2d.setStroke(getScaledStroke());
		if (fillColor!=null) {
			if (!overlay && isActiveOverlayRoi()) {
				g.setColor(Color.cyan);
				g.drawOval(sx1, sy1, sw, sh);
			} else
				g.fillOval(sx1, sy1, sw, sh);
		} else
			g.drawOval(sx1, sy1, sw, sh);
		if (state!=CONSTRUCTING && clipboard==null && !overlay) {
			int size2 = HANDLE_SIZE/2;
			drawHandle(g, sx1+sw2-size2, sy1+sh2-size2);
			drawHandle(g, sx3-sw2-size2, sy1+sh2-size2);
			drawHandle(g, sx3-sw2-size2, sy3-sh2-size2);
			drawHandle(g, sx1+sw2-size2, sy3-sh2-size2);
			drawHandle(g, sx2-size2, sy1-size2);
			drawHandle(g, sx3-size2, sy2-size2);
			drawHandle(g, sx2-size2, sy3-size2);
			drawHandle(g, sx1-size2, sy2-size2);
		}
		drawPreviousRoi(g);
		if (updateFullWindow)
			{updateFullWindow = false; imp.draw();}
		if (state!=NORMAL) showStatus();
	}

	/**
	 *  Draws an outline of this OvalRoi on the image.
	 *
	 * @param ip the ip
	 */
	public void drawPixels(ImageProcessor ip) {
		Polygon p = getPolygon();
		if (p.npoints>0) {
			int saveWidth = ip.getLineWidth();
			if (getStrokeWidth()>1f)
				ip.setLineWidth((int)Math.round(getStrokeWidth()));
			ip.drawPolygon(p);
			ip.setLineWidth(saveWidth);
		}
		if (Line.getWidth()>1 || getStrokeWidth()>1)
			updateFullWindow = true;
	}		

	/**
	 *  Returns this OvalRoi as a Polygon.
	 *
	 * @return the polygon
	 */
	public Polygon getPolygon() {
		ImageProcessor mask = getMask();
		Wand wand = new Wand(mask);
		wand.autoOutline(width/2,height/2, 255, 255);
        for (int i=0; i<wand.npoints; i++) {
            wand.xpoints[i] += x;
            wand.ypoints[i] += y;
        }
		return new Polygon(wand.xpoints, wand.ypoints, wand.npoints);
	}		

	/**
	 *  Returns this OvalRoi as a FloatPolygon.
	 *
	 * @return the float polygon
	 */
	public FloatPolygon getFloatPolygon() {
		Polygon p = getPolygon();
		return new FloatPolygon(toFloat(p.xpoints), toFloat(p.ypoints), p.npoints);
	}

	/**
	 *  Tests if the specified point is inside the boundary of this OvalRoi.
	 * Authors: Barry DeZonia and Michael Schmid
	 *
	 * @param ox the ox
	 * @param oy the oy
	 * @return true, if successful
	 */
	public boolean contains(int ox, int oy) {
		double a = width*0.5;
		double b = height*0.5;
		double cx = x + a - 0.5;
		double cy = y + b - 0.5;
		double dx = ox - cx;
		double dy = oy - cy;
		return ((dx*dx)/(a*a) + (dy*dy)/(b*b)) <= 1.0;
	}
		
	/**
	 *  Returns a handle number if the specified screen coordinates are  
	 * 		inside or near a handle, otherwise returns -1.
	 *
	 * @param sx the sx
	 * @param sy the sy
	 * @return the int
	 */
	public int isHandle(int sx, int sy) {
		if (clipboard!=null || ic==null) return -1;
		double mag = ic.getMagnification();
		int size = HANDLE_SIZE+3;
		int halfSize = size/2;
		int sx1 = ic.screenX(x) - halfSize;
		int sy1 = ic.screenY(y) - halfSize;
		int sx3 = ic.screenX(x+width) - halfSize;
		int sy3 = ic.screenY(y+height) - halfSize;
		int sx2 = sx1 + (sx3 - sx1)/2;
		int sy2 = sy1 + (sy3 - sy1)/2;
		
		int sw2 = (int)(0.14645*(sx3-sx1));
		int sh2 = (int)(0.14645*(sy3-sy1));
		
		if (sx>=sx1+sw2&&sx<=sx1+sw2+size&&sy>=sy1+sh2&&sy<=sy1+sh2+size) return 0;
		if (sx>=sx2&&sx<=sx2+size&&sy>=sy1&&sy<=sy1+size) return 1;		
		if (sx>=sx3-sw2&&sx<=sx3-sw2+size&&sy>=sy1+sh2&&sy<=sy1+sh2+size) return 2;		
		if (sx>=sx3&&sx<=sx3+size&&sy>=sy2&&sy<=sy2+size) return 3;		
		if (sx>=sx3-sw2&&sx<=sx3-sw2+size&&sy>=sy3-sh2&&sy<=sy3-sh2+size) return 4;		
		if (sx>=sx2&&sx<=sx2+size&&sy>=sy3&&sy<=sy3+size) return 5;		
		if (sx>=sx1+sw2&&sx<=sx1+sw2+size&&sy>=sy3-sh2&&sy<=sy3-sh2+size) return 6;
		if (sx>=sx1&&sx<=sx1+size&&sy>=sy2&&sy<=sy2+size) return 7;
		return -1;
	}

	/* (non-Javadoc)
	 * @see ij.gui.Roi#getMask()
	 */
	public ImageProcessor getMask() {
		if (cachedMask!=null && cachedMask.getPixels()!=null)
			return cachedMask;
		ImageProcessor mask = new ByteProcessor(width, height);
		double a=width/2.0, b=height/2.0;
		double a2=a*a, b2=b*b;
        a -= 0.5; b -= 0.5;
		double xx, yy;
        int offset;
        byte[] pixels = (byte[])mask.getPixels();
		for (int y=0; y<height; y++) {
            offset = y*width;
			for (int x=0; x<width; x++) {
				xx = x - a;
				yy = y - b;   
				if ((xx*xx/a2+yy*yy/b2)<=1.0)
					pixels[offset+x] = -1;
			}
		}
		cachedMask = mask;
		return mask;
	}

	/**
	 *  Returns the perimeter length.
	 *
	 * @return the length
	 */
	public double getLength() {
		double pw=1.0, ph=1.0;
		if (imp!=null) {
			Calibration cal = imp.getCalibration();
			pw = cal.pixelWidth;
			ph = cal.pixelHeight;
		}
		return Math.PI*(width*pw+height*ph)/2.0;
	}
		
}