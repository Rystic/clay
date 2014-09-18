package org.rystic.screens;

import java.util.ArrayList;
import java.util.List;

import org.rystic.city.effects.AbstractEffect;
import org.rystic.city.processes.AbstractProcess;
import org.rystic.models.AbstractModel;
import org.rystic.models.PlayerModel;

public abstract class AbstractScreen
{
	public AbstractScreen(PlayerModel playerModel_)
	{
		_playerModel = playerModel_;
		_effects = new ArrayList<AbstractEffect>();
		_processes = new ArrayList<AbstractProcess>();
		_controllers = new ArrayList<AbstractProcess>();
		init();
	}

	protected void init()
	{

	}

	public AbstractEffect getEffect(Class<?> c_)
	{
		for (AbstractEffect effect : _effects)
		{
			if (effect.getClass().equals(c_))
				return effect;
		}
		return null;
	}

	public AbstractProcess getProcess(Class<?> c_)
	{
		for (AbstractProcess process : _processes)
		{
			if (process.getClass().equals(c_))
				return process;
		}
		return null;
	}

	public List<AbstractEffect> getEffects()
	{
		return _effects;
	}

	public List<AbstractProcess> getProcesses()
	{
		return _processes;
	}

	public List<AbstractProcess> getControllers()
	{
		return _controllers;
	}

	public AbstractModel getModel()
	{
		return _model;
	}
	
	public PlayerModel getPlayerModel()
	{
		return _playerModel;
	}

	protected List<AbstractEffect> _effects;
	protected List<AbstractProcess> _processes;
	protected List<AbstractProcess> _controllers;

	protected AbstractModel _model;
	
	protected PlayerModel _playerModel;

}
