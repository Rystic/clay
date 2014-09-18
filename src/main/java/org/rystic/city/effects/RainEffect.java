package org.rystic.city.effects;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.rystic.main.ClayConstants;
import org.rystic.screens.AbstractScreen;

public class RainEffect extends AbstractEffect
{
	public RainEffect(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_existingDroplets = new ArrayList<Droplet>(_maxDroplets);
		_deadDroplets = new ArrayList<Droplet>(_maxDroplets);

		_maxDroplets = 200;
		_dropletSpeedVariance = 6;
		_intensity = 5;
	}
	
	public void executeEffect()
	{
		if (_existingDroplets.size() < _maxDroplets)
		{
			for (int i = 0; i < _intensity; i++)
			{
				_existingDroplets.add(new Droplet());
				if (_existingDroplets.size() == _maxDroplets)
				{
					break;
				}
			}
		}
		for (Droplet droplet : _existingDroplets)
		{
			GL11.glColor3f(0.0f, 0.0f, droplet._color);
			double x = droplet._xPos;
			double y = -droplet._yPos;
			double size = droplet._size;
			GL11.glBegin(GL11.GL_POLYGON);
			GL11.glVertex2d(x, y);
			GL11.glVertex2d(x - (.50 * size), y);
			GL11.glVertex2d(x, y - (1.75 * size));
			GL11.glVertex2d(x + 1, y - (1.75 * size));

			droplet.update();
			GL11.glEnd();
			if (droplet._yPos < -1000)
			{
				_deadDroplets.add(droplet);
			}
		}
		_existingDroplets.removeAll(_deadDroplets);
		_deadDroplets.clear();
	}

	private class Droplet
	{

		public Droplet()
		{
			Random random = new Random();
			_size = random.nextDouble() + 7;
			_xPos = random.nextInt(ClayConstants.DEFAULT_MAP_WIDTH + 50);
			_yPos = ClayConstants.DEFAULT_MAP_HEIGHT;
			_color = .88f + random.nextFloat();
			_fallSpeed += 20 + random.nextInt(_dropletSpeedVariance);
		}

		public void update()
		{
			_xPos -= (_size / 3);
			_yPos -= _fallSpeed;
			_color -= .01f;
		}
		
		double _size;
		double _xPos;
		double _yPos;
		double _fallSpeed;
		float _color;

	}

	private ArrayList<Droplet> _existingDroplets;
	private ArrayList<Droplet> _deadDroplets;

	private int _maxDroplets;
	private int _dropletSpeedVariance;
	private int _intensity;

	
}
