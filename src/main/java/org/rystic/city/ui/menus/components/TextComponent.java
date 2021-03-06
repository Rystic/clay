package org.rystic.city.ui.menus.components;

import org.newdawn.slick.Color;


public class TextComponent extends AbstractComponent
{
	public TextComponent(int x, int y)
	{
		super(x, y, 0, 0);
		_text = "";
		_textColor = Color.white;
	}

	public TextComponent(int x, int y, String text_)
	{
		super(x, y, 0, 0);
		setText(text_);
	}
	
	public TextComponent(int x, int y, String text_, Color textColor_)
	{
		super(x, y, 0, 0);
		setText(text_);
		setColor(textColor_);
	}

	public void setText(String text_)
	{
		if (text_ == null)
			text_ = "";
		else
			_text = text_;
	}

	public String getText()
	{
		return _text;
	}

	public void setColor(Color textColor_)
	{
		_textColor = textColor_;
	}

	public Color getColor()
	{
		return _textColor == null ? DEFAULT_COLOR : _textColor;
	}

	private String _text;

	private Color _textColor;

	private static final Color DEFAULT_COLOR = new Color(1.0f, 1.0f, 1.0f);
}
