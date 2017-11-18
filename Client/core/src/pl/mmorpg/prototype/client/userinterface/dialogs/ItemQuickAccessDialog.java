package pl.mmorpg.prototype.client.userinterface.dialogs;

import java.util.Objects;
import java.util.stream.Stream;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import pl.mmorpg.prototype.client.communication.PacketsSender;
import pl.mmorpg.prototype.client.exceptions.CannotUseThisItemException;
import pl.mmorpg.prototype.client.exceptions.NoSuchItemInQuickAccessBarException;
import pl.mmorpg.prototype.client.items.ItemUseable;
import pl.mmorpg.prototype.client.objects.icons.items.Item;
import pl.mmorpg.prototype.client.objects.monsters.Monster;
import pl.mmorpg.prototype.client.userinterface.UserInterface;
import pl.mmorpg.prototype.client.userinterface.dialogs.components.ItemQuickAccessIcon;
import pl.mmorpg.prototype.client.userinterface.dialogs.components.inventory.ButtonField;
import pl.mmorpg.prototype.client.userinterface.dialogs.components.inventory.InventoryTextField;

public class ItemQuickAccessDialog extends QuickAccessDialog<ItemQuickAccessIcon>
{
	private UserInterface linkedInterface;

	public ItemQuickAccessDialog(UserInterface linkedInterface)
	{
		super("Quick access");
		this.linkedInterface = linkedInterface;
		this.setX(430);
	}

	@Override
	protected ButtonField<ItemQuickAccessIcon> createField(int cellPosition)
	{
		InventoryTextField<ItemQuickAccessIcon> inventoryField = new InventoryTextField<>(
				"F" + String.valueOf(cellPosition + 1));
		inventoryField.setTextShiftX(-16);
		inventoryField.setTextShiftY(-4);
		inventoryField.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				fieldClicked(cellPosition);
			}
		});
		return inventoryField;
	}

	private void fieldClicked(int cellPosition)
	{
		linkedInterface.itemQuickAccessButtonClicked(quickAccessButtons.get(cellPosition), cellPosition);
	}

	public void useButtonItem(int cellPosition, Monster target, PacketsSender packetSender)
	{
		ButtonField<ItemQuickAccessIcon> fieldWithIcon = quickAccessButtons.get(cellPosition);
		ItemQuickAccessIcon quickAccessIcon = fieldWithIcon.getContent();
		String itemIdentifier = quickAccessIcon.getItemIdenfier();
		Item item = linkedInterface.searchForItem(itemIdentifier);
		if (item == null)
			return;
		if (!(item instanceof ItemUseable))
			throw new CannotUseThisItemException((Item) item);

		((ItemUseable) item).use(target, packetSender);

	}

	public boolean hasItem(ItemQuickAccessIcon item)
	{
		for (ButtonField<ItemQuickAccessIcon> field : quickAccessButtons.values())
			if (field.hasContent() && field.getContent() == item)
				return true;
		return false;
	}

	public void removeItem(ItemQuickAccessIcon usedItem)
	{
		for (ButtonField<ItemQuickAccessIcon> field : quickAccessButtons.values())
			if (field.hasContent() && field.getContent() == usedItem)
			{
				field.removeContent();
				return;
			}
		throw new NoSuchItemInQuickAccessBarException(usedItem);
	}

	public void decreaseNumberOfItems(String identifier)
	{
		getValidIcons(identifier).forEach(ItemQuickAccessIcon::decreaseItemNumber);
	}

	private Stream<ItemQuickAccessIcon> getValidIcons(String identifier)
	{
		return quickAccessButtons.values().stream().map(ButtonField::getContent).filter(Objects::nonNull)
				.filter(icon -> icon.getItemIdenfier().equals(identifier));
	}

	public void increaseNumbers(String identifier, int itemCount)
	{
		getValidIcons(identifier).forEach(icon -> icon.increaseItemNumber(itemCount));
	}

	public void putItem(String itemIdentifier, int cellPosition, ItemCounter itemCounter)
	{
		ItemQuickAccessIcon icon = new ItemQuickAccessIcon(itemIdentifier, itemCounter);
		ButtonField<ItemQuickAccessIcon> quickAccessField = quickAccessButtons.get(cellPosition);
		quickAccessField.put(icon);
	}

}
