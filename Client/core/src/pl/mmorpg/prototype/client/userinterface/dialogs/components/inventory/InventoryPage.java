package pl.mmorpg.prototype.client.userinterface.dialogs.components.inventory;

import java.awt.Point;
import java.util.*;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import pl.mmorpg.prototype.client.exceptions.NoSuchItemInInventoryException;
import pl.mmorpg.prototype.client.items.ItemInventoryPosition;
import pl.mmorpg.prototype.client.items.ItemUseable;
import pl.mmorpg.prototype.client.items.StackableItem;
import pl.mmorpg.prototype.client.objects.icons.items.Item;
import pl.mmorpg.prototype.client.userinterface.dialogs.InventoryDialog;
import pl.mmorpg.prototype.client.userinterface.dialogs.ItemCounter;
import pl.mmorpg.prototype.client.userinterface.dialogs.components.ButtonCreator;

public class InventoryPage extends VerticalGroup implements ItemCounter
{
	private static final int INVENTORY_FIELDS_HEIGHT_NUMBER = 5;
	private static final int INVENTORY_FIELDS_WIDTH_NUMBER = 5;

	private final Map<Point, ButtonField<Item>> inventoryFields = new HashMap<>();
	private final InventoryDialog inventoryDialog;

	public InventoryPage(InventoryDialog inventoryDialog, int pageIndex)
	{
		this.inventoryDialog = inventoryDialog;

		space(0).pad(0).padRight(5).fill();
		for (int i = 0; i < INVENTORY_FIELDS_HEIGHT_NUMBER; i++)
		{
			HorizontalGroup buttonRow = new HorizontalGroup().space(0).pad(0).fill();
			for (int j = 0; j < INVENTORY_FIELDS_WIDTH_NUMBER; j++)
			{
				Point cellPosition = new Point(j, i);
				ItemInventoryPosition inventoryPosition = new ItemInventoryPosition(pageIndex, cellPosition);
				ButtonField<Item> button =ButtonCreator.createButtonField(() -> buttonClicked(inventoryPosition));
				inventoryFields.put(cellPosition, button);
				buttonRow.addActor(button);
			}
			addActor(buttonRow);
		}
		padBottom(8);
	}

	private void buttonClicked(ItemInventoryPosition cellPosition)
	{
		inventoryDialog.buttonClicked(cellPosition);
	}

	public ButtonField<Item> getField(Point point)
	{
		return inventoryFields.get(point);
	}

	public Collection<ButtonField<Item>> getAllFields()
	{
		return inventoryFields.values();
	}

	public boolean removeIfHas(Item item)
	{
		for (ButtonField<Item> field : inventoryFields.values())
		{
			Item fieldItem = field.getContent();
			if (fieldItem == item)	
			{
				field.removeContent();
				return true;
			}
		}
		return false;
	}
	
	public void put(Item item, Point position)
	{
		inventoryFields.get(position).put(item);
	}


	public Item getItem(long itemId)
	{
		for (ButtonField<Item> field : inventoryFields.values())
		{
			Item item = field.getContent();
			if (item != null && item.getId() == itemId)
				return item;
		}
		return null;
	}

	public ItemUseable useIfHas(long itemId)
	{
		for (ButtonField<Item> field : inventoryFields.values())
		{
			Item item = field.getContent();
			if (item != null && item.getId() == itemId)
			{
				ItemUseable itemUseable = (ItemUseable)item;
				itemUseable.useInterfaceUpdate();
				if(itemUseable.shouldBeRemoved())
					field.removeContent();
				return itemUseable;
			}
		}
		return null;
	}
	
	@Override
	public int countItems(String itemIdentifier)
	{
		int itemCounter = 0;
		for (ButtonField<Item> field : inventoryFields.values())
		{
			Item item = field.getContent();
			if (item != null && item.getIdentifier().equals(itemIdentifier))
				itemCounter += getItemCount(item);
		}
		return itemCounter;
	}

	private int getItemCount(Item item)
	{
		if(item instanceof StackableItem)
			return ((StackableItem)item).getItemCount();
		return 1;
	}

	public int getInventoryFieldsHeightNumber()
	{
		return INVENTORY_FIELDS_HEIGHT_NUMBER;
	}

	public int getInventoryFieldsWidthNumber()
	{
		return INVENTORY_FIELDS_WIDTH_NUMBER;
	}

	public Item searchForItem(String itemIdentifier)
	{
		return inventoryFields.values().stream()
				.map(ButtonField::getContent)
				.filter(Objects::nonNull)
				.filter(item -> item.getIdentifier().equals(itemIdentifier))
				.findAny()
				.orElse(null);
	}

	public Item removeItem(long itemId)
	{
		Optional<ButtonField<Item>> itemField = inventoryFields.values().stream()
				.filter(field -> field.getContent() != null && field.getContent().getId() == itemId)
				.findAny();
		if (itemField.isPresent())
		{
			Item item = itemField.get().getContent();
			itemField.get().removeContent();
			return item;
		}
		return null;
	}
}
