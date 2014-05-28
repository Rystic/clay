package city.processes;

import java.util.Map;

import models.CityModel;
import screens.AbstractScreen;

public class ResourceManagerProcess extends AbstractProcess
{
	public ResourceManagerProcess(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
		_requiresUpdate = false;
	}

	@Override
	public void execute()
	{
		if (_requiresUpdate)
		{
			Map<String, Map<String, Integer>> itemInventory = _model
					.getItemInventory();
			Map<String, Integer> itemRatios = _model.getItemRatios();
			for (String familyName : itemInventory.keySet())
			{
				Integer total = 0;
				Map<String, Integer> familyInventory = itemInventory
						.get(familyName);
				for (String item : familyInventory.keySet())
				{
					total += familyInventory.get(item);
				}
				for (String item : familyInventory.keySet())
				{
					Double percentage = 100 * (double) (familyInventory.get(item) / total);
					System.out.println("The percentage of " + item + " is " + percentage + " with a desired percentage of " + itemRatios.get(item));
				}
			}
		}
	}

	public void requiresUpdate()
	{
		_requiresUpdate = true;
	}

	private boolean _requiresUpdate;

	private CityModel _model;
}
