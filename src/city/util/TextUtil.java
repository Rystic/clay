package city.util;

import java.awt.Font;
import java.io.InputStream;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import city.ui.menus.components.TextComponent;

@SuppressWarnings("deprecation")
public class TextUtil
{
	private static TrueTypeFont _font;
	private static TrueTypeFont _font2;

	static
	{
		// load a default java font
		Font awtFont = new Font("Times New Roman", Font.BOLD, 18);
		_font = new TrueTypeFont(awtFont, false);

		// load font from a .ttf file
		try
		{
			InputStream inputStream = ResourceLoader
					.getResourceAsStream("Arial-Black.ttf");

			Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			awtFont2 = awtFont2.deriveFont(24f); // set font size
			_font2 = new TrueTypeFont(awtFont2, false);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public static void drawString(TextComponent _t, int _x, int _y)
	{
		float xf = (float) _x;
		float yf = (float) _y;
		_font.drawString(xf, yf, _t.getText(), _t.getTextColor());
		_font2.drawString(0, 0, "", Color.yellow); // If this line isn't here, nothing draws. I have no idea why.
	}

}