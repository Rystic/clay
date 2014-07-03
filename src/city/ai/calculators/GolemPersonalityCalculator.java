package city.ai.calculators;

import java.util.Random;

import main.ClayConstants;
import screens.AbstractScreen;
import city.generics.entities.GolemEntity;

public class GolemPersonalityCalculator
{
	public static byte generatePersonality(AbstractScreen homeScreen_, GolemEntity childGolem_, GolemEntity parentGolem_)
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
					.getIntensity() : 1;
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

	public static byte generatePsychology(AbstractScreen homeScreen_, GolemEntity golem_)
	{
		if (golem_.getPersonality() == ClayConstants.PERSONALITY_NONE) return 0;
		return (byte) _random.nextInt(4);
	}
	
	public static byte generateIntensity()
	{
		int baseChance = INTENSITY_BASE_CHANCE;
		byte intensity = 0;
		while (_random.nextInt(100) > baseChance)
		{
			intensity++;
			baseChance -= INTENSITY_CHANCE_DECREASE;
		}
		return intensity;
	}


	private static final Random _random = new Random();
	
	private static final int INTENSITY_BASE_CHANCE = 50;
	private static final int INTENSITY_CHANCE_DECREASE = 10;
}
