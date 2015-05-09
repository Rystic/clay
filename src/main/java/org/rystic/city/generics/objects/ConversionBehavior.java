package org.rystic.city.generics.objects;

import org.rystic.city.generics.GenericBehavior;
import org.rystic.city.processes.GolemBehaviorProcess;

public class ConversionBehavior extends Behavior
{
	public ConversionBehavior(String conversionKey_, GenericBehavior behavior_, GolemBehaviorProcess behaviorProcess_, Object... params_)
	{
		super(behavior_, behaviorProcess_, params_);
		_conversionKey = conversionKey_;
	}
	
	public String getConversionKey()
	{
		return _conversionKey;
	}
	
	private String _conversionKey;
}