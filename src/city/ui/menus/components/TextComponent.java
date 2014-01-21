package city.ui.menus.components;

import java.awt.Color;

public class TextComponent extends AbstractComponent
{
	public TextComponent(int x, int y)
	{
		super(x, y, 0, 0);
		_text = "";
	}

	public TextComponent(int x, int y, String text_)
	{
		super(x, y, 0, 0);
		setText(text_);
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

	public void setTextColor(Color textColor_)
	{
		_textColor = textColor_;
	}

	public Color getTextColor()
	{
		return _textColor == null ? DEFAULT_COLOR : _textColor;
	}

	private String _text;

	private Color _textColor;

	private static final Color DEFAULT_COLOR = new Color(1.0f, 1.0f, 1.0f);
}
