package city.generics.objects;

import city.generics.GenericBehavior;

public class ConversionBehavior extends Behavior
{
	public ConversionBehavior(String conversionKey_, GenericBehavior behavior_, Object... params_)
	{
		super(behavior_, params_);
		_conversionKey = conversionKey_;
	}
	
	public String getConversionKey()
	{
		return _conversionKey;
	}
	
	private String _conversionKey;
}