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
package org.pushingpixels.radiance.theming.internal.blade;

import org.pushingpixels.radiance.common.api.RadianceCommonCortex;
import org.pushingpixels.radiance.theming.api.ComponentState;
import org.pushingpixels.radiance.theming.api.colorscheme.RadianceColorScheme;
import org.pushingpixels.radiance.theming.api.painter.border.FlatBorderPainter;
import org.pushingpixels.radiance.theming.api.painter.border.RadianceBorderPainter;
import org.pushingpixels.radiance.theming.api.painter.fill.RadianceFillPainter;
import org.pushingpixels.radiance.theming.internal.painter.SimplisticFillPainter;
import org.pushingpixels.radiance.theming.internal.utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

public class BladeIconUtils {
    /**
     * Custom fill painter for the checkmarks of checkboxes and radio buttons.
     *
     * @author Kirill Grouchnikov
     */
    private static class SimplisticSoftBorderReverseFillPainter extends SimplisticFillPainter {
        /**
         * Singleton instance.
         */
        public static final RadianceFillPainter INSTANCE = new SimplisticSoftBorderReverseFillPainter();

        /**
         * Private constructor.
         */
        private SimplisticSoftBorderReverseFillPainter() {
        }

        @Override
        public String getDisplayName() {
            return "Simplistic Soft Border Reverse";
        }

        @Override
        public Color getTopFillColor(RadianceColorScheme fillScheme) {
            return super.getBottomFillColor(fillScheme);
        }

        @Override
        public Color getBottomFillColor(RadianceColorScheme fillScheme) {
            return super.getTopFillColor(fillScheme);
        }
    }

    public static void drawCheckBox(Graphics2D g, AbstractButton button, RadianceFillPainter fillPainter,
            RadianceBorderPainter borderPainter, int dimension, ComponentState componentState,
            RadianceColorScheme fillColorScheme, RadianceColorScheme markColorScheme,
            RadianceColorScheme borderColorScheme, float checkMarkVisibility,
            boolean isCheckMarkFadingOut, float alpha) {

        Graphics2D graphics = (Graphics2D) g.create();
        // Important - do not set KEY_STROKE_CONTROL to VALUE_STROKE_PURE, as that instructs AWT
        // to not normalize coordinates to paint at full pixels, and will result in blurry
        // outlines.
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        RadianceCommonCortex.paintAtScale1x(graphics, 0, 0, dimension, dimension,
                (graphics1X, x, y, scaledWidth, scaledHeight, scaleFactor) -> {
                    float cornerRadius = (float) scaleFactor *
                            RadianceSizeUtils.getClassicButtonCornerRadius(RadianceSizeUtils.getComponentFontSize(button));

                    int contourDim = scaledWidth - 1;
                    Shape contour = RadianceOutlineUtilities.getBaseOutline(
                            contourDim, contourDim,
                            cornerRadius, null, 0.0f);

                    RadianceFillPainter finalFillPainter = componentState.isActive() ? fillPainter
                            : SimplisticSoftBorderReverseFillPainter.INSTANCE;
                    graphics1X.setComposite(getAlphaComposite(alpha));
                    Graphics2D clipped = (Graphics2D) graphics1X.create();
                    clipped.clip(contour);
                    finalFillPainter.paintContourBackground(clipped, button,
                            contourDim, contourDim,
                            contour, false,
                            fillColorScheme, true);
                    clipped.dispose();

                    Shape contourInner = borderPainter.isPaintingInnerContour() ?
                            RadianceOutlineUtilities.getBaseOutline(
                                    contourDim, contourDim, cornerRadius, null, 1.0f)
                            : null;
                    borderPainter.paintBorder(graphics1X, button, contourDim, contourDim,
                            contour, contourInner, borderColorScheme);

                    float finalCheckMarkVisibility = isCheckMarkFadingOut && (checkMarkVisibility > 0.0f) ?
                            1.0f : checkMarkVisibility;
                    if (finalCheckMarkVisibility > 0.0) {
                        Graphics2D graphicsForCheckMark = (Graphics2D) graphics1X.create();
                        if (isCheckMarkFadingOut) {
                            graphicsForCheckMark.setComposite(getAlphaComposite(alpha * checkMarkVisibility));
                        }

                        drawCheckMarkAtScale1X(graphicsForCheckMark, scaledWidth, markColorScheme,
                                checkMarkVisibility);

                        graphicsForCheckMark.dispose();
                    }
                });
    }

