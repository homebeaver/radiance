/*
 * Copyright (c) 2005-2022 Radiance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of the copyright holder nor the names of
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.pushingpixels.radiance.common.api.icon;

import org.pushingpixels.radiance.common.api.RadianceCommonCortex;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Extension of the core {@link Icon} interface that adds more capabilities like
 * resizing and color filtering.
 * 
 * @author Kirill Grouchnikov
 * @author EUG https://github.com/homebeaver (rotation)
 */
public interface RadianceIcon extends Icon, SwingConstants  {
	
	/**
	 * A hint to rotate the icon when painting
	 * 
	 * @param theta the angle of rotation in radians, zero means no rotation
	 */
	// a default is necessary for icons generated before this feature was active
	default void setRotation(double theta) {}
	default double getRotation() {
		return 0d; // no rotation
	}
	/**
	 * A hint to rotate the icon to a direction.
	 * <p> The icon is aligned to {@code NORTH} per default, 
	 * so rotate direction {@code NORTH_EAST} means rotating 45° right
	 * and {@code WEST} means rotating 90° left.
	 * 
	 * @param direction Compass-direction, use {@code SwingConstants} {@code NORTH}, {@code NORTH_EAST} etc
	 * 
	 * @see #setRotation(double)
	 */
	default void setRotation(int direction) {
        if(direction>=NORTH && direction<=SOUTH) {
        	this.setRotation(Math.PI*(direction-1)/4); // rotation to right, includes NORTH (no rotation)
        } else if(direction>=SOUTH_WEST && direction<=NORTH_WEST) {
        	this.setRotation(Math.PI*(direction-9)/4); // rotation to left
        } else {
            setRotation(0d); // no rotation for invalid directions
        }
	}

	/**
	 * Changes the dimension of <code>this</code> icon.
	 * 
	 * @param newDimension
	 *            New dimension for <code>this</code> icon.
	 */
	void setDimension(Dimension newDimension);

	default BufferedImage toImage(double scale) {
		BufferedImage result = RadianceCommonCortex.getBlankScaledImage(scale,
				this.getIconWidth(), this.getIconHeight());
		this.paintIcon(null, result.getGraphics(), 0, 0);
		return result;
	}

	boolean supportsColorFilter();

	void setColorFilter(ColorFilter colorFilter);

    /**
     * Interface for creating Radiance icons.
     *
     * @author Kirill Grouchnikov
     */
    interface Factory {
        /**
         * Returns a new instance of the icon managed by this factory.
         *
         * @return A new instance of the icon managed by this factory.
         */
		RadianceIcon createNewIcon();
    }

    interface ColorFilter {
    	Color filter(Color color);
	}
}
