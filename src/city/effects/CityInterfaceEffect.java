package city.effects;

import main.ClayConstants;
import models.CityModel;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import screens.AbstractScreen;
import city.ui.menus.AbstractMenu;
import city.ui.menus.components.AbstractComponent;
import city.ui.menus.components.TextComponent;
import city.util.TextUtil;

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
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			if (component instanceof TextComponent)
			{
				TextComponent textComponent = (TextComponent) component;
				GL11.glColor3f(textComponent.getColor().getRed(), textComponent
						.getColor().getGreen(), textComponent.getColor()
						.getBlue());
				TextUtil.drawString(
						textComponent,
						(int) textComponent.getX()
								+ ClayConstants.DEFAULT_MAP_WIDTH,
						ClayConstants.DEFAULT_MAP_HEIGHT
								- (int) textComponent.getY());
			}
			else if (component.getTexture() != null)
			{
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, component.getTexture()
						.getTextureID());
				GL11.glBegin(GL11.GL_POLYGON);
				GL11.glTexCoord2f(0, 0);

				GL11.glVertex2d(
						component.getX() + ClayConstants.DEFAULT_MAP_WIDTH,
						ClayConstants.DEFAULT_MAP_HEIGHT - component.getY()
								- component.getHeight());

				GL11.glTexCoord2f(component.getDrawRatio(), 0);

				GL11.glVertex2d(
						component.getX() + component.getWidth()
								+ ClayConstants.DEFAULT_MAP_WIDTH,
						ClayConstants.DEFAULT_MAP_HEIGHT - component.getY()
								- component.getHeight());

				GL11.glTexCoord2f(
						component.getDrawRatio(),
						component.getDrawRatio());

				GL11.glVertex2d(
						component.getX() + component.getWidth()
								+ ClayConstants.DEFAULT_MAP_WIDTH,
						ClayConstants.DEFAULT_MAP_HEIGHT - component.getY());

				GL11.glTexCoord2f(0, component.getDrawRatio());

				GL11.glVertex2d(
						component.getX() + ClayConstants.DEFAULT_MAP_WIDTH,
						ClayConstants.DEFAULT_MAP_HEIGHT - component.getY());
				GL11.glEnd();
			}
			else if (component.getColor() != null)
			{
				Color color = component.getColor();
				GL11.glColor3f(color.r, color.g, color.b);
				GL11.glBegin(GL11.GL_POLYGON);
				GL11.glVertex2d(component.getX()
						+ ClayConstants.DEFAULT_MAP_WIDTH, ClayConstants.DEFAULT_MAP_HEIGHT - component.getY());
				GL11.glVertex2d(component.getX()
						+ ClayConstants.DEFAULT_MAP_WIDTH, ClayConstants.DEFAULT_MAP_HEIGHT - component.getY()
						+ component.getHeight());
				GL11.glVertex2d(component.getX() + component.getWidth()
						+ ClayConstants.DEFAULT_MAP_WIDTH, ClayConstants.DEFAULT_MAP_HEIGHT - component.getY()
						+ component.getHeight());
				GL11.glVertex2d(component.getX() + component.getWidth()
						+ ClayConstants.DEFAULT_MAP_WIDTH, ClayConstants.DEFAULT_MAP_HEIGHT - component.getY());
				GL11.glEnd();
			}
			else
				System.out.println("Got an undrawable component.");
		}
	}

	private CityModel _model;
}
