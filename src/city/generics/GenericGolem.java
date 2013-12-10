package city.generics;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import city.generics.util.GenericUtil;

public class GenericGolem
{
	public GenericGolem(Node node_)
	{
		Element eElement = (Element) node_;
		_golemName = eElement.getAttribute("GolemName");
		_golemTag = eElement.getAttribute("GolemTag");
		_moveSpeed = GenericUtil.parseDouble(eElement.getAttribute("MoveSpeed"));
		_moveVariation = GenericUtil.parseDouble(eElement.getAttribute("MoveVariation"));
		_startingMana = GenericUtil.parseDouble(eElement.getAttribute("StartingMana"));
		_startingClay = GenericUtil.parseDouble(eElement.getAttribute("StartingClay"));
		_startingClayMaximum = GenericUtil.parseDouble(eElement.getAttribute("StartingClayMaximum"));
		_maximumMana = GenericUtil.parseDouble(eElement.getAttribute("MaximumMana"));
		_maximumClay = GenericUtil.parseDouble(eElement.getAttribute("MaximumClay"));
		_manaLostOnMovement = GenericUtil.parseDouble(eElement.getAttribute("ManaLostOnMovement"));
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
	
}
