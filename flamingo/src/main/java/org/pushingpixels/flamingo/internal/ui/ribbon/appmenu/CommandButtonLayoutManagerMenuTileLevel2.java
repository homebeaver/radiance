/*
 * Copyright (c) 2005-2019 Flamingo Kirill Grouchnikov. All Rights Reserved.
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
 *  o Neither the name of Flamingo Kirill Grouchnikov nor the names of 
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
package org.pushingpixels.flamingo.internal.ui.ribbon.appmenu;

import org.pushingpixels.flamingo.api.common.*;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;
import org.pushingpixels.neon.icon.ResizableIcon;
import org.pushingpixels.substance.internal.utils.SubstanceMetricsUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.font.*;
import java.beans.PropertyChangeEvent;
import java.text.AttributedString;
import java.util.ArrayList;

public class CommandButtonLayoutManagerMenuTileLevel2 implements CommandButtonLayoutManager {

    @Override
    public int getPreferredIconSize(AbstractCommandButton commandButton) {
        return FlamingoUtilities.getScaledSize(32, commandButton.getFont().getSize(), 2.0, 4);
    }

    @Override
    public Dimension getPreferredSize(AbstractCommandButton commandButton) {
        Insets borderInsets = commandButton.getInsets();
        int bx = borderInsets.left + borderInsets.right;
        int by = borderInsets.top + borderInsets.bottom;
        FontMetrics fm = SubstanceMetricsUtilities.getFontMetrics(commandButton.getFont());
        JSeparator jsep = new JSeparator(JSeparator.VERTICAL);

        int titleWidth = fm.stringWidth(commandButton.getText());
        int layoutHGap = 2 * FlamingoUtilities.getHLayoutGap(commandButton);
        int layoutVGap = 2 * FlamingoUtilities.getVLayoutGap(commandButton);
        int widthMed = this.getPreferredIconSize(commandButton)
                + 2 * layoutHGap
                + jsep.getPreferredSize().width
                + titleWidth
                + (FlamingoUtilities.hasPopupAction(commandButton) ? 1
                        + fm.getHeight() / 2 + 4 * layoutHGap
                        + jsep.getPreferredSize().width : 0);

        // height - three lines of text and two gaps between them.
        // The gap between the lines is half the main gap.
        int fontHeight = fm.getAscent() + fm.getDescent();
        int textHeight = fontHeight + layoutVGap;
        String extraText = commandButton.getExtraText();
        if ((extraText != null) && (extraText.length() > 0)) {
            textHeight += 2 * fontHeight;
        }
        return new Dimension(bx + widthMed, by
                + Math.max(this.getPreferredIconSize(commandButton), textHeight));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public Point getActionKeyTipAnchorCenterPoint(AbstractCommandButton commandButton) {
        CommandButtonLayoutInfo layoutInfo = this.getLayoutInfo(commandButton);
        if (commandButton.getComponentOrientation().isLeftToRight()) {
            // bottom-right corner of the icon area
            return new Point(layoutInfo.iconRect.x + layoutInfo.iconRect.width,
                    layoutInfo.actionClickArea.y + layoutInfo.actionClickArea.height);
        } else {
            // bottom-left corner of the icon area
            return new Point(layoutInfo.iconRect.x,
                    layoutInfo.actionClickArea.y + layoutInfo.actionClickArea.height);
        }
    }

    @Override
    public Point getPopupKeyTipAnchorCenterPoint(AbstractCommandButton commandButton) {
        CommandButtonLayoutInfo layoutInfo = this.getLayoutInfo(commandButton);
        if (commandButton.getComponentOrientation().isLeftToRight()) {
            // bottom-left corner of the popup action area
            return new Point(layoutInfo.popupActionRect.x,
                    layoutInfo.popupClickArea.y + layoutInfo.popupClickArea.height);
        } else {
            // bottom-right corner of the popup action area
            return new Point(layoutInfo.popupActionRect.x + layoutInfo.popupActionRect.width,
                    layoutInfo.popupClickArea.y + layoutInfo.popupClickArea.height);
        }
    }

    @Override
    public CommandButtonLayoutInfo getLayoutInfo(AbstractCommandButton commandButton) {
        CommandButtonLayoutInfo result = new CommandButtonLayoutInfo();

        result.actionClickArea = new Rectangle(0, 0, 0, 0);
        result.popupClickArea = new Rectangle(0, 0, 0, 0);

        Insets ins = commandButton.getInsets();

        result.iconRect = new Rectangle();
        result.popupActionRect = new Rectangle();

        int width = commandButton.getWidth();
        int height = commandButton.getHeight();

        FontMetrics fm = SubstanceMetricsUtilities.getFontMetrics(commandButton.getFont());
        int labelHeight = fm.getAscent() + fm.getDescent();

        JCommandButton.CommandButtonKind buttonKind = (commandButton instanceof JCommandButton)
                ? ((JCommandButton) commandButton).getCommandButtonKind()
                : JCommandButton.CommandButtonKind.ACTION_ONLY;

        if (buttonKind == JCommandButton.CommandButtonKind.ACTION_ONLY) {
            result.actionClickArea.x = 0;
            result.actionClickArea.y = 0;
            result.actionClickArea.width = width;
            result.actionClickArea.height = height;
            result.isTextInActionArea = true;
        }
        if (buttonKind == JCommandButton.CommandButtonKind.POPUP_ONLY) {
            result.popupClickArea.x = 0;
            result.popupClickArea.y = 0;
            result.popupClickArea.width = width;
            result.popupClickArea.height = height;
            result.isTextInActionArea = false;
        }

        JSeparator jsep = new JSeparator(JSeparator.VERTICAL);
        int layoutHGap = 2 * FlamingoUtilities.getHLayoutGap(commandButton);
        int layoutVGap = 2 * FlamingoUtilities.getVLayoutGap(commandButton);

        ResizableIcon buttonIcon = commandButton.getIcon();
        boolean ltr = commandButton.getComponentOrientation().isLeftToRight();

        if (ltr) {
            int x = ins.left;
            // medium icon, 1-line text, 1-line extra text and action arrow
            result.iconRect.x = x;
            result.iconRect.y = ins.top + layoutVGap;
            result.iconRect.width = buttonIcon.getIconWidth();
            result.iconRect.height = buttonIcon.getIconHeight();

            x += buttonIcon.getIconWidth();
            if (buttonKind == JCommandButton.CommandButtonKind.ACTION_AND_POPUP_MAIN_POPUP) {
                result.actionClickArea.x = 0;
                result.actionClickArea.y = 0;
                result.actionClickArea.width = x + layoutHGap;
                result.actionClickArea.height = height;

                result.popupClickArea.x = x + layoutHGap;
                result.popupClickArea.y = 0;
                result.popupClickArea.width = width - x - layoutHGap;
                result.popupClickArea.height = height;

                result.separatorOrientation = CommandButtonSeparatorOrientation.VERTICAL;
                result.separatorArea = new Rectangle();
                result.separatorArea.x = x + layoutHGap;
                result.separatorArea.y = 0;
                result.separatorArea.width = new JSeparator(JSeparator.VERTICAL)
                        .getPreferredSize().width;
                result.separatorArea.height = height;

                result.isTextInActionArea = false;
            }
            x += 2 * layoutHGap + jsep.getPreferredSize().width;

            TextLayoutInfo lineLayoutInfo = new TextLayoutInfo();
            lineLayoutInfo.text = commandButton.getText();
            lineLayoutInfo.textRect = new Rectangle();

            lineLayoutInfo.textRect.x = x;
            lineLayoutInfo.textRect.y = ins.top + layoutVGap / 2;
            lineLayoutInfo.textRect.width = fm.stringWidth(commandButton
                    .getText());
            lineLayoutInfo.textRect.height = labelHeight;

            result.textLayoutInfoList = new ArrayList<>();
            result.textLayoutInfoList.add(lineLayoutInfo);

            String extraText = commandButton.getExtraText();
            if ((extraText == null) || (extraText.length() == 0)) {
                lineLayoutInfo.textRect.y = (height - labelHeight) / 2;
            } else {
                AttributedString attributedDescription = new AttributedString(
                        commandButton.getExtraText());
                attributedDescription.addAttribute(TextAttribute.FONT, commandButton.getFont());
                LineBreakMeasurer lineBreakMeasurer = new LineBreakMeasurer(
                        attributedDescription.getIterator(),
                        SubstanceMetricsUtilities.getFontRenderContext(commandButton.getFont()));
                // The max width of the extra text line - need to leave
                // space for the popup arrow icon
                int maxFirstExtraLineWidth = width - x - ins.right - layoutHGap
                        - labelHeight;
                int breakIndex = lineBreakMeasurer
                        .nextOffset(maxFirstExtraLineWidth);

                TextLayoutInfo extraLineLayoutInfo1 = new TextLayoutInfo();
                extraLineLayoutInfo1.text = commandButton.getExtraText()
                        .substring(0, breakIndex);
                extraLineLayoutInfo1.textRect = new Rectangle();

                extraLineLayoutInfo1.textRect.x = x;
                extraLineLayoutInfo1.textRect.y = ins.top + layoutVGap
                        + labelHeight;
                extraLineLayoutInfo1.textRect.width = fm
                        .stringWidth(extraLineLayoutInfo1.text);
                extraLineLayoutInfo1.textRect.height = labelHeight;

                TextLayoutInfo extraLineLayoutInfo2 = new TextLayoutInfo();
                extraLineLayoutInfo2.text = commandButton.getExtraText()
                        .substring(breakIndex);
                extraLineLayoutInfo2.textRect = new Rectangle();

                extraLineLayoutInfo2.textRect.x = x;
                extraLineLayoutInfo2.textRect.y = ins.top + layoutVGap + 2
                        * labelHeight;
                extraLineLayoutInfo2.textRect.width = fm
                        .stringWidth(extraLineLayoutInfo2.text);
                extraLineLayoutInfo2.textRect.height = labelHeight;

                result.extraTextLayoutInfoList = new ArrayList<TextLayoutInfo>();
                result.extraTextLayoutInfoList.add(extraLineLayoutInfo1);
                result.extraTextLayoutInfoList.add(extraLineLayoutInfo2);
            }

            x += fm.stringWidth(commandButton.getText());
            if (buttonKind == JCommandButton.CommandButtonKind.ACTION_AND_POPUP_MAIN_ACTION) {
                // popup click areas are right aligned
                result.actionClickArea.x = 0;
                result.actionClickArea.y = 0;
                result.actionClickArea.width = width - ins.right - labelHeight;
                result.actionClickArea.height = height;

                result.popupClickArea.x = width - ins.right - labelHeight;
                result.popupClickArea.y = 0;
                result.popupClickArea.width = labelHeight + ins.right;
                result.popupClickArea.height = height;

                result.separatorOrientation = CommandButtonSeparatorOrientation.VERTICAL;
                result.separatorArea = new Rectangle();
                result.separatorArea.x = width - ins.right - labelHeight;
                result.separatorArea.y = 0;
                result.separatorArea.width = new JSeparator(JSeparator.VERTICAL)
                        .getPreferredSize().width;
                result.separatorArea.height = height;
                result.isTextInActionArea = true;
            }

            if (FlamingoUtilities.hasPopupAction(commandButton)) {
                result.popupActionRect.x = width - ins.right - labelHeight * 3
                        / 4;
                result.popupActionRect.y = (height - labelHeight) / 2 - 1;
                result.popupActionRect.width = 1 + labelHeight / 2;
                result.popupActionRect.height = labelHeight + 2;
            }
        } else {
            int x = commandButton.getWidth() - ins.right;
            // medium icon, 1-line text, 1-line extra text and action arrow
            result.iconRect.x = x - buttonIcon.getIconWidth();
            result.iconRect.y = ins.top + layoutVGap;
            result.iconRect.width = buttonIcon.getIconWidth();
            result.iconRect.height = buttonIcon.getIconHeight();

            x -= buttonIcon.getIconWidth();
            if (buttonKind == JCommandButton.CommandButtonKind.ACTION_AND_POPUP_MAIN_POPUP) {
                result.actionClickArea.x = x + layoutHGap;
                result.actionClickArea.y = 0;
                result.actionClickArea.width = width - x - layoutHGap;
                result.actionClickArea.height = height;

                result.popupClickArea.x = 0;
                result.popupClickArea.y = 0;
                result.popupClickArea.width = x + layoutHGap;
                result.popupClickArea.height = height;

                result.separatorOrientation = CommandButtonSeparatorOrientation.VERTICAL;
                result.separatorArea = new Rectangle();
                result.separatorArea.x = x + layoutHGap;
                result.separatorArea.y = 0;
                result.separatorArea.width = new JSeparator(JSeparator.VERTICAL)
                        .getPreferredSize().width;
                result.separatorArea.height = height;

                result.isTextInActionArea = false;
            }
            x -= (2 * layoutHGap + jsep.getPreferredSize().width);

            TextLayoutInfo lineLayoutInfo = new TextLayoutInfo();
            lineLayoutInfo.text = commandButton.getText();
            lineLayoutInfo.textRect = new Rectangle();

            lineLayoutInfo.textRect.width = fm.stringWidth(commandButton
                    .getText());
            lineLayoutInfo.textRect.x = x - lineLayoutInfo.textRect.width;
            lineLayoutInfo.textRect.y = ins.top + layoutVGap / 2;
            lineLayoutInfo.textRect.height = labelHeight;

            result.textLayoutInfoList = new ArrayList<TextLayoutInfo>();
            result.textLayoutInfoList.add(lineLayoutInfo);

            String extraText = commandButton.getExtraText();
            if ((extraText == null) || (extraText.length() == 0)) {
                lineLayoutInfo.textRect.y = (height - labelHeight) / 2;
            } else {
                AttributedString attributedDescription = new AttributedString(
                        commandButton.getExtraText());
                attributedDescription.addAttribute(TextAttribute.FONT, commandButton.getFont());
                LineBreakMeasurer lineBreakMeasurer = new LineBreakMeasurer(
                        attributedDescription.getIterator(),
                        SubstanceMetricsUtilities.getFontRenderContext(commandButton.getFont()));
                // The max width of the extra text line - need to leave
                // space for the popup arrow icon
                int maxFirstExtraLineWidth = x - ins.left - layoutHGap - labelHeight;
                int breakIndex = lineBreakMeasurer
                        .nextOffset(maxFirstExtraLineWidth);

                TextLayoutInfo extraLineLayoutInfo1 = new TextLayoutInfo();
                extraLineLayoutInfo1.text = commandButton.getExtraText()
                        .substring(0, breakIndex);
                extraLineLayoutInfo1.textRect = new Rectangle();

                extraLineLayoutInfo1.textRect.width = fm
                        .stringWidth(extraLineLayoutInfo1.text);
                extraLineLayoutInfo1.textRect.x = x
                        - extraLineLayoutInfo1.textRect.width;
                extraLineLayoutInfo1.textRect.y = ins.top + layoutVGap
                        + labelHeight;
                extraLineLayoutInfo1.textRect.height = labelHeight;

                TextLayoutInfo extraLineLayoutInfo2 = new TextLayoutInfo();
                extraLineLayoutInfo2.text = commandButton.getExtraText()
                        .substring(breakIndex);
                extraLineLayoutInfo2.textRect = new Rectangle();

                extraLineLayoutInfo2.textRect.width = fm
                        .stringWidth(extraLineLayoutInfo2.text);
                extraLineLayoutInfo2.textRect.x = x
                        - extraLineLayoutInfo2.textRect.width;
                extraLineLayoutInfo2.textRect.y = ins.top + layoutVGap + 2
                        * labelHeight;
                extraLineLayoutInfo2.textRect.height = labelHeight;

                result.extraTextLayoutInfoList = new ArrayList<TextLayoutInfo>();
                result.extraTextLayoutInfoList.add(extraLineLayoutInfo1);
                result.extraTextLayoutInfoList.add(extraLineLayoutInfo2);
            }

            if (buttonKind == JCommandButton.CommandButtonKind.ACTION_AND_POPUP_MAIN_ACTION) {
                // popup click areas are left aligned
                result.actionClickArea.x = labelHeight + ins.left;
                result.actionClickArea.y = 0;
                result.actionClickArea.width = width - ins.right - labelHeight;
                result.actionClickArea.height = height;

                result.popupClickArea.x = 0;
                result.popupClickArea.y = 0;
                result.popupClickArea.width = ins.left + labelHeight;
                result.popupClickArea.height = height;

                result.separatorOrientation = CommandButtonSeparatorOrientation.VERTICAL;
                result.separatorArea = new Rectangle();
                result.separatorArea.x = labelHeight + ins.left;
                result.separatorArea.y = 0;
                result.separatorArea.width = new JSeparator(JSeparator.VERTICAL)
                        .getPreferredSize().width;
                result.separatorArea.height = height;

                result.isTextInActionArea = true;
            }

            if (FlamingoUtilities.hasPopupAction(commandButton)) {
                result.popupActionRect.x = ins.left + labelHeight / 4;
                result.popupActionRect.y = (height - labelHeight) / 2 - 1;
                result.popupActionRect.width = 1 + labelHeight / 2;
                result.popupActionRect.height = labelHeight + 2;
            }
        }
        return result;
    }
}
