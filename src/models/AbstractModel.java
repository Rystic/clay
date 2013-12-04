package models;

import screens.AbstractScreen;

public class AbstractModel
{
	public AbstractModel(AbstractScreen homeScreen_)
	{
		_homeScreen = homeScreen_;
	}

	protected AbstractScreen _homeScreen;
}
