package city.ui.menus.areas;

import java.util.ArrayList;
import java.util.List;

import main.ClayConstants;
import models.CityModel;
import screens.AbstractScreen;
import screens.CityScreen;
import city.generics.GenericBuilding;
import city.ui.components.ImageComponent;
import city.ui.components.TextComponent;

public class BuildingSliderArea extends AbstractArea
{
	public BuildingSliderArea(AbstractScreen homeScreen_, int yPos_)
	{
		super(homeScreen_);
		_yPos = yPos_;

		_buildings = new ArrayList<GenericBuilding>();
		_listPointer = 0;

		loadKnownBuildings();

		_buildingNameLabel = new TextComponent(115, _yPos - 40);
		_nextLabel = new TextComponent(200, _yPos - 80, "Next - E");
		_prevLabel = new TextComponent(50, _yPos - 80, "Q - Prev");

		_leftBuilding = new ImageComponent(115, _yPos - 90, 35, 35);
		_middleBuilding = new ImageComponent(115, _yPos - 25, 75, 75);
		_rightBuilding = new ImageComponent(155, _yPos - 90, 35, 35);

		_leftBuilding.setTexture(_buildings.get(_buildings.size() - 1)
				.getTexture(
						ClayConstants.T_STATE_DEFAULT,
						ClayConstants.DEFAULT_BUILDING_POSITION));
		_middleBuilding.setTexture(_buildings.get(0).getTexture(
				ClayConstants.T_STATE_DEFAULT,
				ClayConstants.DEFAULT_BUILDING_POSITION));
		_rightBuilding.setTexture(_buildings.get(1).getTexture(
				ClayConstants.T_STATE_DEFAULT,
				ClayConstants.DEFAULT_BUILDING_POSITION));

		_leftBuilding.setDrawRatio(.75f);
		_middleBuilding.setDrawRatio(.75f);
		_rightBuilding.setDrawRatio(.75f);

		_components.add(_buildingNameLabel);
		_components.add(_prevLabel);
		_components.add(_nextLabel);
		_components.add(_leftBuilding);
		_components.add(_middleBuilding);
		_components.add(_rightBuilding);
	}

	private void loadKnownBuildings()
	{
		_buildings = ((CityScreen) _homeScreen).getPlayerModel()
				.getKnownBuildings();
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
		_leftBuilding.setTexture(_buildings.get(
				_listPointer == 0 ? _buildings.size() - 1 : _listPointer - 1)
				.getTexture(
						ClayConstants.T_STATE_DEFAULT,
						ClayConstants.DEFAULT_BUILDING_POSITION));
		_middleBuilding.setTexture(_buildings.get(_listPointer).getTexture(
				ClayConstants.T_STATE_DEFAULT,
				ClayConstants.DEFAULT_BUILDING_POSITION));
		_rightBuilding.setTexture(_buildings.get(
				_listPointer == _buildings.size() - 1 ? 0 : _listPointer + 1)
				.getTexture(
						ClayConstants.T_STATE_DEFAULT,
						ClayConstants.DEFAULT_BUILDING_POSITION));
	}

	@Override
	public void update()
	{
		_buildingNameLabel.setText(_buildings.get(_listPointer)
				.getBuildingName());
	}

	private List<GenericBuilding> _buildings;

	private int _yPos;

	private int _listPointer;

	private ImageComponent _leftBuilding;
	private ImageComponent _middleBuilding;
	private ImageComponent _rightBuilding;

	private TextComponent _buildingNameLabel;
	private TextComponent _nextLabel;
	private TextComponent _prevLabel;
}
