package city.ui.menus.areas;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import main.ClayConstants;
import models.CityModel;

import org.newdawn.slick.opengl.Texture;

import screens.AbstractScreen;
import screens.CityScreen;
import city.generics.GenericBuilding;
import city.ui.menus.components.AbstractButton;
import city.ui.menus.components.ImageComponent;
import city.ui.menus.components.TextComponent;

public class BuildingSliderArea extends AbstractArea
{
	public BuildingSliderArea(AbstractScreen homeScreen_, int yPos_)
	{
		super(homeScreen_);
		_yPos = yPos_;

		_buildings = new ArrayList<GenericBuilding>();
		_listPointer = 0;

		loadKnownBuildings();

		_firstBuilding = new AbstractButton(40, _yPos, 35, 35)
		{

			@Override
			public void clicked()
			{
				moveLeft();
				moveLeft();
			}
		};
		_secondBuilding = new AbstractButton(40, _yPos - 40, 35, 35)
		{

			@Override
			public void clicked()
			{
				moveLeft();
			}
		};
		_thirdBuilding = new ImageComponent(40, _yPos - 80, 35, 35);
		_fourthBuilding = new AbstractButton(40, _yPos - 120, 35, 35)
		{

			@Override
			public void clicked()
			{
				moveRight();
			}
		};
		_fifthBuilding = new AbstractButton(40, _yPos - 160, 35, 35)
		{

			@Override
			public void clicked()
			{
				moveRight();
				moveRight();
			}
		};

		_firstBuilding.setTexture(getTextureFromPointer(-2));
		_secondBuilding.setTexture(getTextureFromPointer(-1));
		_thirdBuilding.setTexture(getTextureFromPointer(0));
		_fourthBuilding.setTexture(getTextureFromPointer(1));
		_fifthBuilding.setTexture(getTextureFromPointer(2));

		_firstBuilding.setDrawRatio(.75f);
		_secondBuilding.setDrawRatio(.75f);
		_thirdBuilding.setDrawRatio(.75f);
		_fourthBuilding.setDrawRatio(.75f);
		_fifthBuilding.setDrawRatio(.75f);

		_firstBuildingLabel = new TextComponent(80, _yPos,
				getBuildingNameFromPointer(-2));
		_secondBuildingLabel = new TextComponent(80, _yPos - 35,
				getBuildingNameFromPointer(-1));
		_thirdBuildingLabel = new TextComponent(80, _yPos - 70,
				getBuildingNameFromPointer(0));
		_fourthBuildingLabel = new TextComponent(80, _yPos - 105,
				getBuildingNameFromPointer(1));
		_fifthBuildingLabel = new TextComponent(80, _yPos - 140,
				getBuildingNameFromPointer(2));

		_thirdBuildingLabel.setTextColor(new Color(1.0f, 0f, 0f));

		_components.add(_firstBuilding);
		_components.add(_secondBuilding);
		_components.add(_thirdBuilding);
		_components.add(_fourthBuilding);
		_components.add(_fifthBuilding);

		_components.add(_firstBuildingLabel);
		_components.add(_secondBuildingLabel);
		_components.add(_thirdBuildingLabel);
		_components.add(_fourthBuildingLabel);
		_components.add(_fifthBuildingLabel);
	}

	private void loadKnownBuildings()
	{
		_buildings = ((CityScreen) _homeScreen).getPlayerModel()
				.getKnownBuildings();
	}

	private Texture getTextureFromPointer(int pointerOffset_)
	{
		if (pointerOffset_ == 0)
			return _buildings.get(_listPointer).getTexture(
					ClayConstants.T_STATE_DEFAULT,
					ClayConstants.DEFAULT_BUILDING_POSITION);
		int index = _listPointer + pointerOffset_;
		if (index < 0)
		{
			index = _buildings.size() + index;
		}
		if (index > _buildings.size() - 1)
		{
			index = index - _buildings.size();
		}
		return _buildings.get(index).getTexture(
				ClayConstants.T_STATE_DEFAULT,
				ClayConstants.DEFAULT_BUILDING_POSITION);
	}

	private String getBuildingNameFromPointer(int pointerOffset_)
	{
		if (pointerOffset_ == 0)
			return _buildings.get(_listPointer).getBuildingName();
		int index = _listPointer + pointerOffset_;
		if (index < 0)
		{
			index = _buildings.size() + index;
		}
		if (index > _buildings.size() - 1)
		{
			index = index - _buildings.size();
		}
		return _buildings.get(index).getBuildingName();
	}

	public void moveLeft()
	{
		_listPointer--;
		if (_listPointer < 0)
			_listPointer = _buildings.size() - 1;
		updateSelectedBuilding();
	}

	public void moveRight()
	{
		_listPointer++;
		if (_listPointer > _buildings.size() - 1)
			_listPointer = 0;
		updateSelectedBuilding();
	}

	public void updateSelectedBuilding()
	{
		CityModel model = ((CityScreen) _homeScreen).getModel();
		model.setSelectedBuilding(_buildings.get(_listPointer)
				.getBuildingIdentifier());
		_firstBuilding.setTexture(getTextureFromPointer(-2));
		_secondBuilding.setTexture(getTextureFromPointer(-1));
		_thirdBuilding.setTexture(getTextureFromPointer(0));
		_fourthBuilding.setTexture(getTextureFromPointer(1));
		_fifthBuilding.setTexture(getTextureFromPointer(2));

		_firstBuildingLabel.setText(getBuildingNameFromPointer(-2));
		_secondBuildingLabel.setText(getBuildingNameFromPointer(-1));
		_thirdBuildingLabel.setText(getBuildingNameFromPointer(0));
		_fourthBuildingLabel.setText(getBuildingNameFromPointer(1));
		_fifthBuildingLabel.setText(getBuildingNameFromPointer(2));

	}

	@Override
	public void update()
	{
	}

	private List<GenericBuilding> _buildings;

	private int _yPos;

	private int _listPointer;

	private AbstractButton _firstBuilding;
	private AbstractButton _secondBuilding;
	private ImageComponent _thirdBuilding;
	private AbstractButton _fourthBuilding;
	private AbstractButton _fifthBuilding;

	private TextComponent _firstBuildingLabel;
	private TextComponent _secondBuildingLabel;
	private TextComponent _thirdBuildingLabel;
	private TextComponent _fourthBuildingLabel;
	private TextComponent _fifthBuildingLabel;
}
