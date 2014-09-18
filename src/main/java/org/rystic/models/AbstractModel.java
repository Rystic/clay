package org.rystic.models;

import org.rystic.screens.AbstractScreen;

public class AbstractModel
{
	public AbstractModel(AbstractScreen homeScreen_)
	{
		_homeScreen = homeScreen_;
	}

	protected AbstractScreen _homeScreen;
}
