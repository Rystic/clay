package city.effects;

import java.util.List;

import main.ClayConstants;
import models.CityModel;

import org.lwjgl.opengl.GL11;

import screens.AbstractScreen;
import city.generics.entities.GolemEntity;

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
				GL11.glColor3f(1f,1f,1f);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, golem.getCurrentTexture()
					.getTextureID());
			double x = golem.getX();
			double y = golem.getY();

			GL11.glBegin(GL11.GL_POLYGON);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2d(x, ClayConstants.DEFAULT_MAP_HEIGHT - y
					- GOLEM_DEFAULT_HEIGHT);
			GL11.glTexCoord2f(_drawRatioGolems, 0);
			GL11.glVertex2d(
					x + GOLEM_DEFAULT_WIDTH,
					ClayConstants.DEFAULT_MAP_HEIGHT - y - GOLEM_DEFAULT_HEIGHT);
			GL11.glTexCoord2f(_drawRatioGolems, _drawRatioGolems);
			GL11.glVertex2d(
					x + GOLEM_DEFAULT_WIDTH,
					ClayConstants.DEFAULT_MAP_HEIGHT - y);
			GL11.glTexCoord2f(0, _drawRatioGolems);
			GL11.glVertex2d(x, ClayConstants.DEFAULT_MAP_HEIGHT - y);
			GL11.glEnd();
			if (!golem.getCopyOfHeldItems().isEmpty())
			{
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, golem
						.getCopyOfHeldItems().get(0).getTexture()
						.getTextureID());
				GL11.glBegin(GL11.GL_POLYGON);
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2d(x, ClayConstants.DEFAULT_MAP_HEIGHT - y
						- GOLEM_DEFAULT_HEIGHT);
				GL11.glTexCoord2f(_drawRatioItems, 0);
				GL11.glVertex2d(
						x + GOLEM_DEFAULT_WIDTH,
						ClayConstants.DEFAULT_MAP_HEIGHT - y
								- GOLEM_DEFAULT_HEIGHT);
				GL11.glTexCoord2f(_drawRatioItems, _drawRatioItems);
				GL11.glVertex2d(
						x + GOLEM_DEFAULT_WIDTH,
						ClayConstants.DEFAULT_MAP_HEIGHT - y);
				GL11.glTexCoord2f(0, _drawRatioItems);
				GL11.glVertex2d(x, ClayConstants.DEFAULT_MAP_HEIGHT - y);
				GL11.glEnd();
			}
		}
	}

	private final static int GOLEM_DEFAULT_WIDTH = 30;
	private final static int GOLEM_DEFAULT_HEIGHT = 30;

	private final static float _drawRatioGolems = .6f;
	private final static float _drawRatioItems = .6f;

	private List<GolemEntity> _golemList;
}