    private static void drawCheckMarkAtScale1X(Graphics2D graphics1X, int dimension,
            RadianceColorScheme scheme, float checkMarkVisibility) {
        // create straight checkbox path
        GeneralPath path = new GeneralPath();
        path.moveTo(0.25f * dimension, 0.47f * dimension);
        path.lineTo(0.48f * dimension, 0.72f * dimension);
        path.lineTo(0.76f * dimension, 0.27f * dimension);

        // compute the x-based clip for the visibility
        float xClipStart = 0.15f * dimension;
        float xClipEnd = 0.95f * dimension;
        float xClipRealEnd = xClipStart + (xClipEnd - xClipStart) * checkMarkVisibility;

        Graphics2D clipped = (Graphics2D) graphics1X.create();
        clipped.setClip(0, 0, (int) Math.ceil(xClipRealEnd), dimension);

        clipped.setColor(scheme.getMarkColor());
        Stroke stroke = new BasicStroke((float) 0.15 * dimension, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND);
        clipped.setStroke(stroke);
        clipped.draw(path);
        clipped.dispose();
    }

    public static void drawRadioButton(Graphics2D g, AbstractButton button, RadianceFillPainter fillPainter,
            RadianceBorderPainter borderPainter, int dimension, ComponentState componentState,
            RadianceColorScheme fillColorScheme, RadianceColorScheme markColorScheme,
            RadianceColorScheme borderColorScheme, float checkMarkVisibility,
            float alpha) {

        Graphics2D graphics = (Graphics2D) g.create();
        // Important - do not set KEY_STROKE_CONTROL to VALUE_STROKE_PURE, as that instructs AWT
        // to not normalize coordinates to paint at full pixels, and will result in blurry
        // outlines.
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        RadianceCommonCortex.paintAtScale1x(graphics, 0, 0, dimension, dimension,
                (graphics1X, x, y, scaledWidth, scaledHeight, scaleFactor) -> {
                    int contourDim = scaledWidth;
                    Shape contour = new Ellipse2D.Float(0.0f, 0.0f, contourDim, contourDim);

                    RadianceFillPainter finalFillPainter = componentState.isActive() ? fillPainter
                            : SimplisticSoftBorderReverseFillPainter.INSTANCE;
                    graphics1X.setComposite(getAlphaComposite(alpha));
                    Graphics2D clipped = (Graphics2D) graphics1X.create();
                    clipped.clip(contour);
                    finalFillPainter.paintContourBackground(clipped, button,
                            contourDim, contourDim,
                            contour, false,
                            fillColorScheme, true);
                    clipped.dispose();

                    Shape contourInner = borderPainter.isPaintingInnerContour() ?
                            new Ellipse2D.Float(1.0f, 1.0f, contourDim - 2.0f, contourDim - 2.0f)
                            : null;
                    borderPainter.paintBorder(graphics1X, button, contourDim, contourDim,
                            contour, contourInner, borderColorScheme);

                    float rc = contourDim / 2.0f + 0.5f;
                    float radius = contourDim / 4.5f;
                    Shape markOval = new Ellipse2D.Double(rc - radius, rc - radius, 2 * radius, 2 * radius);
                    Graphics2D graphicsForCheckMark = (Graphics2D) graphics1X.create();

                    if (checkMarkVisibility > 0.0) {
                        // mark
                        graphicsForCheckMark.setComposite(getAlphaComposite(alpha * checkMarkVisibility));
                        graphicsForCheckMark.setColor(markColorScheme.getMarkColor());
                    } else {
                        // draw ghost mark holder
                        graphicsForCheckMark.setComposite(getAlphaComposite(alpha * 0.3f));
                        graphicsForCheckMark.setPaint(
                                new GradientPaint(rc + radius, rc - radius, fillColorScheme.getDarkColor(),
                                        rc - radius, rc + radius, fillColorScheme.getLightColor()));
                    }
                    graphicsForCheckMark.fill(markOval);
                    graphicsForCheckMark.dispose();
                });
    }

