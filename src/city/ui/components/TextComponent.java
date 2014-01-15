package city.ui.components;

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
		if (text_ == null) text_ = "";
		else _text = text_;
	}

	public String getText()
	{
		return _text;
	}

	private String _text;
}
