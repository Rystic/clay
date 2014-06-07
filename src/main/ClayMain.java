package main;

import models.CityModel;
import models.PlayerModel;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import screens.AbstractScreen;
import screens.CityScreen;
import city.effects.AbstractEffect;
import city.generics.data.BehaviorData;
import city.generics.data.BuildingData;
import city.generics.data.ConversionData;
import city.generics.data.GolemData;
import city.generics.data.ItemData;
import city.processes.AbstractProcess;

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
		GolemData.init();
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
		while (!Display.isCloseRequested())
		{
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
			Display.update();
			Display.sync(60);
		}
		System.exit(0);
	}

	public static void main(String[] args)
	{
		new ClayMain();
	}

	private AbstractScreen _screen;

	public static PlayerModel _globalModel;
}