    public static void drawSliderThumbHorizontal(Graphics2D g, JSlider slider,
            RadianceFillPainter fillPainter, RadianceBorderPainter borderPainter,
            int width, int height, boolean isMirrored,
            RadianceColorScheme fillColorScheme,
            RadianceColorScheme borderColorScheme, float alpha) {

        Graphics2D graphics = (Graphics2D) g.create();
        // Important - do not set KEY_STROKE_CONTROL to VALUE_STROKE_PURE, as that instructs AWT
        // to not normalize coordinates to paint at full pixels, and will result in blurry
        // outlines.
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        RadianceCommonCortex.paintAtScale1x(graphics, 0, 0, width, height,
                (graphics1X, x, y, scaledWidth, scaledHeight, scaleFactor) -> {
                    if (isMirrored) {
                        AffineTransform mirror = AffineTransform.getTranslateInstance(scaledWidth, scaledHeight);
                        mirror.rotate(Math.PI);
                        graphics1X.transform(mirror);
                    }

                    Shape contour = RadianceOutlineUtilities.getTriangleButtonOutline(
                            scaledWidth, scaledHeight, 2 * (float) scaleFactor, 1.0f);

                    graphics1X.setComposite(getAlphaComposite(alpha));
                    Graphics2D clipped = (Graphics2D) graphics1X.create();
                    clipped.clip(contour);
                    fillPainter.paintContourBackground(clipped, slider,
                            scaledWidth, scaledHeight,
                            contour, false,
                            fillColorScheme, true);
                    clipped.dispose();

                    Shape contourInner = RadianceOutlineUtilities.getTriangleButtonOutline(
                            scaledWidth, scaledHeight, 2 * (float) scaleFactor, 2.0f);
                    borderPainter.paintBorder(graphics1X, slider,
                            scaledWidth, scaledHeight,
                            contour, contourInner, borderColorScheme);
                });
    }

    public static void drawSliderThumbVertical(Graphics2D g, JSlider slider,
            RadianceFillPainter fillPainter, RadianceBorderPainter borderPainter,
            int width, int height, boolean isMirrored,
            RadianceColorScheme fillColorScheme,
            RadianceColorScheme borderColorScheme, float alpha) {

        Graphics2D graphics = (Graphics2D) g.create();
        // Important - do not set KEY_STROKE_CONTROL to VALUE_STROKE_PURE, as that instructs AWT
        // to not normalize coordinates to paint at full pixels, and will result in blurry
        // outlines.
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        RadianceCommonCortex.paintAtScale1x(graphics, 0, 0, height, width,
                (graphics1X, x, y, scaledWidth, scaledHeight, scaleFactor) -> {
                    AffineTransform at;
                    if (isMirrored) {
                        at = AffineTransform.getTranslateInstance(scaledWidth, 0);
                        at.rotate(Math.PI / 2);
                    } else {
                        at = AffineTransform.getTranslateInstance(0, scaledHeight);
                        at.rotate(-Math.PI / 2);
                    }
                    graphics1X.transform(at);
                    if (!slider.getComponentOrientation().isLeftToRight()) {
                        AffineTransform mirror = AffineTransform.getTranslateInstance(scaledWidth, scaledHeight);
                        mirror.rotate(Math.PI);
                        graphics1X.transform(mirror);
                    }

                    Shape contour = RadianceOutlineUtilities.getTriangleButtonOutline(
                            scaledWidth, scaledHeight, 2 * (float) scaleFactor, 1.0f);

                    graphics1X.setComposite(getAlphaComposite(alpha));
                    Graphics2D clipped = (Graphics2D) graphics1X.create();
                    clipped.clip(contour);
                    fillPainter.paintContourBackground(clipped, slider,
                            scaledWidth, scaledHeight,
                            contour, false,
                            fillColorScheme, true);
                    clipped.dispose();

                    Shape contourInner = RadianceOutlineUtilities.getTriangleButtonOutline(
                            scaledWidth, scaledHeight, 2 * (float) scaleFactor, 2.0f);
                    borderPainter.paintBorder(graphics1X, slider,
                            scaledWidth, scaledHeight,
                            contour, contourInner, borderColorScheme);
                });
    }

