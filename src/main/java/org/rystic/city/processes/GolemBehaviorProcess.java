package org.rystic.city.processes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.rystic.city.ai.GolemBrain;
import org.rystic.city.ai.util.BehaviorAndWeight;
import org.rystic.city.entities.golem.GolemEntity;
import org.rystic.city.generics.objects.Behavior;
import org.rystic.main.ClayConstants;
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

		// Check if there are new, queued behaviors. If so, calculate whether or
		// not they're possible.

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
			_pendingBehaviors = new ArrayBlockingQueue<Behavior>(256);
		}

		@Override
		public void run()
		{
			Map<Integer, BehaviorAndWeight> assignedBehaviorMap = new HashMap<Integer, BehaviorAndWeight>();

			List<GolemEntity> inactiveGolems = new ArrayList<GolemEntity>();
			Queue<Behavior> unassignedBehaviors = new ArrayBlockingQueue<Behavior>(
					256);
			
			List<GolemEntity> neededBehaviorGolems = new ArrayList<GolemEntity>();

			GolemEntity golem;
			int bestGolem;
			BehaviorAndWeight bestScore;

			while (true)
			{
				while (!_pendingInactiveGolems.isEmpty())
				{
					inactiveGolems.add(_pendingInactiveGolems.poll());
				}

				while (!_pendingBehaviors.isEmpty())
				{
					unassignedBehaviors.add(_pendingBehaviors.poll());
				}

				if (!inactiveGolems.isEmpty())
				{
					golem = null;
					for (int i = 0; i < inactiveGolems.size(); i++)
					{
						golem = inactiveGolems.get(i);
						BehaviorAndWeight triple = GolemBrain
								.calculateBestBehavior(golem,
										golem.getNeededBehaviors(),
										true);
						if (triple != null)
						{
							triple._behavior
									.setBehaviorProcess(GolemBehaviorProcess.this);
							triple._behavior.setAssignedGolem(golem);
							int requiredComplete = triple._behavior
									.calculateRequired(golem);
							if (requiredComplete == ClayConstants.BEHAVIOR_PASSED)
							{
								golem.setBehavior(triple._behavior);
								neededBehaviorGolems.add(golem);
							}
							else
								behaviorFailed(
										triple._behavior,
										requiredComplete);
						}
					}
					inactiveGolems.removeAll(neededBehaviorGolems);
					neededBehaviorGolems.clear();
					if (inactiveGolems.isEmpty()) continue;

					while (!unassignedBehaviors.isEmpty())
					{
						golem = null;
						bestScore = null;
						bestGolem = -1;
						Behavior behavior = unassignedBehaviors.poll();
						for (int i = 0; i < inactiveGolems.size(); i++)
						{
							golem = inactiveGolems.get(i);
							if (behavior.isInvalid(golem))
								continue;
							BehaviorAndWeight behaviorAndWeight = new BehaviorAndWeight(
									behavior,
									behavior.calculateBehaviorWeight(golem)
											+ behavior.getAddedWeight());
							if (behaviorAndWeight._weight <= 0)
								behaviorAndWeight._behavior
										.requiredFailed(golem);
							else
							{
								BehaviorAndWeight oldBehaviorAndWeight = assignedBehaviorMap
										.get(i);
								if ((oldBehaviorAndWeight == null || oldBehaviorAndWeight._weight < behaviorAndWeight._weight)
										&& (bestScore == null || bestScore._weight < behaviorAndWeight._weight))
								{
									bestScore = behaviorAndWeight;
									bestGolem = i;
								}
							}
						}
						if (bestScore != null)
						{
							BehaviorAndWeight oldBehaviorAndWeight = assignedBehaviorMap
									.get(bestGolem);
							if (oldBehaviorAndWeight != null)
								unassignedBehaviors
										.add(oldBehaviorAndWeight._behavior);
							assignedBehaviorMap.put(bestGolem, bestScore);
						}
					}

					for (Integer i : assignedBehaviorMap.keySet())
					{
						BehaviorAndWeight behaviorAndWeight = assignedBehaviorMap
								.get(i);
						golem = inactiveGolems.get(i);
						int requiredComplete = behaviorAndWeight._behavior
								.calculateRequired(golem);
						if (requiredComplete == ClayConstants.BEHAVIOR_PASSED)
						{
							golem.setBehavior(behaviorAndWeight._behavior);
							behaviorAndWeight._behavior
									.setBehaviorProcess(GolemBehaviorProcess.this);
							behaviorAndWeight._behavior.setAssignedGolem(golem);
							inactiveGolems.remove(golem);
							_activeGolems.add(golem);
						}
					}
					assignedBehaviorMap.clear();
					// calculate needed behaviors
					// calculate personal behaviors

					// calculate behavior weights

					// unreachable behaviors
				}
			}
		}

		private Queue<GolemEntity> _pendingInactiveGolems;
		private Queue<Behavior> _pendingBehaviors;
	}

	public int getBehaviorCount(String behaviorTag)
	{
		return 0;
	}

	public void behaviorComplete(Behavior behavior_, GolemEntity golem_)
	{
		_behaviorAssignmentRunnable._pendingInactiveGolems.add(golem_);
	}

	private Thread _behaviorAssignmentThread;
	private BehaviorAssignmentRunnable _behaviorAssignmentRunnable;

	private Queue<GolemEntity> _pendingActiveGolems;
	private List<GolemEntity> _activeGolems;

	public void behaviorFailed(Behavior behavior, int behaviorFailedObsolete)
	{
		// TODO Auto-generated method stub

	}

}
