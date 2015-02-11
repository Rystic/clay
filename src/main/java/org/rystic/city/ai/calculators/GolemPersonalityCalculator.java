package org.rystic.city.ai.calculators;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.rystic.city.entities.building.BuildingEntity;
import org.rystic.city.entities.golem.GolemEntity;
import org.rystic.city.generics.GenericBuilding;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;

public class GolemPersonalityCalculator
{

	public static void buildPersonality(AbstractScreen homeScreen_, GolemEntity childGolem_, GolemEntity parentGolem_)
	{
		childGolem_.setPersonality(generatePersonality(
				homeScreen_,
				childGolem_,
				parentGolem_));
		childGolem_.setPsychology(generatePsychology(
				homeScreen_,
				childGolem_,
				parentGolem_));
		childGolem_.setGolemanity(generateGolemanity(
				homeScreen_,
				childGolem_,
				parentGolem_));
	}

	private static byte generatePersonality(AbstractScreen homeScreen_, GolemEntity childGolem_, GolemEntity parentGolem_)
	{
		// TODO pull from city stats
		int ambitiousChance = 1;
		int ingeniousChance = 1;
		int creativeChance = 1;
		int noneChance = 5;

		if (parentGolem_ != null)
		{
			int parentPersonality = parentGolem_.getPersonality();
			int parentPersonalityBonus = parentGolem_.getPsychology() == ClayConstants.PSYCHOLOGY_INFLUENTIAL ? parentGolem_
					.getGolemanity() + 1 : 1;
			switch (parentPersonality)
			{
			case ClayConstants.PERSONALITY_AMBITIOUS:
				ambitiousChance += parentPersonalityBonus;
				break;
			case ClayConstants.PERSONALITY_INGENIOUS:
				ingeniousChance += parentPersonalityBonus;
				break;
			case ClayConstants.PERSONALITY_CREATIVE:
				creativeChance += parentPersonalityBonus;
				break;
			}
		}
		int childPersonality = _random.nextInt(ambitiousChance
				+ ingeniousChance + creativeChance + noneChance);
		if (childPersonality < ambitiousChance)
			return ClayConstants.PERSONALITY_AMBITIOUS;
		if (childPersonality < ambitiousChance + ingeniousChance)
			return ClayConstants.PERSONALITY_INGENIOUS;
		if (childPersonality < ambitiousChance + ingeniousChance
				+ creativeChance)
			return ClayConstants.PERSONALITY_CREATIVE;
		return ClayConstants.PERSONALITY_NONE;
	}

	private static byte generatePsychology(AbstractScreen homeScreen_, GolemEntity childGolem_, GolemEntity parentGolem_)
	{
		if (childGolem_.getPersonality() == ClayConstants.PERSONALITY_NONE)
			return 0;
		CityModel model = (CityModel) homeScreen_.getModel();
		Map<Integer, List<BuildingEntity>> buildingMap = model.getBuildingMap();
		int paragonChance = 0;
		int elitistChance = 0;
		int hardWorkingChance = 1;
		int influentialChance = 0;
		for (Integer buildingId : buildingMap.keySet())
		{
			GenericBuilding building = BuildingData.getBuildingById(buildingId);
			if (building.getPsychologyType() != ClayConstants.PSYCHOLOGY_NONE)
			{
				int influence = buildingMap.get(buildingId).size()
						* building.getPsychologyInfluence();
				switch (building.getPsychologyType())
				{
				case ClayConstants.PSYCHOLOGY_PARAGON:
					paragonChance += influence;
					break;
				case ClayConstants.PSYCHOLOGY_ELITIST:
					elitistChance += influence;
					break;
				case ClayConstants.PSYCHOLOGY_HARD_WORKING:
					hardWorkingChance += influence;
					break;
				case ClayConstants.PSYCHOLOGY_INFLUENTIAL:
					influentialChance += influence;
					break;
				}
			}
		}
		int childPsychology = _random.nextInt(paragonChance + elitistChance
				+ hardWorkingChance + influentialChance);
		if (childPsychology < paragonChance)
			return ClayConstants.PSYCHOLOGY_PARAGON;
		if (childPsychology < paragonChance + elitistChance)
			return ClayConstants.PSYCHOLOGY_ELITIST;
		if (childPsychology < paragonChance + elitistChance + hardWorkingChance)
			return ClayConstants.PSYCHOLOGY_HARD_WORKING;
		return ClayConstants.PSYCHOLOGY_INFLUENTIAL;
	}

	private static byte generateGolemanity(AbstractScreen homeScreen_, GolemEntity childGolem_, GolemEntity parentGolem_)
	{
		int baseChance = GOLEMANITY_BASE_CHANCE;
		byte golemanity = 0;
		if (childGolem_.getPersonality() == ClayConstants.PERSONALITY_NONE)
			return 0;
		if (parentGolem_ != null)
		{
			if (parentGolem_.getPsychology() == ClayConstants.PSYCHOLOGY_PARAGON)
				baseChance += 20;
			if (parentGolem_.getPersonality() == childGolem_.getPersonality())
				golemanity++;
		}
		while (_random.nextInt(100) < baseChance)
		{
			golemanity++;
			baseChance -= GOLEMANITY_CHANCE_DECREASE;
		}
		return golemanity;
	}

	private static final Random _random = new Random();

	private static final int GOLEMANITY_BASE_CHANCE = 50;
	private static final int GOLEMANITY_CHANCE_DECREASE = 10;
}
