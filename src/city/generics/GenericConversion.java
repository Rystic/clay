package city.generics;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GenericConversion
{
	public GenericConversion(Node node_)
	{
		Element eElement = (Element) node_;
		_conversionName = eElement.getAttribute("ConversionName");
		_conversionTag = eElement.getAttribute("ConversionTag");
		_conversionInput = eElement.getAttribute("ConversionInput");
		_converstionOutput = eElement.getAttribute("ConversionOutput");
	}
	
	public String getConversionName()
	{
		return _conversionName;
	}

	public String getConversionTag()
	{
		return _conversionTag;
	}
	
	public String getConversionInput()
	{
		return _conversionInput;
	}

	public String getConverstionOutput()
	{
		return _converstionOutput;
	}

	private final String _conversionName;
	private final String _conversionTag;
	private final String _conversionInput;
	private final String _converstionOutput;
}
