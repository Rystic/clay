package org.rystic.city.processes;

import org.rystic.screens.AbstractScreen;

public abstract class AbstractProcess
{
	public AbstractProcess(AbstractScreen homeScreen_)
	{
		_homeScreen = homeScreen_;
	}

	public void execute()
	{

	}

	protected AbstractScreen _homeScreen;

}
