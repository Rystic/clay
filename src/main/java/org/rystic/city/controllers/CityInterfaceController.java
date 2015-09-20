package org.rystic.city.controllers;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.rystic.city.processes.AbstractProcess;
import org.rystic.city.ui.events.InterfaceMouseEvent;
import org.rystic.city.ui.menus.AbstractMenu;
import org.rystic.city.util.KeyboardHandler;
import org.rystic.city.util.MouseHandler;
import org.rystic.main.ClayConstants;
import org.rystic.main.ClayMain;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;
import org.rystic.screens.CityScreen;

import com.sun.glass.events.KeyEvent;

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
			if (!_pressedKeys.contains(key) && KeyboardHandler.isKeyDown(key))
			{
				_pressedKeys.add(key);
				menu.handleKeyEvent(key);
			}
		}
		int wheel = GLFW.GLFW_MOUSE_BUTTON_3;
		if (wheel != 0)
			menu.handleMouseWheel(wheel > 0);
		if (GLFW.glfwGetMouseButton(ClayMain._mainDisplay, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS)
		{
			if (!_leftMouseDown)
			{
				menu.handleMouseEvent(new InterfaceMouseEvent(MouseHandler.x
						- ClayConstants.DEFAULT_MAP_WIDTH
						+ ClayConstants.DEFAULT_INTERFACE_WIDTH, MouseHandler.y));
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
				if (!KeyboardHandler.isKeyDown(key))
					liftedKeys.add(key);
			}
			_pressedKeys.removeAll(liftedKeys);
		}
		if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_SPACE) && !_spaceDown)
		{
			_model.moveMenuRight();
			_spaceDown = true;
		}
		else if (!KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_SPACE))
			_spaceDown = false;
	}

	private boolean _leftMouseDown;
	private boolean _spaceDown;

	private List<Integer> _pressedKeys;

	private CityScreen _cityScreen;
	private CityModel _model;
}
