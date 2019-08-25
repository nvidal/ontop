package it.unibz.inf.ontop.protege.utils;

/*
 * #%L
 * ontop-protege
 * %%
 * Copyright (C) 2009 - 2013 KRDB Research Centre. Free University of Bozen Bolzano.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.atom.TargetAtom;
import it.unibz.inf.ontop.spec.mapping.PrefixManager;
import it.unibz.inf.ontop.spec.mapping.pp.SQLPPTriplesMap;
import it.unibz.inf.ontop.spec.mapping.OBDASQLQuery;
import it.unibz.inf.ontop.protege.core.OBDAModel;
import it.unibz.inf.ontop.spec.mapping.serializer.SourceQueryRenderer;
import it.unibz.inf.ontop.spec.mapping.serializer.TargetQueryRenderer;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class OBDAMappingListRenderer implements ListCellRenderer {

	private PrefixManager prefixManager;
	
	private JTextPane mapTextPane;
	private JTextPane trgQueryTextPane;
	private JTextPane srcQueryTextPane;
	private JPanel renderingComponent;

	private int preferredWidth;
	private int minTextHeight;

	private Font plainFont;

	public static final Color SELECTION_BACKGROUND = UIManager.getDefaults().getColor("List.selectionBackground");
	public static final Color SELECTION_FOREGROUND = UIManager.getDefaults().getColor("List.selectionForeground");
	public static final Color FOREGROUND = UIManager.getDefaults().getColor("List.foreground");

	private int plainFontHeight;
	private Style plainStyle;
	private Style boldStyle;
	private Style nonBoldStyle;
	private Style selectionForeground;
	private Style foreground;
	private Style fontSizeStyle;

	private int width;
	private int plainFontWidth;

	private Style background;
	private Style alignment;
	private JPanel trgQueryPanel;
	private JPanel srcQueryPanel;
	private JPanel mapPanel;
	private JPanel mainPanel;
	private QueryPainter painter;
	private SQLQueryPainter sqlpainter;

	public OBDAMappingListRenderer(OBDAModel obdaModel) {

		prefixManager = obdaModel.getMutablePrefixManager();

		trgQueryTextPane = new JTextPane();
		painter = new QueryPainter(obdaModel, trgQueryTextPane);

		trgQueryTextPane.setMargin(new Insets(4, 4, 4, 4));

		srcQueryTextPane = new JTextPane();
		srcQueryTextPane.setMargin(new Insets(4, 4, 4, 4));
		sqlpainter = new SQLQueryPainter(srcQueryTextPane);

		mapTextPane = new JTextPane();
		mapTextPane.setMargin(new Insets(4, 4, 4, 4));

		trgQueryPanel = new JPanel();
		trgQueryPanel.add(trgQueryTextPane);

		srcQueryPanel = new JPanel();
		srcQueryPanel.add(srcQueryTextPane);

		mapPanel = new JPanel();
		mapPanel.add(mapTextPane);

		mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));


		mainPanel.add(this.mapTextPane);
		mainPanel.add(this.trgQueryTextPane);
		mainPanel.add(srcQueryTextPane);

		mainPanel.setBorder(BorderFactory.createLineBorder(new Color(192, 192, 192), 1));

		mainPanel.setOpaque(true);
		mapTextPane.setOpaque(false);
		srcQueryTextPane.setOpaque(false);
		trgQueryTextPane.setOpaque(false);

		SpringLayout layout = new SpringLayout();

		renderingComponent = new JPanel(layout);
		renderingComponent.setBorder(null);
		renderingComponent.add(mainPanel);

		layout.putConstraint(SpringLayout.WEST, mainPanel, 2, SpringLayout.WEST, renderingComponent);
		layout.putConstraint(SpringLayout.NORTH, mainPanel, 2, SpringLayout.NORTH, renderingComponent);

		renderingComponent.setBackground(Color.white);

		prepareStyles();
		setupFont();
	}

	private void resetStyles(StyledDocument doc) {
		doc.setParagraphAttributes(0, doc.getLength(), plainStyle, true);
		StyleConstants.setFontSize(fontSizeStyle, 14);
		Font f = plainFont;
		StyleConstants.setFontFamily(fontSizeStyle, f.getFamily());
		doc.setParagraphAttributes(0, doc.getLength(), fontSizeStyle, false);
		setupFont();
	}

	private void prepareStyles() {
		StyledDocument doc = trgQueryTextPane.getStyledDocument();
		plainStyle = doc.addStyle("PLAIN_STYLE", null);
		StyleConstants.setItalic(plainStyle, false);

		boldStyle = doc.addStyle("BOLD_STYLE", null);
		StyleConstants.setBold(boldStyle, true);

		nonBoldStyle = doc.addStyle("NON_BOLD_STYLE", null);
		StyleConstants.setBold(nonBoldStyle, false);

		selectionForeground = doc.addStyle("SEL_FG_STYPE", null);
		// we know that it is possible for SELECTION_FOREGROUND to be null
		// and an exception here means that Protege doesn't start
		if (selectionForeground != null && SELECTION_FOREGROUND != null) {
			StyleConstants.setForeground(selectionForeground, SELECTION_FOREGROUND);
		}

		foreground = doc.addStyle("FG_STYLE", null);
		if (foreground != null && FOREGROUND != null) {
			StyleConstants.setForeground(foreground, FOREGROUND);
		}

		background = doc.addStyle("BG_STYLE", null);
		if (background != null) {
			// StyleConstants.setBackground(background, Color.WHITE);
		}

		alignment = doc.addStyle("ALIGNMENT_STYLE", null);
		if (alignment != null) {
			StyleConstants.setAlignment(alignment, StyleConstants.ALIGN_LEFT);
		}

		fontSizeStyle = doc.addStyle("FONT_SIZE", null);
		StyleConstants.setFontSize(fontSizeStyle, 40);

	}

	public void setPreferredWidth(int preferredWidth) {
		this.preferredWidth = preferredWidth;
	}

	private void setupFont() {
		plainFont = new Font("Lucida Grande", Font.PLAIN, 14);
		plainFontHeight = trgQueryTextPane.getFontMetrics(plainFont).getHeight();

		int[] widths = trgQueryTextPane.getFontMetrics(plainFont).getWidths();
		int sum = 0;
		for (int i = 0; i < widths.length; i++) {
			int j = widths[i];
			sum += j;
		}
		plainFontWidth = sum / widths.length;
		trgQueryTextPane.setFont(plainFont);
	}


	int textWidth;
	int textidHeight;
	int textTargetHeight;
	int textSourceHeight;
	int height;
	int totalWidth;
	int totalHeight;

	/***
	 * Now we compute the sizes of each of the components, including the text
	 * panes. Note that for the target text pane we need to compute the number
	 * of lines that the text will require, and the height of these lines
	 * acordingly. Right now this is done in an approximate way using
	 * FontMetris. It works fine most of the time, however, the algorithm needs
	 * to be improved. For the source query we have an even cruder
	 * implementation that needs to be adjusted in the same way as the one for
	 * the target query (to compute the number of lines using FontMetris instead
	 * of "maxChars".
	 */
	private void computeDimensions(JPanel parent) {

		Insets rcInsets = renderingComponent.getInsets();

		if (preferredWidth != -1) {
			textWidth = preferredWidth  - rcInsets.left - rcInsets.right - 10;

			int maxChars = (textWidth / (plainFontWidth)) - 10;

			String trgQuery = trgQueryTextPane.getText();

			// int linesTarget = (int) (trgQuery.length() / maxChars) + 1;

			/***
			 * Computing the number of lines for the target query base on the
			 * FontMetris. We are going to simulate a "wrap" operation over the
			 * text of the target query in order to be able to count number of
			 * lines.
			 */
			String[] split = trgQuery.split(" ");
			int currentWidth = 0;
			StringBuffer currentLine = new StringBuffer();
			int linesTarget = 1;
			FontMetrics m = trgQueryTextPane.getFontMetrics(plainFont);
			for (String splitst : split) {
				boolean space = false;
				if (currentLine.length() != 0) {
					currentLine.append(" ");
					space = true;
				}

				int newSize = m.stringWidth((space ? " " : "") + splitst);
				if (currentWidth + newSize <= textWidth) {
					/* No need to wrap */
					currentLine.append(splitst);
					currentWidth += newSize;
				} else {
					/* we need to spit, all the sizes and string reset */
					currentLine.setLength(0);
					currentLine.append(splitst);
					currentWidth = m.stringWidth(splitst);
					linesTarget += 1;
				}
			}

			String srcQuery = srcQueryTextPane.getText();
			int linesSource = (int) (srcQuery.length() / maxChars) + 1;

			textTargetHeight = m.getHeight() * linesTarget; // target
			textSourceHeight = minTextHeight * linesSource; // source
			textidHeight = minTextHeight;

			trgQueryTextPane.getUI().getRootView(trgQueryTextPane);
			// v.setSize(textWidth, Integer.MAX_VALUE);
			// v.setSize(textWidth, 0);
			trgQueryTextPane.setPreferredSize(new Dimension(textWidth, textTargetHeight));

			srcQueryTextPane.getUI().getRootView(srcQueryTextPane);
			// v.setSize(textWidth, Integer.MAX_VALUE);
			// v.setSize(textWidth, 0);
			srcQueryTextPane.setPreferredSize(new Dimension(textWidth, textSourceHeight));

			mapTextPane.getUI().getRootView(mapTextPane);
			// v.setSize(textWidth, Integer.MAX_VALUE);
			// v.setSize(textWidth, 0);
			mapTextPane.setPreferredSize(new Dimension(textWidth, textidHeight));

			width = preferredWidth;
		} else {

			/***
			 * This block is not used
			 */
			textWidth = mapTextPane.getPreferredSize().width;

			textidHeight = mapTextPane.getPreferredSize().height;
			textSourceHeight = srcQueryTextPane.getPreferredSize().height;
			textTargetHeight = trgQueryTextPane.getPreferredSize().width;

			width = textWidth ;
		}

			height = textidHeight;

		int minHeight = minTextHeight;
		if (height < minHeight) {
			height = minHeight;
		}
		// totalWidth = width + rcInsets.left + rcInsets.right;
		totalWidth = width;
		// totalHeight = textidHeight + textSourceHeight + textTargetHeight +
		// rcInsets.top + rcInsets.bottom;
		totalHeight = textidHeight + textSourceHeight + textTargetHeight;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		//preferredWidth = list.getParent().getWidth(); //TODO: UNDERSTAND WHY BROKE
		preferredWidth = list.getParent().getParent().getWidth();

		minTextHeight = this.plainFontHeight + 6;
		Component c = prepareRenderer((SQLPPTriplesMap) value, isSelected);
		return c;
	}

	private Component prepareRenderer(SQLPPTriplesMap value, boolean isSelected) {
		renderingComponent.setOpaque(false);
		prepareTextPanes(value, isSelected);

		if (isSelected) {
			mainPanel.setBackground(SELECTION_BACKGROUND);
		} else {
			mainPanel.setBackground(new Color(240, 245, 240));
		}
		computeDimensions(renderingComponent);

		trgQueryTextPane.setPreferredSize(new Dimension(textWidth, textTargetHeight));
		srcQueryTextPane.setPreferredSize(new Dimension(textWidth, textSourceHeight));
		mapTextPane.setPreferredSize(new Dimension(textWidth , textidHeight));

		mainPanel.setPreferredSize(new Dimension(totalWidth - 4, this.totalHeight + 2));
		renderingComponent.setPreferredSize(new Dimension(totalWidth, this.totalHeight + 5));

		boolean debugColors = false;

		if (debugColors) {
			mapTextPane.setOpaque(true);
			srcQueryTextPane.setOpaque(true);
			trgQueryTextPane.setOpaque(true);

			mainPanel.setBackground(Color.yellow);
			renderingComponent.setBackground(Color.yellow);
			trgQueryTextPane.setBackground(Color.ORANGE);
			trgQueryTextPane.setOpaque(true);
			trgQueryPanel.setBackground(Color.orange);
			srcQueryTextPane.setBackground(Color.pink);
			srcQueryPanel.setBackground(Color.yellow);
			mapTextPane.setBackground(Color.green);
			mapPanel.setBackground(Color.BLACK);

		}
		try {
			painter.recolorQuery();
			sqlpainter.recolorQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
		trgQueryTextPane.setBorder(null);
		renderingComponent.revalidate();
		return renderingComponent;
	}

	private void prepareTextPanes(SQLPPTriplesMap value, boolean selected) {
		ImmutableList<TargetAtom> targetQuery = value.getTargetAtoms();
		String trgQuery = TargetQueryRenderer.encode(targetQuery, prefixManager);
 		trgQueryTextPane.setText(trgQuery);

 		OBDASQLQuery sourceQuery = value.getSourceQuery();
		String srcQuery = SourceQueryRenderer.encode(sourceQuery);
		srcQueryTextPane.setText(srcQuery);
		
		mapTextPane.setText(value.getId());

		StyledDocument doc = trgQueryTextPane.getStyledDocument();
		resetStyles(doc);
		doc.setParagraphAttributes(0, doc.getLength(), background, false);
		doc.setParagraphAttributes(0, doc.getLength(), alignment, false);
		if (selected) {
			doc.setParagraphAttributes(0, doc.getLength(), selectionForeground, false);
		} else {
			doc.setParagraphAttributes(0, doc.getLength(), foreground, false);
		}

		doc = srcQueryTextPane.getStyledDocument(); // reuse variable
		resetStyles(doc);
		doc.setParagraphAttributes(0, doc.getLength(), background, false);
		doc.setParagraphAttributes(0, doc.getLength(), alignment, false);
		if (selected) {
			doc.setParagraphAttributes(0, doc.getLength(), selectionForeground,	false);
		} else {
			doc.setParagraphAttributes(0, doc.getLength(), foreground, false);
		}

		doc = mapTextPane.getStyledDocument(); // reuse variable
		resetStyles(doc);
		doc.setParagraphAttributes(0, doc.getLength(), boldStyle, false);
		doc.setParagraphAttributes(0, doc.getLength(), background, false);
		doc.setParagraphAttributes(0, doc.getLength(), alignment, false);
		if (selected) {
			doc.setParagraphAttributes(0, doc.getLength(), selectionForeground,	false);
		} else {
			doc.setParagraphAttributes(0, doc.getLength(), foreground, false);
		}
	}
}
