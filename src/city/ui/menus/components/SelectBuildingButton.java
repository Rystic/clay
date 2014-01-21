package city.ui.menus.components;

import main.ClayConstants;

import org.newdawn.slick.opengl.Texture;

import city.generics.GenericBuilding;
import city.generics.data.BuildingData;

public class SelectBuildingButton extends AbstractButton
{
	public SelectBuildingButton(int x_, int y_, int width_, int height_,
			GenericBuilding building_)
	{
		super(x_, y_, width_, height_);
		setGenericBuilding(building_);
	}
	
	@Override
	public Texture getTexture()
	{
		if (_building == null) return BuildingData._unbuiltTexture;
		return _building.getTexture(ClayConstants.T_STATE_DEFAULT, ClayConstants.DEFAULT_BUILDING_POSITION);
	}
	
	public int getIdentifier()
	{
		if (_building == null) return -1;
		return _building.getBuildingIdentifier();
	}
	
	public void setGenericBuilding(GenericBuilding building_)
	{
		_building = building_;
		setTexture(_building.getTexture(ClayConstants.T_STATE_DEFAULT, ClayConstants.DEFAULT_BUILDING_POSITION));
	}

	@Override
	public void clicked()
	{
		
	}

	private GenericBuilding _building;
}
