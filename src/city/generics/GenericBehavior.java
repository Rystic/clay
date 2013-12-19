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

	public int calculateRequired(CityModel model_, Object[] params_)
	{
		String[] require = _require.split(",");
		for (String requireCondition : require)
		{
			String[] commandAndParams = requireCondition.split(":");
			if (commandAndParams[0]
					.equals(ClayConstants.BEHAVIOR_REQUIRE_STORAGE))
			{
				AbstractEntity entity = (AbstractEntity) params_[0];
				Queue<Point> queue = SearchUtil.search(
						entity,
						entity.getHomeScreen(),
						ClayConstants.SEARCH_STORAGE);
				if (queue.isEmpty())
					return ClayConstants.BEHAVIOR_FAILED_NO_STORAGE;
			}
			if (commandAndParams[0]
					.equals(ClayConstants.BEHAVIOR_REQUIRE_HOUSE))
			{
				AbstractEntity entity = (AbstractEntity) params_[0];
				Queue<Point> queue = SearchUtil.search(
						entity,
						entity.getHomeScreen(),
						ClayConstants.SEARCH_HOUSE);
				if (queue.isEmpty())
					return ClayConstants.BEHAVIOR_FAILED_NO_PATH;
			}
			if (commandAndParams[0]
					.equals(ClayConstants.BEHAVIOR_REQUIRE_ITEM_EXISTS))
			{
				AbstractEntity entity = (AbstractEntity) params_[0];
				Item item = new Item(ItemData.getItem(commandAndParams[1]));
				if (entity.isHolding(item))
					continue;
				Queue<Point> queue = SearchUtil.search(
						entity,
						entity.getHomeScreen(),
						ClayConstants.SEARCH_ITEM_GOAL_ONLY,
						item);
				if (queue.isEmpty())
					return ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS;
			}
		}
		return ClayConstants.BEHAVIOR_PASSED;
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
