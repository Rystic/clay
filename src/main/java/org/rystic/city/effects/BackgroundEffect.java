package org.rystic.city.effects;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.rystic.city.generics.util.FieldParser;
import org.rystic.main.ClayConstants;
import org.rystic.screens.AbstractScreen;

public class BackgroundEffect extends AbstractEffect
{
	public BackgroundEffect(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
	}

	@Override
	public void init()
	{
		try
		{
			_skyTexture = FieldParser.parseTexture("sky4.png");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void executeEffect()
	{
		GL11.glColor3f(.85f, .85f, .95f);
		// GL11.glColor3f(0.6f, 0.6f, 1.0f); not main

		// GL11.glColor3f(0.45f, 0.45f, .42f);

		// Draw the sky.
		// GL11.glBegin(GL11.GL_POLYGON);
		// GL11.glVertex2d(0, ClayConstants.DEFAULT_MAP_HEIGHT);
		// GL11.glVertex2d(
		// ClayConstants.DEFAULT_MAP_WIDTH,
		// ClayConstants.DEFAULT_MAP_HEIGHT);
		// GL11.glVertex2d(ClayConstants.DEFAULT_MAP_WIDTH, TILE_Y);
		// GL11.glVertex2d(0, TILE_Y);
		// GL11.glEnd();

//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, _skyTexture.getTexture//ID());
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, ClayConstants.DEFAULT_MAP_WIDTH, ClayConstants.DEFAULT_MAP_HEIGHT, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, _skyTexture);
		// continue;

		GL11.glBegin(GL11.GL_POLYGON);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2d(0, 0);
		GL11.glTexCoord2f(.50f, 0);
		GL11.glVertex2d(ClayConstants.DEFAULT_MAP_WIDTH, 0);
		GL11.glTexCoord2f(.50f, .50f);
		GL11.glVertex2d(
				ClayConstants.DEFAULT_MAP_WIDTH,
				ClayConstants.DEFAULT_MAP_HEIGHT);
		GL11.glTexCoord2f(0, .50f);
		GL11.glVertex2d(0, ClayConstants.DEFAULT_MAP_HEIGHT);

		GL11.glEnd();
	}

	private static ByteBuffer _skyTexture;
}
