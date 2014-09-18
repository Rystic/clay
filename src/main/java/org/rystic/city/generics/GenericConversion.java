package org.rystic.city.generics;

import org.rystic.city.generics.util.FieldParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GenericConversion
{
	public GenericConversion(Node node_)
	{
		Element eElement = (Element) node_;
		_conversionName = eElement.getAttribute("ConversionName");
		_conversionTag = eElement.getAttribute("ConversionTag");
		_conversionBuilding = eElement.getAttribute("ConversionBuilding");
		_conversionInput = eElement.getAttribute("ConversionInput");
		_converstionOutput = eElement.getAttribute("ConversionOutput");
		
		_heat = FieldParser.parseInt(eElement.getAttribute("Heat"));
	}
	
	public String getConversionName()
	{
		return _conversionName;
	}

	public String getConversionTag()
	{
		return _conversionTag;
	}
	
	public String getConversionBuilding()
	{
		return _conversionBuilding;
	}
	
	public String getConversionInput()
	{
		return _conversionInput;
	}

	public String getConverstionOutput()
	{
		return _converstionOutput;
	}
	
	public int getHeat()
	{
		return _heat;
	}

	private final String _conversionName;
	private final String _conversionTag;
	private final String _conversionBuilding;
	private final String _conversionInput;
	private final String _converstionOutput;
	
	private final int _heat;
}
