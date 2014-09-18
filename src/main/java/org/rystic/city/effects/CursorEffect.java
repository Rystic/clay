package org.rystic.city.effects;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.rystic.city.generics.GenericBuilding;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;
import org.rystic.screens.CityScreen;

public class CursorEffect extends AbstractEffect
{
	public CursorEffect(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (((CityScreen) _homeScreen).getModel());
		_selectedBuilding = -1;
	}

	@Override
	public void executeEffect()
	{
		if (Mouse.getX() < Display.getWidth()
				- ClayConstants.DEFAULT_INTERFACE_WIDTH)
		{
			int selectedBuilding = _model.getSelectedBuilding();
			if (selectedBuilding != _selectedBuilding)
			{
				_selectedBuilding = selectedBuilding;
				GenericBuilding building = BuildingData
						.getBuildingById(_selectedBuilding);
				_cursorTexture = building.getTexture(
						ClayConstants.T_STATE_DEFAULT,
						ClayConstants.DEFAULT_BUILDING_POSITION);
			}

			int x = Mouse.getX() - 10;
			int y = Mouse.getY() - 10;
			GL11.glBindTexture(
					GL11.GL_TEXTURE_2D,
					_cursorTexture.getTextureID());
			GL11.glBegin(GL11.GL_POLYGON);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2d(x, y + _cursorSize);
			GL11.glTexCoord2f(.77f, 0);
			GL11.glVertex2d(x + _cursorSize, y + _cursorSize);
			GL11.glTexCoord2f(.77f, .77f);
			GL11.glVertex2d(x + _cursorSize, y);
			GL11.glTexCoord2f(0, .77f);
			GL11.glVertex2d(x, y);
			GL11.glEnd();
		}
	}

	private CityModel _model;

	private Texture _cursorTexture;

	private int _selectedBuilding;

	private int _cursorSize = (int) (ClayConstants.TILE_X / 1.5);
}
