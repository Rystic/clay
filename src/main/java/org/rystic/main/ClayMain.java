package org.rystic.main;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.rystic.city.effects.AbstractEffect;
import org.rystic.city.generics.data.BehaviorData;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.city.generics.data.ConversionData;
import org.rystic.city.generics.data.GolemData;
import org.rystic.city.generics.data.ItemData;
import org.rystic.city.processes.AbstractProcess;
import org.rystic.city.util.MouseHandler;
import org.rystic.models.CityModel;
import org.rystic.models.PlayerModel;
import org.rystic.screens.AbstractScreen;
import org.rystic.screens.CityScreen;

public class ClayMain implements Runnable {
	public ClayMain() {
		Thread mainThread = new Thread(this);
		mainThread.start();
	}

	@Override
	public void run() {
		
		initWindow();
        initGameData();

		PlayerModel player = new PlayerModel();
		_screen = new CityScreen(player);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, ClayConstants.DEFAULT_MAP_WIDTH + ClayConstants.DEFAULT_INTERFACE_WIDTH,
				ClayConstants.DEFAULT_MAP_HEIGHT, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		// getDelta();
		// lastFPS = getTime();
        glClearColor(.35f, .15f, 0.0f, 0.0f);
		while (glfwWindowShouldClose(_mainDisplay) == GL_FALSE) {
			// updateFPS();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			glfwSwapBuffers(_mainDisplay); 
			for (AbstractEffect effect : _screen.getEffects()) {
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
				effect.executeEffect();
			}
			if (_screen instanceof CityScreen) {
				CityModel model = (CityModel) _screen.getModel();
				model.getSelectedMenu().update();
			}
			for (AbstractProcess controller : _screen.getControllers()) {
				controller.execute();

			}
			for (AbstractProcess process : _screen.getProcesses()) {
				process.execute();
			}
			glfwPollEvents();
			GL11.glFlush();
		}
		System.exit(0);
	}
	
	private void initWindow()
	{
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
        
        if ( glfwInit() != GL11.GL_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); 
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE); 

		// Create the window
		_mainDisplay = glfwCreateWindow(ClayConstants.DEFAULT_MAP_WIDTH, ClayConstants.DEFAULT_MAP_HEIGHT,
				"a.good.golem", NULL, NULL);

		glfwSetKeyCallback(_mainDisplay, _keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
					glfwSetWindowShouldClose(window, GL_TRUE); // We will detect
																// this in our
																// rendering
																// loop
			}
		});

		glfwSetCursorPosCallback(_mainDisplay, _mouseCallback = new MouseHandler());

        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
        		_mainDisplay,
            (GLFWvidmode.width(vidmode) - ClayConstants.DEFAULT_MAP_WIDTH) / 2,
            (GLFWvidmode.height(vidmode) - ClayConstants.DEFAULT_MAP_HEIGHT) / 2
        );
 
        // Make the OpenGL context current
        glfwMakeContextCurrent(_mainDisplay);
        // Enable v-sync
        glfwSwapInterval(1);
 
        // Make the window visible
        glfwShowWindow(_mainDisplay);
        		
        GLContext.createFromCurrent();
	}

	private void initGameData() {
		BuildingData.init();
		BehaviorData.init();
		ItemData.init();
		GolemData.init(); // Depends on BehaviorData.
		ConversionData.init();
	}

	public static void main(String[] args) {
		new ClayMain();
	}

	// public long getTime()
	// {
	// return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	// }
	//
	// public int getDelta()
	// {
	// long time = getTime();
	// int delta = (int) (time - lastFrame);
	// lastFrame = time;
	//
	// return delta;
	// }
	//
	// public void updateFPS()
	// {
	// if (getTime() - lastFPS > 1000)
	// {
	// Display.setTitle("FPS: " + fps);
	// if (fps < 60)
	// System.out.println("FPS DROP: " + fps);
	// fps = 0; // reset the FPS counter
	// lastFPS += 1000; // add one second
	// }
	// fps++;
	// }

	// private long lastFrame;
	// private long fps;
	// private long lastFPS;

	private AbstractScreen _screen;

	public static GLFWKeyCallback _keyCallback;
	public static GLFWCursorPosCallback _mouseCallback;
    private GLFWErrorCallback errorCallback;
	
	public static long _mainDisplay;

	public static PlayerModel _globalModel;
}
