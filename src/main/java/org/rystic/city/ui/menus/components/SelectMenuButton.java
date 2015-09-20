

package org.rystic.city.ui.menus.components;

import java.nio.ByteBuffer;

import org.rystic.screens.AbstractScreen;
import org.rystic.screens.CityScreen;

public class SelectMenuButton extends AbstractButton
{
	public SelectMenuButton(AbstractScreen homeScreen_, int x_, int y_, int width_, int height_,
			ByteBuffer texture_, boolean leftArrow_)
	{
		super(x_, y_, width_, height_);
		_homeScreen = homeScreen_;
		setTexture(texture_);
		setDrawRatio(.77f);
		_leftArrow = leftArrow_;
	}
	
	@Override
	public void clicked()
	{
		if (_leftArrow)
			((CityScreen) _homeScreen).getModel().moveMenuLeft();
		else
			((CityScreen) _homeScreen).getModel().moveMenuRight();
	}

	private AbstractScreen _homeScreen;
	private boolean _leftArrow;
}
