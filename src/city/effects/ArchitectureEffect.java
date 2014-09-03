package city.effects;

import main.ClayConstants;
import models.CityModel;

import org.lwjgl.opengl.GL11;

import screens.AbstractScreen;
import city.generics.entities.BuildingEntity;

public class ArchitectureEffect extends AbstractEffect
{
	public ArchitectureEffect(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
		_tileValues = _model.getTileValues();
		_coordSize = .777f;
	}

	@Override
	public void executeEffect()
	{
		_coordSize = .75f;
		GL11.glColor3f(0.75f, 0.75f, .75f);
		for (int i = 0; i < ClayConstants.DEFAULT_MAP_WIDTH; i += TILE_X)
		{
			for (int j = 0; j < ClayConstants.DEFAULT_MAP_HEIGHT; j += TILE_Y)
			{
				BuildingEntity tile = _tileValues[i / TILE_X][j / TILE_Y];
				if (tile != null)
				{
					if (tile.isHighlighted())
					{
						GL11.glColor3f(0.75f, 0.0f, .0f);
						tile.setHighlightedForDeletion(false);
					}
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, tile.getTexture()
							.getTextureID());
					GL11.glBegin(GL11.GL_POLYGON);
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex2d(i - 1, ClayConstants.DEFAULT_MAP_HEIGHT - j
							- TILE_Y - 1);
					GL11.glTexCoord2f(_coordSize + .05f, 0);
					GL11.glVertex2d(
							i + TILE_X,
							ClayConstants.DEFAULT_MAP_HEIGHT - j - TILE_Y - 1);
					GL11.glTexCoord2f(_coordSize + .05f, _coordSize);
					GL11.glVertex2d(
							i + TILE_X,
							ClayConstants.DEFAULT_MAP_HEIGHT - j);
					GL11.glTexCoord2f(0, _coordSize);
					GL11.glVertex2d(i - 1, ClayConstants.DEFAULT_MAP_HEIGHT - j);
					GL11.glEnd();
					GL11.glColor3f(0.75f, 0.75f, .75f);
				}
			}
		}

	}

	private float _coordSize;

	private static final int TILE_X = ClayConstants.TILE_X;
	private static final int TILE_Y = ClayConstants.TILE_Y;

	private CityModel _model;
	private BuildingEntity[][] _tileValues;

}
