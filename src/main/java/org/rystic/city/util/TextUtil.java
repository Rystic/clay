package org.rystic.city.util;

import org.newdawn.slick.TrueTypeFont;
import org.rystic.city.ui.menus.components.TextComponent;

@SuppressWarnings("deprecation")
public class TextUtil
{

	static
	{
		// load a default java font
//		Font awtFont = new Font("Times New Roman", Font.BOLD, 18);
//		_font = new TrueTypeFont(awtFont, false);
//
//		// load font from a .ttf file
//		try
//		{
//			InputStream inputStream = ResourceLoader
//					.getResourceAsStream("Arial-Black.ttf");
//
//			Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
//			awtFont2 = awtFont2.deriveFont(24f); // set font size
//			_font2 = new TrueTypeFont(awtFont2, false);
//
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//		}

	}

	public static int drawString(TextComponent t_, int x_, int y_)
	{
		float xf = (float) x_;
		float yf = (float) y_;

		int lines = 1;
		
//		String[] text = t_.getText().split(" ");
//		StringBuilder builder = new StringBuilder(30);
//		for (int i = 0; i < text.length; i++)
//		{
//			if (builder.length() + text[i].length() + 1 > MAX_LINE_LENGTH)
//			{
//				_font.drawString(xf, yf, builder.toString(), t_.getColor());
//				builder.setLength(0);
//				yf += LINE_BREAK_Y_INCREMENT;
//				lines++;
//			}
//			builder.append(text[i]);
//			builder.append(" ");
//		}
//		_font.drawString(xf, yf, builder.toString(), t_.getColor());
//		_font2.drawString(0, 0, "", Color.yellow); // If this line isn't here,
//													// nothing draws. I have no
//													// idea why.
		return lines;
	}

	private static TrueTypeFont _font;
	private static TrueTypeFont _font2;
	private static final float LINE_BREAK_Y_INCREMENT = 24f;
	private static final int MAX_LINE_LENGTH = 33;
}