    public static void drawSliderThumbRound(Graphics2D g, JSlider slider,
            RadianceFillPainter fillPainter, RadianceBorderPainter borderPainter,
            int dimension,
            RadianceColorScheme fillColorScheme,
            RadianceColorScheme borderColorScheme, float alpha) {

        Graphics2D graphics = (Graphics2D) g.create();
        // Important - do not set KEY_STROKE_CONTROL to VALUE_STROKE_PURE, as that instructs AWT
        // to not normalize coordinates to paint at full pixels, and will result in blurry
        // outlines.
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        RadianceCommonCortex.paintAtScale1x(graphics, 0, 0, dimension, dimension,
                (graphics1X, x, y, scaledWidth, scaledHeight, scaleFactor) -> {
                    Shape contour = new Ellipse2D.Float(0.0f, 0.0f,
                            scaledWidth - 1.0f, scaledHeight - 1.0f);

                    graphics1X.setComposite(getAlphaComposite(alpha));
                    Graphics2D clipped = (Graphics2D) graphics1X.create();
                    clipped.clip(contour);
                    fillPainter.paintContourBackground(clipped, slider,
                            scaledWidth, scaledHeight,
                            contour, false,
                            fillColorScheme, true);
                    clipped.dispose();

                    Shape contourInner = new Ellipse2D.Float(1.0f, 1.0f,
                            scaledWidth - 3.0f, scaledHeight - 3.0f);
                    borderPainter.paintBorder(graphics1X, slider,
                            scaledWidth, scaledHeight,
                            contour, contourInner, borderColorScheme);
                });
    }

    public static void drawTreeIcon(Graphics2D g, JTree tree, int size, RadianceColorScheme fillScheme,
            RadianceColorScheme borderScheme, RadianceColorScheme markScheme,
            boolean isCollapsed) {

        Graphics2D graphics = (Graphics2D) g.create();
        // Important - do not set KEY_STROKE_CONTROL to VALUE_STROKE_PURE, as that instructs AWT
        // to not normalize coordinates to paint at full pixels, and will result in blurry
        // outlines.
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        RadianceCommonCortex.paintAtScale1x(graphics, 0, 0, size, size,
                (graphics1X, x, y, scaledWidth, scaledHeight, scaleFactor) -> {
                    RadianceFillPainter fillPainter = SimplisticSoftBorderReverseFillPainter.INSTANCE;
                    RadianceBorderPainter borderPainter = new FlatBorderPainter();

                    Shape contour = RadianceOutlineUtilities.getBaseOutline(scaledWidth, scaledHeight,
                            (float) scaleFactor * RadianceSizeUtils.getClassicButtonCornerRadius(
                                    RadianceSizeUtils.getComponentFontSize(tree)) / 1.5f, null,
                            1.0f);

                    fillPainter.paintContourBackground(graphics1X, tree, scaledWidth, scaledHeight,
                            contour, false, fillScheme, false);
                    borderPainter.paintBorder(graphics1X, tree, scaledWidth, scaledHeight, contour,
                            null, borderScheme);

                    Color signColor = markScheme.getForegroundColor();
                    graphics1X.setColor(signColor);
                    float mid = scaledWidth / 2;
                    float length = 7 * scaledWidth / 12;
                    // Horizontal stroke
                    graphics1X.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
                    graphics1X.draw(new Line2D.Float(mid - length / 2, mid, mid + length / 2, mid));
                    if (isCollapsed) {
                        // Vertical stroke
                        graphics1X.draw(new Line2D.Float(mid, mid - length / 2, mid, mid + length / 2));
                    }
                });
    }

