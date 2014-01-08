package city.generics.objects;

import org.newdawn.slick.opengl.Texture;

import city.generics.GenericItem;
import city.generics.entities.BuildingEntity;

public class Item
{
	public Item(GenericItem item_)
	{
		_item = item_;
		_claimingBuilding = null;
		_itemIdentifier = NO_IDENTIFIER;
	}
	
	public void release()
	{
		_claimingBuilding = null;
		_itemIdentifier = NO_IDENTIFIER;
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

	public long getItemIdentifier()
	{
		return _itemIdentifier;
	}

	public void setItemIdentifier(long itemIdentifier_)
	{
		_itemIdentifier = itemIdentifier_;
	}
	
	public Texture getTexture()
	{
		return _item.getTexture();
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

		if (_itemIdentifier != otherItem.getItemIdentifier())
			return false;
		
		return getTag().equals((otherItem.getTag()));
	}

	
	private static final long NO_IDENTIFIER = -1;

	private GenericItem _item;

	private BuildingEntity _claimingBuilding;

	private long _itemIdentifier;
}
