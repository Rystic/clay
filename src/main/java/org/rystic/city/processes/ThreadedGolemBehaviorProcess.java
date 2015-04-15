package org.rystic.city.processes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.rystic.city.entities.golem.GolemEntity;
import org.rystic.city.generics.objects.Behavior;
import org.rystic.main.ClayConstants;
import org.rystic.screens.AbstractScreen;

public class ThreadedGolemBehaviorProcess extends AbstractProcess
{

	public ThreadedGolemBehaviorProcess(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_behaviorAssignmentRunnable = new BehaviorAssignmentRunnable();
		_behaviorAssignmentThread = new Thread(_behaviorAssignmentRunnable);
		_activeGolems = new ArrayList<GolemEntity>();

		_behaviorAssignmentThread.start();
	}

	@Override
	public void execute()
	{
		while (!_pendingActiveGolems.isEmpty())
		{
			_activeGolems.add(_pendingActiveGolems.poll());
		}
		for (GolemEntity golem : _activeGolems)
		{
			golem.executeBehavior();
		}
	}

	public void addInactiveGolem(GolemEntity golem_)
	{
		_behaviorAssignmentRunnable.pushGolem(golem_);
	}

	private void addActiveGolem(GolemEntity golem_)
	{
		_pendingActiveGolems.add(golem_);
	}

	private void queueBehavior(Behavior behavior_)
	{
		
	}
	
	private class BehaviorAssignmentRunnable implements Runnable
	{
		public BehaviorAssignmentRunnable()
		{
			_pendingInactiveGolems = new ArrayBlockingQueue<GolemEntity>(256);
			_inactiveGolems = new ArrayList<GolemEntity>();
			
			// failed -> obsolete is too vague.
		}

		@Override
		public void run()
		{
			while (true)
			{
				while (!_pendingInactiveGolems.isEmpty())
				{
					_inactiveGolems.add(_pendingInactiveGolems.poll());
				}
				
				// loop through behaviors and remove the obsolete ones

				if (!_inactiveGolems.isEmpty())
				{
					// calculate needed behaviors
					// calculate personal behaviors
					
					// unreachable behaviors
				}
			}
		}

		public void pushGolem(GolemEntity golem_)
		{
			_pendingInactiveGolems.add(golem_);
		}

		private Queue<GolemEntity> _pendingInactiveGolems;
		private List<GolemEntity> _inactiveGolems;
		
		private Map<Integer, List<Behavior>> _failedBehaviorMap;
		
	}

	private Thread _behaviorAssignmentThread;
	private BehaviorAssignmentRunnable _behaviorAssignmentRunnable;

	private Queue<GolemEntity> _pendingActiveGolems;
	private List<GolemEntity> _activeGolems;
	
}
