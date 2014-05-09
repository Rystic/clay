package city.controllers;

import java.util.ArrayList;
import java.util.List;

import main.ClayConstants;
import models.CityModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import screens.AbstractScreen;
import screens.CityScreen;
import city.processes.AbstractProcess;
import city.ui.events.InterfaceMouseEvent;
import city.ui.menus.AbstractMenu;

public class CityInterfaceController extends AbstractProcess
{

	public CityInterfaceController(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_cityScreen = (CityScreen) _homeScreen;
		_pressedKeys = new ArrayList<Integer>();
		_model = _cityScreen.getModel();
		_menuPointer = 0;
	}

	@Override
	public void execute()
	{
		List<AbstractMenu> menus = _cityScreen.getMenus();
		AbstractMenu menu = menus.get(_menuPointer);
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
			_menuPointer++;
			if (_menuPointer == menus.size())
				_menuPointer = 0;
			_model.setSelectedMenu(menus.get(_menuPointer));
			_spaceDown = true;
		}
		else if (!Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			_spaceDown = false;
	}

	private int _menuPointer;

	private boolean _leftMouseDown;
	private boolean _spaceDown;

	private List<Integer> _pressedKeys;

	private CityScreen _cityScreen;
	private CityModel _model;
}
