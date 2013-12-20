package city.generics;

import java.awt.Point;
import java.util.Queue;

import main.ClayConstants;
import models.CityModel;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import city.ai.objects.Item;
import city.entities.AbstractEntity;
import city.entities.GolemEntity;
import city.generics.util.GenericUtil;
import city.util.SearchUtil;
import data.ItemData;

public class GenericBehavior
{
	public GenericBehavior(Node node_)
	{
		Element eElement = (Element) node_;
		_behaviorName = eElement.getAttribute("BehaviorName");
		_behaviorTag = eElement.getAttribute("BehaviorTag");
		_code = eElement.getAttribute("code");
		_require = eElement.getAttribute("require");
		_weightConditions = eElement.getAttribute("weight");
		_golemCost = eElement.getAttribute("golemCost");
		_isNeededBehavior = GenericUtil.parseBoolean(eElement
				.getAttribute("neededBehavior"));
		_isPersonalBehavior = GenericUtil.parseBoolean(eElement
				.getAttribute("personalBehavior"));
		_limit = GenericUtil.parseInt(eElement.getAttribute("limit"));
		_defaultParams = new Object[1];
		_defaultParams[0] = eElement.getAttribute("defaultParams");
	}

	public void calculateGolemCost(GolemEntity golem_)
	{
		if (_golemCost.isEmpty())
			return;
		String[] golemCost = _golemCost.split(",");
		for (String cost : golemCost)
		{
			String[] params = cost.split(":");
			String attribute = params[0];
			Double val = Double.parseDouble(params[1]);
			if (attribute.equals("clay"))
				golem_.adjustClay(val);
			else if (attribute.equals("mana"))
				golem_.adjustMana(val);
		}
	}

	public int getLimit()
	{
		return _limit;
	}

	public String getBehaviorName()
	{
		return _behaviorName;
	}

	public String getBehaviorTag()
	{
		return _behaviorTag;
	}

	public String getCode()
	{
		return _code;
	}

	public String getRequired()
	{
		return _require;
	}

	public String getWeightConditions()
	{
		return _weightConditions;
	}

	public Boolean isNeededBehavior()
	{
		return _isNeededBehavior;
	}

	public Boolean isPersonalBehavior()
	{
		return _isPersonalBehavior;
	}

	public Object[] getDefaultParams()
	{
		return _defaultParams;
	}

	private final String _behaviorName;
	private final String _behaviorTag;
	private final String _code;
	private final String _require;
	private final String _weightConditions;
	private final String _golemCost;
	private final Integer _limit;
	private final Boolean _isNeededBehavior;
	private final Boolean _isPersonalBehavior;

	private final Object[] _defaultParams;
}
