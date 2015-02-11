package org.rystic.city.ui.menus.areas;

import java.util.List;

import org.rystic.city.entities.golem.GolemEntity;
import org.rystic.city.generics.objects.Behavior;
import org.rystic.city.ui.menus.components.ImageComponent;
import org.rystic.city.ui.menus.components.TextComponent;
import org.rystic.main.ClayConstants;
import org.rystic.screens.AbstractScreen;
import org.rystic.screens.CityScreen;

public class GolemRosterArea extends AbstractArea
{
	public GolemRosterArea(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_golemCountLabel = new TextComponent(5, 5);
		_golemCountLabel.setColor(ClayConstants.M_MENU_HEADER_COLOR);
		_golemList = ((CityScreen) homeScreen_).getModel().getGolems();
	}

	@Override
	public void update()
	{
		_components.clear();
		_golemCountLabel.setText("Golem Count: " + _golemList.size());
		_components.add(_golemCountLabel);

		int increment = 9;
		for (GolemEntity golem : _golemList)
		{
			Behavior currBehavior = golem.getCurrentBehavior();
			String task = currBehavior == null ? "No task." : currBehavior
					.getBehaviorDescription();
			ImageComponent golemStatus = new ImageComponent(3, increment + 3, 25,
					25, golem.getCurrentTexture());
			TextComponent text = new TextComponent(12, increment, task);
			_components.add(text);
			_components.add(golemStatus);
			increment += 3;
		}

	}

	private TextComponent _golemCountLabel;
	private List<GolemEntity> _golemList;
}
