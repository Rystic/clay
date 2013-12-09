package city.ai.objects;

import city.entities.BuildingEntity;
import city.generics.GenericItem;

public class Item
{
	public Item(GenericItem item_)
	{
		_item = item_;
		_claimingBuilding = null;
	}

	public String getTag()
	{
		return _item.getItemTag();
	}

	public BuildingEntity getClaimingBuilding()
	{
		return _claimingBuilding;
	}

	public void setClaimingBuilding(BuildingEntity claimingBuilding_)
	{
		_claimingBuilding = claimingBuilding_;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Item))
			return false;
		Item otherItem = (Item) obj;

		if (_claimingBuilding == null)
		{
			if (otherItem.getClaimingBuilding() != null)
				return false;
		}
		else if (!_claimingBuilding.equals(otherItem.getClaimingBuilding()))
			return false;

		return getTag().equals((otherItem.getTag()));
	}

	private GenericItem _item;

	private BuildingEntity _claimingBuilding;
}
