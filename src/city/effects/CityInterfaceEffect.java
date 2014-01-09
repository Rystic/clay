package city.effects;

import main.ClayConstants;
import models.CityModel;
import screens.AbstractScreen;

public class CityInterfaceEffect extends AbstractEffect
{
	public CityInterfaceEffect(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
	}

	@Override
	public void executeEffect()
	{
//		AbstractMenu menu = _model.getSelectedMenu();
//		for (AbstractComponent component : menu.getCopyOfComponents())
//		{
//			// TODO draw it!
//		}
	}

	private CityModel _model;

	private int _interfaceHeight = ClayConstants.DEFAULT_MAP_HEIGHT;
	private int _interfaceBorderThickness = 1;
}
