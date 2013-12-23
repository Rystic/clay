package city.effects;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import models.CityModel;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import screens.AbstractScreen;
import city.entities.GolemEntity;

public class GolemEffect extends AbstractEffect
{
	public GolemEffect(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_golemList = ((CityModel) homeScreen_.getModel()).getGolems();
	}

	@Override
	public void init()
	{
		try
		{
			_golemTexture = TextureLoader.getTexture(
					"PNG",
					new FileInputStream(new File("art/golem.png")));
			_lowManaTexture = TextureLoader.getTexture(
					"PNG",
					new FileInputStream(new File("art/lowManaGolem.png")));
			_lowClayTexture = TextureLoader.getTexture(
					"PNG",
					new FileInputStream(new File("art/lowClayGolem.png")));
			_lowManaLowClayTexture = TextureLoader
					.getTexture("PNG", new FileInputStream(new File(
							"art/lowManaLowClayGolem.png")));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
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
			GL11.glColor3f(manaVal, manaVal, clayVal);
			if (golem.isLowClay())
			{
				if (golem.isLowMana())
				{
					GL11.glBindTexture(
							GL11.GL_TEXTURE_2D,
							_lowManaLowClayTexture.getTextureID());
				}
				else
				{
					GL11.glBindTexture(
							GL11.GL_TEXTURE_2D,
							_lowClayTexture.getTextureID());
				}
			}
			else if (golem.isLowMana())
			{
				GL11.glBindTexture(
						GL11.GL_TEXTURE_2D,
						_lowManaTexture.getTextureID());
			}
			else
			{
				GL11.glBindTexture(
						GL11.GL_TEXTURE_2D,
						_golemTexture.getTextureID());
			}
			double x = golem.getX();
			double y = golem.getY();
			// if (!golem.getVisible())
			// continue;

			GL11.glBegin(GL11.GL_POLYGON);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2d(x, y + GOLEM_DEFAULT_HEIGHT);
			GL11.glTexCoord2f(.60f, 0);
			GL11.glVertex2d(x + GOLEM_DEFAULT_WIDTH, y + GOLEM_DEFAULT_HEIGHT);
			GL11.glTexCoord2f(.60f, .60f);
			GL11.glVertex2d(x + GOLEM_DEFAULT_WIDTH, y);
			GL11.glTexCoord2f(0, .60f);
			GL11.glVertex2d(x, y);
			GL11.glEnd();
			if (!golem.getCopyOfHeldItems().isEmpty())
			{
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, golem
						.getCopyOfHeldItems().get(0).getTexture()
						.getTextureID());
				GL11.glBegin(GL11.GL_POLYGON);
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2d(x, y + GOLEM_DEFAULT_HEIGHT);
				GL11.glTexCoord2f(.60f, 0);
				GL11.glVertex2d(x + GOLEM_DEFAULT_WIDTH, y
						+ GOLEM_DEFAULT_HEIGHT);
				GL11.glTexCoord2f(.60f, .60f);
				GL11.glVertex2d(x + GOLEM_DEFAULT_WIDTH, y);
				GL11.glTexCoord2f(0, .60f);
				GL11.glVertex2d(x, y);
				GL11.glEnd();
			}
		}
	}

	private final static int GOLEM_DEFAULT_WIDTH = 25;
	private final static int GOLEM_DEFAULT_HEIGHT = 25;

	private Texture _golemTexture;
	private Texture _lowManaTexture;
	private Texture _lowClayTexture;
	private Texture _lowManaLowClayTexture;

	private List<GolemEntity> _golemList;
}
