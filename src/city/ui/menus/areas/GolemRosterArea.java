package city.ui.menus.areas;

import java.util.List;

import org.newdawn.slick.Color;

import screens.AbstractScreen;
import screens.CityScreen;
import city.generics.entities.GolemEntity;
import city.generics.objects.Behavior;
import city.ui.menus.components.ImageComponent;
import city.ui.menus.components.TextComponent;

public class GolemRosterArea extends AbstractArea
{
	public GolemRosterArea(AbstractScreen homeScreen_, int yPos_)
	{
		super(homeScreen_);
		_golemRosterLabel = new TextComponent(95, yPos_, "Golem Roster");
		_golemRosterLabel.setTextColor(Color.yellow);
		_golemCountLabel = new TextComponent(25, yPos_ - 35);
		_golemCountLabel.setTextColor(Color.yellow);
		_golemList = ((CityScreen) homeScreen_).getModel().getGolems();
		_yPos = yPos_;
	}

	@Override
	public void update()
	{
		_components.clear();
		_golemCountLabel.setText("Golem Count: " + _golemList.size());
		_components.add(_golemRosterLabel);
		_components.add(_golemCountLabel);
		
		int yPos = _yPos - 75;
		for (GolemEntity golem : _golemList)
		{
			Behavior currBehavior = golem.getCurrentBehavior();
			String task = currBehavior == null ? "No task." : currBehavior
					.getBehaviorDescription();
			ImageComponent golemStatus = new ImageComponent(20, yPos - 30, 30, 30, golem.getCurrentTexture());
			TextComponent text = new TextComponent(50, yPos, task);
			_components.add(text);
			_components.add(golemStatus);
			yPos -= 25;
		}

	}

	private TextComponent _golemRosterLabel;
	private TextComponent _golemCountLabel;
	private List<GolemEntity> _golemList;
	
	private int _yPos;
}