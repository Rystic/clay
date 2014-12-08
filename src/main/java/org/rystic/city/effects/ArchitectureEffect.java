package org.rystic.city.effects;

import org.lwjgl.opengl.GL11;
import org.rystic.city.generics.entities.BuildingEntity;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;

public class ArchitectureEffect extends AbstractEffect
{
	public ArchitectureEffect(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
		_tileValues = _model.getTileValues();
		_coordSize = 1.0f;
	}

	@Override
	public void executeEffect()
	{
		GL11.glColor3f(0.75f, 0.75f, .75f);
		int iIndex = 0;
		int jIndex = 0;
		int prevTexture = -1;
		for (int i = 0; i < ClayConstants.DEFAULT_MAP_WIDTH; i += TILE_X)
		{
			for (int j = 0; j < ClayConstants.DEFAULT_MAP_HEIGHT; j += TILE_Y)
			{
				BuildingEntity tile = _tileValues[iIndex][jIndex];
				if (tile != null)
				{
					if (tile.isHighlightedForDeletion())
					{
						GL11.glColor3f(0.75f, .0f, .0f);
						tile.setHighlightedForDeletion(false);
					}
					else if (tile.isHeatedIncludeHeatDamage())
					{
						if (tile.isOverheated())
							GL11.glColor3f(0.75f, 0.25f, .25f);
						else
							GL11.glColor3f(0.75f, 0.50f, .50f);
					}
					int textureId = tile.getTexture().getTextureID();
					if (textureId != prevTexture)
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
					prevTexture = textureId;
					GL11.glBegin(GL11.GL_POLYGON);
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex2d(i, ClayConstants.DEFAULT_MAP_HEIGHT - j
							- TILE_Y);
					GL11.glTexCoord2f(_coordSize, 0);
					GL11.glVertex2d(
							i + TILE_X,
							ClayConstants.DEFAULT_MAP_HEIGHT - j - TILE_Y);
					GL11.glTexCoord2f(_coordSize, _coordSize);
					GL11.glVertex2d(
							i + TILE_X,
							ClayConstants.DEFAULT_MAP_HEIGHT - j);
					GL11.glTexCoord2f(0, _coordSize);
					GL11.glVertex2d(i, ClayConstants.DEFAULT_MAP_HEIGHT - j);
					GL11.glEnd();
					GL11.glColor3f(0.75f, 0.75f, .75f);
				}
				jIndex++;
			}
			jIndex = 0;
			iIndex++;
		}

	}

	private float _coordSize;

	private static final int TILE_X = ClayConstants.TILE_X;
	private static final int TILE_Y = ClayConstants.TILE_Y;

	private CityModel _model;
	private BuildingEntity[][] _tileValues;

}
