package org.rystic.city.generics;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.opengl.Texture;
import org.rystic.city.generics.data.BehaviorData;
import org.rystic.city.generics.util.FieldParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GenericGolem
{
	public GenericGolem(Node node_)
	{
		Element eElement = (Element) node_;
		_golemName = eElement.getAttribute("GolemName");
		_golemTag = eElement.getAttribute("GolemTag");
		
		_defaultTexture = FieldParser.parseTexture(eElement.getAttribute("DefaultTexture"));
		_lowClayTexture = FieldParser.parseTexture(eElement.getAttribute("LowClayTexture"));
		_lowManaTexture = FieldParser.parseTexture(eElement.getAttribute("LowManaTexture"));
		_lowManaLClayTexture = FieldParser.parseTexture(eElement.getAttribute("LowManaLowClayTexture"));
		
		_neededBehaviors = new ArrayList<GenericBehavior>();
		String[] neededBehaviors = eElement.getAttribute("NeededBehaviors").split(",");
		for (String neededBehavior : neededBehaviors)
		{
			_neededBehaviors.add(BehaviorData.getBehavior(neededBehavior));
		}
		
		_spriteSize = FieldParser.parseInt((eElement
				.getAttribute("SpriteSize")));
		
		_trafficWeight = FieldParser.parseInt((eElement
				.getAttribute("TrafficWeight")));
		
		_moveSpeed = FieldParser
				.parseDouble(eElement.getAttribute("MoveSpeed"));
		_moveVariation = FieldParser.parseDouble(eElement
				.getAttribute("MoveVariation"));
		_startingMana = FieldParser.parseDouble(eElement
				.getAttribute("StartingMana"));
		_startingClay = FieldParser.parseDouble(eElement
				.getAttribute("StartingClay"));
		_maximumMana = FieldParser.parseDouble(eElement
				.getAttribute("MaximumMana"));
		_maximumClay = FieldParser.parseDouble(eElement
				.getAttribute("MaximumClay"));
		_manaLostOnMovement = FieldParser.parseDouble(eElement
				.getAttribute("ManaLostOnMovement"));
		_lowManaThreshold = FieldParser.parseDouble(eElement
				.getAttribute("LowManaThreshold"));
		_lowClayThreshold = FieldParser.parseDouble(eElement
				.getAttribute("LowClayThreshold"));
		_personalBehaviorChance = FieldParser.parseInt(eElement
				.getAttribute("PersonalBehaviorChance"));
		_textureScaling = FieldParser.parseDouble(eElement
				.getAttribute("TextureScaling"));
	}

	public String getGolemName()
	{
		return _golemName;
	}

	public String getGolemTag()
	{
		return _golemTag;
	}
	
	public List<GenericBehavior> getNeededBehaviors()
	{
		return _neededBehaviors;
	}
	
	public Texture getDefaultTexture()
	{
		return _defaultTexture;
	}
	
	public Texture getLowClayTexture()
	{
		return _lowClayTexture;
	}
	
	public Texture getLowManaTexture()
	{
		return _lowManaTexture;
	}
	
	public Texture getLowManaLowClayTexture()
	{
		return _lowManaLClayTexture;
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
	
	public int getSpriteSize()
	{
		return _spriteSize;
	}
	
	public int getTrafficWeight()
	{
		return _trafficWeight;
	}
	
	public double getTextureScaling()
	{
		return _textureScaling;
	}

	private final String _golemName;
	private final String _golemTag;
	
	private final Texture _defaultTexture;
	private final Texture _lowClayTexture;
	private final Texture _lowManaTexture;
	private final Texture _lowManaLClayTexture;
	
	private final List<GenericBehavior> _neededBehaviors;

	private final int _spriteSize;
	private final int _trafficWeight;
	
	private final double _moveSpeed;
	private final double _moveVariation;
	private final double _startingMana;
	private final double _startingClay;
	private final double _maximumMana;
	private final double _maximumClay;
	private final double _manaLostOnMovement;
	private final double _lowManaThreshold;
	private final double _lowClayThreshold;
	private final double _textureScaling;

	private final int _personalBehaviorChance;

}
