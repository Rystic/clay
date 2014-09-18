package org.rystic.city.effects;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.rystic.city.generics.entities.GolemEntity;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;

public class GolemEffect extends AbstractEffect
{
	public GolemEffect(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_golemList = ((CityModel) homeScreen_.getModel()).getGolems();
	}

	@Override
	public void executeEffect()
	{
		for (GolemEntity golem : _golemList)
		{
			if (!golem.isVisible())
				continue;
			float manaVal = (float) (golem.getMana() / 100);
			float clayVal = (float) (golem.getClay() / 100);
			if (golem.getGolemTag().equals("clay-golem"))
				GL11.glColor3f(manaVal, manaVal, clayVal);
			else
				GL11.glColor3f(1f, 1f, 1f);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, golem.getCurrentTexture()
					.getTextureID());
			double x = golem.getX();
			double y = golem.getY();
			float drawRatio = golem.getTextureScaling();
			int size = golem.getSpriteSize();

			if (golem.isFacingRight())
			{
				GL11.glBegin(GL11.GL_POLYGON);
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2d(x, ClayConstants.DEFAULT_MAP_HEIGHT - y - size);
				GL11.glTexCoord2f(drawRatio, 0);
				GL11.glVertex2d(x + size, ClayConstants.DEFAULT_MAP_HEIGHT - y
						- size);
				GL11.glTexCoord2f(drawRatio, drawRatio);
				GL11.glVertex2d(x + size, ClayConstants.DEFAULT_MAP_HEIGHT - y);
				GL11.glTexCoord2f(0, drawRatio);
				GL11.glVertex2d(x, ClayConstants.DEFAULT_MAP_HEIGHT - y);
				GL11.glEnd();
			}
			else
			{
				GL11.glBegin(GL11.GL_POLYGON);
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2d(x + size, ClayConstants.DEFAULT_MAP_HEIGHT - y
						- size);
				GL11.glTexCoord2f(drawRatio, 0);
				GL11.glVertex2d(x, ClayConstants.DEFAULT_MAP_HEIGHT - y - size);
				GL11.glTexCoord2f(drawRatio, drawRatio);
				GL11.glVertex2d(x, ClayConstants.DEFAULT_MAP_HEIGHT - y);
				GL11.glTexCoord2f(0, drawRatio);
				GL11.glVertex2d(x + size, ClayConstants.DEFAULT_MAP_HEIGHT - y);
				GL11.glEnd();
			}

			if (!golem.getCopyOfHeldItems().isEmpty())
			{
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, golem
						.getCopyOfHeldItems().get(0).getTexture()
						.getTextureID());
				GL11.glBegin(GL11.GL_POLYGON);
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2d(x, ClayConstants.DEFAULT_MAP_HEIGHT - y
						- _itemDefaultSize);
				GL11.glTexCoord2f(_drawRatioItems, 0);
				GL11.glVertex2d(
						x + _itemDefaultSize,
						ClayConstants.DEFAULT_MAP_HEIGHT - y - _itemDefaultSize);
				GL11.glTexCoord2f(_drawRatioItems, _drawRatioItems);
				GL11.glVertex2d(
						x + _itemDefaultSize,
						ClayConstants.DEFAULT_MAP_HEIGHT - y);
				GL11.glTexCoord2f(0, _drawRatioItems);
				GL11.glVertex2d(x, ClayConstants.DEFAULT_MAP_HEIGHT - y);
				GL11.glEnd();
			}
		}
	}

	private final static float _drawRatioItems = .6f;

	private List<GolemEntity> _golemList;

	private final int _itemDefaultSize = 30;
}
