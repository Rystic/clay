package city.generics.entities;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import city.generics.objects.Item;
import main.ClayConstants;
import models.AbstractModel;
import screens.AbstractScreen;


public abstract class AbstractEntity
{
	public AbstractEntity(Point location_, AbstractScreen homeScreen_)
	{
		_location = location_;
		_homeScreen = homeScreen_;
		_heldItems = new ArrayList<Item>();
	}

	public double getX()
	{
		return _location.getX();
	}

	public double getY()
	{
		return _location.getY();
	}

	public int getGridX()
	{
		return (int) _location.getX() / ClayConstants.TILE_X;
	}

	public int getGridY()
	{
		return (int) _location.getY() / ClayConstants.TILE_Y;
	}
	
	public Point getPoint()
	{
		return _location;
	}
	
	public void generate(Item item_)
	{
		_heldItems.add(item_);
		calculateTexture();
	}

	public void generate(List<Item> items_)
	{
		_heldItems.addAll(items_);
		calculateTexture();
	}
	
	public boolean isHolding(Item item_)
	{
		return _heldItems.contains(item_);
	}
	
	public boolean isHolding(List<Item> items_)
	{
		return _heldItems.containsAll(items_);
	}
	
	public boolean consume(Item item_)
	{
		boolean success =  _heldItems.remove(item_);
		calculateTexture();
		return success;
	}
	
	public boolean consume(List<Item> items_)
	{
		boolean success =  _heldItems.removeAll(items_);
		calculateTexture();
		return success;
	}
	
	public List<Item> getCopyOfHeldItems()
	{
		return new ArrayList<Item>(_heldItems);
	}
	
	public List<Item> consumeAllItemType(Item item_)
	{
		List<Item> consumed = new ArrayList<Item>();
		while (_heldItems.remove(item_))
		{
			consumed.add(item_);
		}
		calculateTexture();
		return consumed;
	}
	
	public void calculateTexture()
	{
		
	}
	
	public AbstractModel getModel()
	{
		return _model;
	}
	
	public AbstractScreen getHomeScreen()
	{
		return _homeScreen;
	}

	protected AbstractModel _model;
	
	protected AbstractScreen _homeScreen;
	
	protected Point _location;

	protected List<Item> _heldItems;
}
