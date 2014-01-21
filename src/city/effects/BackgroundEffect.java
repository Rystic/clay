package city.effects;

import main.ClayConstants;

import org.lwjgl.opengl.GL11;

import screens.AbstractScreen;

public class BackgroundEffect extends AbstractEffect
{
	public BackgroundEffect(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
	}

	@Override
	public void init()
	{
//		try
//		{
//			_skyTexture = TextureLoader.getTexture("PNG", new FileInputStream(
//					new File("art/sky3.jpg")));
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//		}
	}

	@Override
	public void executeEffect()
	{
		GL11.glColor3f(0.2f, 0.7f, .9f);
		// GL11.glColor3f(0.6f, 0.6f, 1.0f); not main

		// GL11.glColor3f(0.45f, 0.45f, .42f);

		// Draw the sky.
		GL11.glBegin(GL11.GL_POLYGON);
		GL11.glVertex2d(0, ClayConstants.DEFAULT_MAP_HEIGHT);
		GL11.glVertex2d(
				ClayConstants.DEFAULT_MAP_WIDTH,
				ClayConstants.DEFAULT_MAP_HEIGHT);
		GL11.glVertex2d(ClayConstants.DEFAULT_MAP_WIDTH, TILE_Y);
		GL11.glVertex2d(0, TILE_Y);
		GL11.glEnd();

		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, _skyTexture.getTextureID());
		// // continue;
		//
		// GL11.glBegin(GL11.GL_POLYGON);
		// GL11.glTexCoord2f(0, 0);
		// GL11.glVertex2d(0, ClayConstants.DEFAULT_MAP_HEIGHT);
		// GL11.glTexCoord2f(.50f, 0);
		// GL11.glVertex2d(
		// ClayConstants.DEFAULT_MAP_WIDTH,
		// ClayConstants.DEFAULT_MAP_HEIGHT);
		// GL11.glTexCoord2f(.50f, .50f);
		// GL11.glVertex2d(ClayConstants.DEFAULT_MAP_WIDTH, TILE_Y);
		// GL11.glTexCoord2f(0, .50f);
		// GL11.glVertex2d(0, TILE_Y);
		// GL11.glEnd();
	}

	private static final int TILE_Y = ClayConstants.TILE_Y;

	// private static Texture _skyTexture;
}
