package org.rystic.city.ui.menus.components;

import org.newdawn.slick.opengl.Texture;
import org.rystic.city.generics.GenericBuilding;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.main.ClayConstants;

public class SelectBuildingButton extends AbstractButton
{
	public SelectBuildingButton(int x_, int y_, int width_, int height_,
			GenericBuilding building_)
	{
		super(x_, y_, width_, height_);
		_position = ClayConstants.DEFAULT_BUILDING_POSITION;
		setGenericBuilding(building_);
	}
	
	public SelectBuildingButton(int x_, int y_, int width_, int height_,
			GenericBuilding building_, String position_)
	{
		super(x_, y_, width_, height_);
		_position = position_;
		setGenericBuilding(building_);
	}
	
	@Override
	public Texture getTexture()
	{
		if (_building == null) return BuildingData._unbuiltTexture;
		return _building.getTexture(ClayConstants.T_STATE_DEFAULT, _position);
	}
	
	public int getIdentifier()
	{
		if (_building == null) return -1;
		return _building.getBuildingIdentifier();
	}
	
	public void setGenericBuilding(GenericBuilding building_)
	{
		_building = building_;
	}

	@Override
	public void clicked()
	{
	}

	private GenericBuilding _building;
	private String _position;
}
