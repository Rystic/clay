package city.ai.calculators;

import java.util.Random;

import main.ClayConstants;
import screens.AbstractScreen;
import city.generics.entities.GolemEntity;

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
		childGolem_.setIntensity(generateIntensity(
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
					.getIntensity() + 1 : 1;
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
		return (byte) _random.nextInt(4);
	}

	private static byte generateIntensity(AbstractScreen homeScreen_, GolemEntity childGolem_, GolemEntity parentGolem_)
	{
		int baseChance = INTENSITY_BASE_CHANCE;
		byte intensity = 0;
		if (childGolem_.getPersonality() == ClayConstants.PERSONALITY_NONE)
			return 0;
		if (parentGolem_ != null)
		{
			if (parentGolem_.getPsychology() == ClayConstants.PSYCHOLOGY_PARAGON)
				baseChance += 20;
			if (parentGolem_.getPersonality() == childGolem_.getPersonality())
				intensity++;
		}
		while (_random.nextInt(100) < baseChance)
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
