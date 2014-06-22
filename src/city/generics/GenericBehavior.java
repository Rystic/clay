package city.generics;

import main.ClayConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import city.generics.data.ItemData;
import city.generics.entities.BuildingEntity;
import city.generics.entities.GolemEntity;
import city.generics.util.FieldParser;

public class GenericBehavior
{
	public GenericBehavior(Node node_)
	{
		Element eElement = (Element) node_;
		_behaviorName = eElement.getAttribute("BehaviorName");
		_behaviorTag = eElement.getAttribute("BehaviorTag");
		_beheviorDescription = eElement.getAttribute("BehaviorDescription");
		_code = eElement.getAttribute("code");
		_require = eElement.getAttribute("require");
		_weightConditions = eElement.getAttribute("weight");
		_golemCost = eElement.getAttribute("golemCost");
		_isPersonalBehavior = FieldParser.parseBoolean(eElement
				.getAttribute("personalBehavior"));
		_limit = FieldParser.parseInt(eElement.getAttribute("limit"));
		_defaultParams = eElement.getAttribute("defaultParams").split(",");
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

	public String calculateBehaviorDescription(Object[] params_)
	{
		String[] behaviorDescription = _beheviorDescription.split(",");
		StringBuilder resultText = new StringBuilder();
		for (String description : behaviorDescription)
		{
			String[] descriptionAndParams = description.split(":");
			if (descriptionAndParams[0]
					.equals(ClayConstants.BEHAVIOR_DESCRIPTION_TEXT))
				resultText.append(descriptionAndParams[1]);
			else if (descriptionAndParams[0]
					.equals(ClayConstants.BEHAVIOR_DESCRIPTION_ITEM))
			{
				String itemTag = params_[Integer
						.parseInt(descriptionAndParams[1])].toString();
				if (itemTag.contains(","))
					itemTag = itemTag.split(",")[0];
				GenericItem item = ItemData.getItem(itemTag);
				resultText.append(item.getItemName());
			}
			else if (descriptionAndParams[0]
					.equals(ClayConstants.BEHAVIOR_DESCRIPTION_BUILDING))
			{
				BuildingEntity building = (BuildingEntity) params_[Integer
						.parseInt(descriptionAndParams[1])];
				resultText.append(building.getBuildingName());
			}
		}
		return resultText.toString();
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
	private final String _beheviorDescription;
	private final String _code;
	private final String _require;
	private final String _weightConditions;
	private final String _golemCost;
	private final Integer _limit;
	private final Boolean _isPersonalBehavior;

	private final Object[] _defaultParams;
}
