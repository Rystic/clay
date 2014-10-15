package org.rystic.main;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.rystic.city.effects.AbstractEffect;
import org.rystic.city.generics.data.BehaviorData;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.city.generics.data.ConversionData;
import org.rystic.city.generics.data.GolemData;
import org.rystic.city.generics.data.ItemData;
import org.rystic.city.processes.AbstractProcess;
import org.rystic.models.CityModel;
import org.rystic.models.PlayerModel;
import org.rystic.screens.AbstractScreen;
import org.rystic.screens.CityScreen;

public class ClayMain implements Runnable
{
	public ClayMain()
	{
		Thread mainThread = new Thread(this);
		mainThread.start();
	}

	@Override
	public void run()
	{
		try
		{
			// DisplayMode[] modes = Display.getAvailableDisplayModes();
			Display.setDisplayMode(new DisplayMode(
					ClayConstants.DEFAULT_MAP_WIDTH
							+ ClayConstants.DEFAULT_INTERFACE_WIDTH,
					ClayConstants.DEFAULT_MAP_HEIGHT));
			Display.setFullscreen(false);
			Display.create();
		} catch (LWJGLException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		BuildingData.init();
		BehaviorData.init();
		ItemData.init();
		GolemData.init(); // Depends on BehaviorData.
		ConversionData.init();
		PlayerModel player = new PlayerModel();
		_screen = new CityScreen(player);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(
				0,
				ClayConstants.DEFAULT_MAP_WIDTH
						+ ClayConstants.DEFAULT_INTERFACE_WIDTH,
				ClayConstants.DEFAULT_MAP_HEIGHT,
				0,
				1,
				-1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		getDelta();
		lastFPS = getTime();
		while (!Display.isCloseRequested())
		{
			updateFPS();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			for (AbstractEffect effect : _screen.getEffects())
			{
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
				effect.executeEffect();
			}
			if (_screen instanceof CityScreen)
			{
				CityModel model = (CityModel) _screen.getModel();
				model.getSelectedMenu().update();
			}
			for (AbstractProcess controller : _screen.getControllers())
			{
				controller.execute();

			}
			for (AbstractProcess process : _screen.getProcesses())
			{
				process.execute();
			}
			GL11.glFlush();
			Display.sync(60);
			Display.update();
		}
		System.exit(0);
	}

	public static void main(String[] args)
	{
		new ClayMain();
	}

	public long getTime()
	{
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	public int getDelta()
	{
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;

		return delta;
	}

	public void updateFPS()
	{
		if (getTime() - lastFPS > 1000)
		{
			Display.setTitle("FPS: " + fps);
		if (fps < 60)
				System.out.println("FPS DROP: " + fps);
			fps = 0; // reset the FPS counter
			lastFPS += 1000; // add one second
		}
		fps++;
	}

	private long lastFrame;
	private long fps;
	private long lastFPS;

	private AbstractScreen _screen;

	public static PlayerModel _globalModel;
}
