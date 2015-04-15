package org.rystic.city.processes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.rystic.city.entities.golem.GolemEntity;
import org.rystic.city.generics.objects.Behavior;
import org.rystic.screens.AbstractScreen;

public class GolemBehaviorProcess extends AbstractProcess
{

	public GolemBehaviorProcess(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_behaviorAssignmentRunnable = new BehaviorAssignmentRunnable();
		_behaviorAssignmentThread = new Thread(_behaviorAssignmentRunnable);
		_behaviorAssignmentThread.setDaemon(true);
		
		_pendingActiveGolems = new ArrayBlockingQueue<GolemEntity>(256);
		_activeGolems = new ArrayList<GolemEntity>();

		_behaviorAssignmentThread.start();
	}

	@Override
	public void execute()
	{
		// Add pending inactive golems to the inactive golem list.
		while (!_pendingActiveGolems.isEmpty())
		{
			_activeGolems.add(_pendingActiveGolems.poll());
		}
		
		// Check if there are new, queued behaviors. If so, calculate whether or not they're possible.
		
		
		for (GolemEntity golem : _activeGolems)
		{
			golem.executeBehavior();
		}
	}

	public void addInactiveGolem(GolemEntity golem_)
	{
		_behaviorAssignmentRunnable._pendingInactiveGolems.add(golem_);
	}

	public void queueBehavior(Behavior behavior_)
	{
		_behaviorAssignmentRunnable._pendingBehaviors.add(behavior_);
	}
	
	private class BehaviorAssignmentRunnable implements Runnable
	{
		public BehaviorAssignmentRunnable()
		{
			_pendingInactiveGolems = new ArrayBlockingQueue<GolemEntity>(256);
			_inactiveGolems = new ArrayList<GolemEntity>();
			
			_pendingBehaviors = new ArrayBlockingQueue<Behavior>(256);
			_unassignedBehaviors = new ArrayList<Behavior>();
			
			// failed -> obsolete is too vague.
		}

		@Override
		public void run()
		{
			Map<GolemEntity, Behavior> assignedBehaviorMap = new HashMap<GolemEntity, Behavior>();
			while (true)
			{
				while (!_pendingInactiveGolems.isEmpty())
				{
					_inactiveGolems.add(_pendingInactiveGolems.poll());
				}
				
				while (!_pendingBehaviors.isEmpty())
				{
					_unassignedBehaviors.add(_pendingBehaviors.poll());
				}
				
				if (!_inactiveGolems.isEmpty())
				{

					for (Behavior behavior : _unassignedBehaviors)
					{
					}
					// calculate needed behaviors
					// calculate personal behaviors
					
					// calculate behavior weights
					
					// unreachable behaviors
				}
			}
		}

		private Queue<GolemEntity> _pendingInactiveGolems;
		private List<GolemEntity> _inactiveGolems;
		
		private Queue<Behavior> _pendingBehaviors;
		private List<Behavior> _unassignedBehaviors;
		
	}

	private Thread _behaviorAssignmentThread;
	private BehaviorAssignmentRunnable _behaviorAssignmentRunnable;

	private Queue<GolemEntity> _pendingActiveGolems;
	private List<GolemEntity> _activeGolems;
	public void behaviorFailed(Behavior behavior, int behaviorFailedObsolete)
	{
		// TODO Auto-generated method stub
		
	}

	public int getBehaviorCount(String behaviorTag)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public void behaviorComplete(Behavior behavior)
	{
		// TODO Auto-generated method stub
		
	}
	
}
