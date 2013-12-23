package city.effects;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import main.ClayConstants;
import models.CityModel;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import screens.AbstractScreen;
import city.components.AbstractButton;
import city.components.SelectBuildingButton;
import city.generics.GenericBuilding;
import city.util.TextUtil;
import data.BuildingData;

public class CityInterfaceEffect extends AbstractEffect
{
	public CityInterfaceEffect(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
	}

	@Override
	public void init()
	{
	}

	@Override
	public void executeEffect()
	{
		doBuildingMenu();
	}

	private void doBuildingMenu()
	{

		GL11.glColor3f(.75f, .75f, .75f);
		GenericBuilding selectedBuilding = BuildingData.getBuildingById(_model
				.getSelectedBuilding());
		Set<AbstractButton> options = _model.getInterfaceOptions();

		// GL11.glColor3f(1.0f, 0.6f, 0.0f);

		GL11.glColor3f(.1f, .6f, .6f);
		GL11.glBegin(GL11.GL_POLYGON);
		GL11.glVertex2d(ClayConstants.DEFAULT_MAP_WIDTH
				- _interfaceBorderThickness, ClayConstants.DEFAULT_MAP_HEIGHT);
		GL11.glVertex2d(
				ClayConstants.DEFAULT_INTERFACE_WIDTH
						+ ClayConstants.DEFAULT_MAP_WIDTH,
				ClayConstants.DEFAULT_MAP_HEIGHT);
		GL11.glVertex2d(
				ClayConstants.DEFAULT_INTERFACE_WIDTH
						+ ClayConstants.DEFAULT_MAP_WIDTH,
				ClayConstants.DEFAULT_MAP_HEIGHT
						- (_interfaceHeight + _interfaceBorderThickness));
		GL11.glVertex2d(ClayConstants.DEFAULT_MAP_WIDTH
				- _interfaceBorderThickness, ClayConstants.DEFAULT_MAP_HEIGHT
				- (_interfaceHeight + _interfaceBorderThickness));
		GL11.glEnd();

		GL11.glColor3f(.8f, .6f, .0f);
		GL11.glBegin(GL11.GL_POLYGON);
		GL11.glVertex2d(
				ClayConstants.DEFAULT_MAP_WIDTH,
				ClayConstants.DEFAULT_MAP_HEIGHT);
		GL11.glVertex2d(
				ClayConstants.DEFAULT_INTERFACE_WIDTH
						+ ClayConstants.DEFAULT_MAP_WIDTH,
				ClayConstants.DEFAULT_MAP_HEIGHT);
		GL11.glVertex2d(
				ClayConstants.DEFAULT_INTERFACE_WIDTH
						+ ClayConstants.DEFAULT_MAP_WIDTH,
				ClayConstants.DEFAULT_MAP_HEIGHT - _interfaceHeight);
		GL11.glVertex2d(
				ClayConstants.DEFAULT_MAP_WIDTH,
				ClayConstants.DEFAULT_MAP_HEIGHT - _interfaceHeight);
		GL11.glEnd();

		for (AbstractButton button : options)
		{
			GL11.glColor3f(.75f, .75f, .75f);
			if (button instanceof SelectBuildingButton)
			{
				if (((SelectBuildingButton) button).getIdentifier() == selectedBuilding
						.getBuildingIdentifier())
				{
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
					GL11.glColor3f(.0f, .0f, 0.0f);
					TextUtil.drawString(
							selectedBuilding.getBuildingName(),
							ClayConstants.DEFAULT_MAP_WIDTH + 50,
							ClayConstants.DEFAULT_MAP_HEIGHT - 95);
					GL11.glColor3f(.0f, 1.0f, 1.0f);
					TextUtil.drawString(
							selectedBuilding.getBuildingDescription(),
							ClayConstants.DEFAULT_MAP_WIDTH + 50,
							ClayConstants.DEFAULT_MAP_HEIGHT - 108);
					GL11.glColor3f(1.0f, .5f, 0.0f);
				}
			}
			placeButton(button, button.getTexture());
		}

		int manaIconDist = 100;

		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, _manaTexture.getTextureID());
		// GL11.glBegin(GL11.GL_POLYGON);
		// GL11.glTexCoord2f(0, 0);
		// GL11.glVertex2d(
		// ClayConstants.DEFAULT_MAP_WIDTH + manaIconDist,
		// ClayConstants.DEFAULT_MAP_HEIGHT - 155);
		// GL11.glTexCoord2f(.80f, 0);
		// GL11.glVertex2d(
		// ClayConstants.DEFAULT_MAP_WIDTH + manaIconDist + 25,
		// ClayConstants.DEFAULT_MAP_HEIGHT - 155);
		// GL11.glTexCoord2f(.80f, .60f);
		// GL11.glVertex2d(
		// ClayConstants.DEFAULT_MAP_WIDTH + manaIconDist + 25,
		// ClayConstants.DEFAULT_MAP_HEIGHT - 170);
		// GL11.glTexCoord2f(0, .60f);
		// GL11.glVertex2d(
		// ClayConstants.DEFAULT_MAP_WIDTH + manaIconDist,
		// ClayConstants.DEFAULT_MAP_HEIGHT - 170);
		// GL11.glEnd();
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		// GL11.glColor3f(.0f, 1.0f, 1.0f);
		// TextUtil.drawString(
		// ClayMain._manaProcess.getLesserMana() + " "
		// + ClayMain._manaProcess.getGreaterMana(),
		// adjustedX + 50,
		// ClayConstants.DEFAULT_MAP_HEIGHT - 150);
	}

	private void placeButton(AbstractButton button_, Texture buttonTexture_)
	{
		int x = (int) button_.getX();
		int y = (int) button_.getY();
		int width = (int) button_.getWidth();
		int height = (int) button_.getHeight();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, buttonTexture_.getTextureID());
		GL11.glBegin(GL11.GL_POLYGON);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2d(x, y + height);
		GL11.glTexCoord2f(.75f, 0);
		GL11.glVertex2d(x + width, y + height);
		GL11.glTexCoord2f(.75f, .75f);
		GL11.glVertex2d(x + width, y);
		GL11.glTexCoord2f(0, .75f);
		GL11.glVertex2d(x, y);
		GL11.glEnd();
	}

	private CityModel _model;

	private int _interfaceHeight = ClayConstants.DEFAULT_MAP_HEIGHT;
	private int _interfaceBorderThickness = 1;
}
