package city.ui.components;

import main.ClayConstants;

import org.newdawn.slick.opengl.Texture;

import city.generics.GenericBuilding;


public class SelectBuildingButton extends AbstractComponent
{
	public SelectBuildingButton(int x_, int y_, int width_, int height_,
			GenericBuilding building_)
	{
		super(x_, y_, width_, height_);
		_building = building_;
	}
	
	@Override
	public Texture getTexture()
	{
		return _building.getTexture(ClayConstants.T_STATE_DEFAULT, ClayConstants.DEFAULT_BUILDING_POSITION);
	}
	
	public int getIdentifier()
	{
		return _building.getBuildingIdentifier();
	}

	@Override
	public void clicked()
	{
		
	}

	private GenericBuilding _building;
}
