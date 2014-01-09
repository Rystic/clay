package city.effects;

import main.ClayConstants;
import models.CityModel;

import org.lwjgl.opengl.GL11;

import screens.AbstractScreen;
import city.ui.components.AbstractComponent;
import city.ui.menus.AbstractMenu;

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
		AbstractMenu menu = _model.getSelectedMenu();
		for (AbstractComponent component : menu.getCopyOfComponents())
		{
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, component.getTexture()
					.getTextureID());
			GL11.glBegin(GL11.GL_POLYGON);
			GL11.glTexCoord2f(0, 0);

			GL11.glVertex2d(
					component.getX() + ClayConstants.DEFAULT_MAP_WIDTH,
					component.getY() + component.getHeight());

			GL11.glTexCoord2f(component.getDrawRatio(), 0);

			GL11.glVertex2d(component.getX() + component.getWidth()
					+ ClayConstants.DEFAULT_MAP_WIDTH, component.getY()
					+ component.getHeight());

			GL11.glTexCoord2f(
					component.getDrawRatio(),
					component.getDrawRatio());

			GL11.glVertex2d(component.getX() + component.getWidth()
					+ ClayConstants.DEFAULT_MAP_WIDTH, component.getY());

			GL11.glTexCoord2f(0, component.getDrawRatio());

			GL11.glVertex2d(
					component.getX() + ClayConstants.DEFAULT_MAP_WIDTH,
					component.getY());

			GL11.glEnd();
		}
	}

	private CityModel _model;
}
