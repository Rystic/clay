package org.rystic.city.controllers;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.rystic.city.processes.AbstractProcess;
import org.rystic.city.ui.events.InterfaceMouseEvent;
import org.rystic.city.ui.menus.AbstractMenu;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;
import org.rystic.screens.CityScreen;

public class CityInterfaceController extends AbstractProcess
{

	public CityInterfaceController(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_cityScreen = (CityScreen) _homeScreen;
		_pressedKeys = new ArrayList<Integer>();
		_model = _cityScreen.getModel();
	}

	@Override
	public void execute()
	{
		AbstractMenu menu = _model.getSelectedMenu();
		for (Integer key : menu.getHotKeys())
		{
			if (!_pressedKeys.contains(key) && Keyboard.isKeyDown(key))
			{
				_pressedKeys.add(key);
				menu.handleKeyEvent(key);
			}
		}
		int wheel = Mouse.getDWheel();
		if (wheel != 0)
			menu.handleMouseWheel(wheel > 0);
		if (Mouse.isButtonDown(0))
		{
			if (!_leftMouseDown)
			{
				menu.handleMouseEvent(new InterfaceMouseEvent(Mouse.getX()
						- Display.getWidth()
						+ ClayConstants.DEFAULT_INTERFACE_WIDTH, Mouse.getY()));
				_leftMouseDown = true;
			}
		}
		else
		{
			_leftMouseDown = false;
		}
		if (_pressedKeys.size() > 0)
		{
			List<Integer> liftedKeys = new ArrayList<Integer>();
			for (Integer key : _pressedKeys)
			{
				if (!Keyboard.isKeyDown(key))
					liftedKeys.add(key);
			}
			_pressedKeys.removeAll(liftedKeys);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !_spaceDown)
		{
			_model.moveMenuRight();
			_spaceDown = true;
		}
		else if (!Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			_spaceDown = false;
	}

	private boolean _leftMouseDown;
	private boolean _spaceDown;

	private List<Integer> _pressedKeys;

	private CityScreen _cityScreen;
	private CityModel _model;
}
