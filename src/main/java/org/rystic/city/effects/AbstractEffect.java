package org.rystic.city.effects;

import org.rystic.screens.AbstractScreen;

public abstract class AbstractEffect
{
	public AbstractEffect(AbstractScreen homeScreen_)
	{
		_homeScreen = homeScreen_;
	}
	
	public void init()
	{
		
	}
	
	public void executeEffect()
	{
		
	}
	
	protected AbstractScreen _homeScreen;
}