    public static void drawCloseIcon(Graphics2D g, int iconSize,
            float primaryStrokeWidth, RadianceColorScheme scheme) {
        Graphics2D graphics = (Graphics2D) g.create();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        int start = iconSize / 4;
        int end = iconSize - start;

        Color primaryColor = scheme.getMarkColor();
        Color echoColor = scheme.getEchoColor();

        int fgStrength = RadianceColorUtilities.getColorBrightness(primaryColor.getRGB());
        int echoStrength = RadianceColorUtilities.getColorBrightness(echoColor.getRGB());
        boolean noEcho = Math.abs(fgStrength - echoStrength) < 48;

        if (!noEcho) {
            // Thicker, translucent stroke
            Stroke echoStroke = new BasicStroke(primaryStrokeWidth * 2.0f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            graphics.setStroke(echoStroke);
            graphics.setColor(echoColor);
            graphics.drawLine(start, start, end, end);
            graphics.drawLine(start, end, end, start);
            graphics.setComposite(AlphaComposite.SrcOver);
        }

        Stroke primaryStroke = new BasicStroke(primaryStrokeWidth,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        graphics.setStroke(primaryStroke);
        graphics.setColor(primaryColor);
        graphics.drawLine(start, start, end, end);
        graphics.drawLine(start, end, end, start);

        graphics.dispose();
    }

    public static void drawMinimizeIcon(Graphics2D g, int iconSize,
            RadianceColorScheme scheme) {
        Graphics2D graphics = (Graphics2D) g.create();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        int start = iconSize / 4 - 2;
        int end = 3 * iconSize / 4;
        int size = end - start - 2;

        Color primaryColor = scheme.getMarkColor();
        Color echoColor = scheme.getEchoColor();

        int fgStrength = RadianceColorUtilities.getColorBrightness(primaryColor.getRGB());
        int echoStrength = RadianceColorUtilities.getColorBrightness(echoColor.getRGB());
        boolean noEcho = Math.abs(fgStrength - echoStrength) < 48;

        if (!noEcho) {
            // Thicker, translucent stroke
            Stroke echoStroke = new BasicStroke(4.2f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            graphics.setStroke(echoStroke);
            graphics.setColor(echoColor);
            graphics.drawLine(start + 3, end, start + size, end);
            graphics.setComposite(AlphaComposite.SrcOver);
        }

        graphics.setColor(primaryColor);
        graphics.fillRect(start + 2, end - 1, size, 3);
        graphics.dispose();
    }

    public static void drawMaximizeIcon(Graphics2D g, int iconSize,
            RadianceColorScheme scheme) {
        Graphics2D graphics = (Graphics2D) g.create();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        int start = iconSize / 4 - 1;
        int end = iconSize - start;

        Color primaryColor = scheme.getMarkColor();
        Color echoColor = scheme.getEchoColor();

        int fgStrength = RadianceColorUtilities.getColorBrightness(primaryColor.getRGB());
        int echoStrength = RadianceColorUtilities.getColorBrightness(echoColor.getRGB());
        boolean noEcho = Math.abs(fgStrength - echoStrength) < 48;

        if (!noEcho) {
            graphics.setColor(echoColor);

            // Thicker, translucent strokes
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

            Stroke echoStroke = new BasicStroke(3.1f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(echoStroke);
            // top (thicker)
            graphics.drawLine(start, start, end - 1, start);

            echoStroke = new BasicStroke(2.0f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(echoStroke);
            GeneralPath rest = new GeneralPath();
            rest.moveTo(start, start);
            rest.lineTo(start, end);
            rest.lineTo(end, end);
            rest.lineTo(end, start);
            graphics.draw(rest);

            graphics.setComposite(AlphaComposite.SrcOver);
        }

        graphics.setColor(primaryColor);
        // top (thicker)
        graphics.fillRect(start, start, end - start, 2);
        // left
        graphics.fillRect(start, start, 1, end - start);
        // right
        graphics.fillRect(end - 1, start, 1, end - start);
        // bottom
        graphics.fillRect(start, end - 1, end - start, 1);
        graphics.dispose();
    }

    public static void drawRestoreIcon(Graphics2D g, int iconSize,
            RadianceColorScheme scheme) {
        Graphics2D graphics = (Graphics2D) g.create();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        int start = iconSize / 4 - 1;
        int end = iconSize - start;
        int smallSquareSize = end - start - 3;
        Color primaryColor = scheme.getMarkColor();
        Color echoColor = scheme.getEchoColor();

        int fgStrength = RadianceColorUtilities.getColorBrightness(primaryColor.getRGB());
        int echoStrength = RadianceColorUtilities.getColorBrightness(echoColor.getRGB());
        boolean noEcho = Math.abs(fgStrength - echoStrength) < 48;

        if (!noEcho) {
            graphics.setColor(echoColor);

            // Thicker, translucent strokes
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

            // "Main" rectangle
            int mainStartX = start;
            int mainStartY = end - smallSquareSize;

            Stroke echoStroke = new BasicStroke(3.1f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(echoStroke);
            // top (thicker)
            graphics.drawLine(mainStartX + 1, mainStartY, mainStartX + smallSquareSize - 1, mainStartY);

            echoStroke = new BasicStroke(2.0f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(echoStroke);
            GeneralPath rest = new GeneralPath();
            rest.moveTo(mainStartX, mainStartY);
            rest.lineTo(mainStartX, mainStartY + smallSquareSize);
            rest.lineTo(mainStartX + smallSquareSize, mainStartY + smallSquareSize);
            rest.lineTo(mainStartX + smallSquareSize, mainStartY);
            graphics.draw(rest);

            // "Secondary rectangle"
            int secondaryStartX = mainStartX + 3;
            int secondaryStartY = mainStartY - 3;

            echoStroke = new BasicStroke(3.1f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(echoStroke);
            // top (thicker)
            graphics.drawLine(secondaryStartX + 1, secondaryStartY,
                    secondaryStartX + smallSquareSize - 1, secondaryStartY);

            echoStroke = new BasicStroke(2.0f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            graphics.setStroke(echoStroke);

            rest.reset();
            rest.moveTo(secondaryStartX + smallSquareSize - 1, secondaryStartY);
            rest.lineTo(secondaryStartX + smallSquareSize - 1, secondaryStartY + smallSquareSize);
            rest.lineTo(mainStartX + smallSquareSize + 1, secondaryStartY + smallSquareSize);
            graphics.draw(rest);

            graphics.setComposite(AlphaComposite.SrcOver);
        }

        graphics.setColor(primaryColor);

        // "Main" rectangle
        int mainStartX = start;
        int mainStartY = end - smallSquareSize;
        // top (thicker)
        graphics.fillRect(mainStartX, mainStartY, smallSquareSize, 2);
        // left
        graphics.fillRect(mainStartX, mainStartY, 1, smallSquareSize);
        // right
        graphics.fillRect(mainStartX + smallSquareSize - 1, mainStartY, 1, smallSquareSize);
        // bottom
        graphics.fillRect(mainStartX, mainStartY + smallSquareSize - 1, smallSquareSize, 1);

        // "Secondary rectangle"
        int secondaryStartX = mainStartX + 3;
        int secondaryStartY = mainStartY - 3;
        // top (thicker)
        graphics.fillRect(secondaryStartX, secondaryStartY, smallSquareSize, 2);
        // right
        graphics.fillRect(secondaryStartX + smallSquareSize - 1, secondaryStartY, 1,
                smallSquareSize);
        // bottom (partial)
        graphics.fillRect(mainStartX + smallSquareSize + 1, secondaryStartY + smallSquareSize - 1,
                2, 1);

        graphics.dispose();
    }

    private static AlphaComposite getAlphaComposite(float alpha) {
        // Fix for "alpha value out of range"
        float finalAlpha = alpha;
        if (finalAlpha < 0.0f)
            finalAlpha = 0.0f;
        else if (finalAlpha > 1.0f)
            finalAlpha = 1.0f;
        return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, finalAlpha);
    }

}
