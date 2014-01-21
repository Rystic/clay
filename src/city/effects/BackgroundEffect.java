package city.effects;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import main.ClayConstants;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import screens.AbstractScreen;

public class BackgroundEffect extends AbstractEffect
{
	public BackgroundEffect(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_colorCycle = new ArrayList<Color>();
		_colorIncrementThreshold = 1200;
		_colorCycle.add(new Color(.2f, .2f, .6f));
		_colorCycle.add(new Color(.4f, .4f, .6f));
		_colorCycle.add(new Color(.6f, .6f, .6f));
		_colorCycle.add(new Color(.8f, .8f, .8f));
		_colorCycle.add(new Color(.8f, .6f, .6f));
		_colorCycle.add(new Color(.8f, .4f, .4f));
		_colorCycle.add(new Color(.3f, .4f, .4f));
		_colorPointer = 0;
	}

	@Override
	public void init()
	{
		try
		{
			_skyTexture = TextureLoader.getTexture("PNG", new FileInputStream(
					new File("art/sky3.jpg")));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void executeEffect()
	{
		GL11.glColor3f(
				_colorCycle.get(_colorPointer).getRed(),
				_colorCycle.get(_colorPointer).getGreen(),
				_colorCycle.get(_colorPointer).getBlue());
		 GL11.glColor3f(0.2f, 0.7f, .9f);
		// GL11.glColor3f(0.6f, 0.6f, 1.0f); not main

		// GL11.glColor3f(0.45f, 0.45f, .42f);

		// Draw the sky.
		  GL11.glBegin(GL11.GL_POLYGON);
		  GL11.glVertex2d(0, ClayConstants.DEFAULT_MAP_HEIGHT);
		  GL11.glVertex2d(ClayConstants.DEFAULT_MAP_WIDTH,
		 ClayConstants.DEFAULT_MAP_HEIGHT);
		  GL11.glVertex2d(ClayConstants.DEFAULT_MAP_WIDTH, TILE_Y);
		  GL11.glVertex2d(0, TILE_Y);
		  GL11.glEnd();
		
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, _skyTexture.getTextureID());
//		// continue;
//
//		GL11.glBegin(GL11.GL_POLYGON);
//		GL11.glTexCoord2f(0, 0);
//		GL11.glVertex2d(0, ClayConstants.DEFAULT_MAP_HEIGHT);
//		GL11.glTexCoord2f(.50f, 0);
//		GL11.glVertex2d(
//				ClayConstants.DEFAULT_MAP_WIDTH,
//				ClayConstants.DEFAULT_MAP_HEIGHT);
//		GL11.glTexCoord2f(.50f, .50f);
//		GL11.glVertex2d(ClayConstants.DEFAULT_MAP_WIDTH, TILE_Y);
//		GL11.glTexCoord2f(0, .50f);
//		GL11.glVertex2d(0, TILE_Y);
//		GL11.glEnd();
	}

	private static final int TILE_Y = ClayConstants.TILE_Y;

	private List<Color> _colorCycle;

	private int _colorPointer;
	private int _colorIncrement;
	private int _colorIncrementThreshold;

	private static Texture _skyTexture;
}
