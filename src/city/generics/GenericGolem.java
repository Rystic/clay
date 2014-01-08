package city.generics;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import city.generics.util.XmlFieldParser;

public class GenericGolem
{
	public GenericGolem(Node node_)
	{
		Element eElement = (Element) node_;
		_golemName = eElement.getAttribute("GolemName");
		_golemTag = eElement.getAttribute("GolemTag");
		_moveSpeed = XmlFieldParser
				.parseDouble(eElement.getAttribute("MoveSpeed"));
		_moveVariation = XmlFieldParser.parseDouble(eElement
				.getAttribute("MoveVariation"));
		_startingMana = XmlFieldParser.parseDouble(eElement
				.getAttribute("StartingMana"));
		_startingClay = XmlFieldParser.parseDouble(eElement
				.getAttribute("StartingClay"));
		_startingClayMaximum = XmlFieldParser.parseDouble(eElement
				.getAttribute("StartingClayMaximum"));
		_maximumMana = XmlFieldParser.parseDouble(eElement
				.getAttribute("MaximumMana"));
		_maximumClay = XmlFieldParser.parseDouble(eElement
				.getAttribute("MaximumClay"));
		_manaLostOnMovement = XmlFieldParser.parseDouble(eElement
				.getAttribute("ManaLostOnMovement"));
		_lowManaThreshold = XmlFieldParser.parseDouble(eElement
				.getAttribute("LowManaThreshold"));
		_lowClayThreshold = XmlFieldParser.parseDouble(eElement
				.getAttribute("LowClayThreshold"));
		_personalBehaviorChance = XmlFieldParser.parseInt(eElement
				.getAttribute("PersonalBehaviorChance"));
	}

	public String getGolemName()
	{
		return _golemName;
	}

	public String getGolemTag()
	{
		return _golemTag;
	}

	public double getMoveSpeed()
	{
		return _moveSpeed;
	}

	public double getMoveVariation()
	{
		return _moveVariation;
	}

	public double getStartingMana()
	{
		return _startingMana;
	}

	public double getStartingClay()
	{
		return _startingClay;
	}

	public double getStartingClayMaximum()
	{
		return _startingClayMaximum;
	}

	public double getMaximumMana()
	{
		return _maximumMana;
	}

	public double getMaximumClay()
	{
		return _maximumClay;
	}

	public double getManaLostOnMovement()
	{
		return _manaLostOnMovement;
	}

	public double getLowManaThreshold()
	{
		return _lowManaThreshold;
	}

	public double getLowClayThreshold()
	{
		return _lowClayThreshold;
	}
	
	public int getPersonalBehaviorChance()
	{
		return _personalBehaviorChance;
	}

	private final String _golemName;
	private final String _golemTag;

	private final double _moveSpeed;
	private final double _moveVariation;
	private final double _startingMana;
	private final double _startingClay;
	private final double _startingClayMaximum;
	private final double _maximumMana;
	private final double _maximumClay;
	private final double _manaLostOnMovement;
	private final double _lowManaThreshold;
	private final double _lowClayThreshold;

	private final int _personalBehaviorChance;

}
