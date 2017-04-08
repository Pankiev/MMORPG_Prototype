package pl.mmorpg.prototype.client.userinterface.dialogs;

import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import pl.mmorpg.prototype.client.exceptions.CannotUseThisItemException;
import pl.mmorpg.prototype.client.items.Item;
import pl.mmorpg.prototype.client.items.ItemUseable;
import pl.mmorpg.prototype.client.objects.GameCharacter;
import pl.mmorpg.prototype.client.states.helpers.Settings;
import pl.mmorpg.prototype.client.userinterface.UserInterface;
import pl.mmorpg.prototype.client.userinterface.dialogs.components.InventoryField;
import pl.mmorpg.prototype.client.userinterface.dialogs.components.InventoryTextField;

public class QuickAccessDialog extends Dialog
{
	private Map<Integer, InventoryField> quickAccessButtons = new TreeMap<>();
	private UserInterface linkedState;

	public QuickAccessDialog(UserInterface linkedInterface)
	{
		super("Quick access", Settings.DEFAULT_SKIN);
		this.linkedState = linkedInterface;

		HorizontalGroup buttons = new HorizontalGroup().padBottom(8).space(4).padTop(0).fill();
		for (int i = 0; i < 12; i++)
		{
			InventoryField button = createField(i);
			quickAccessButtons.put(i, button);
			buttons.addActor(button);
		}
		add(buttons);
		row();
		pack();
		this.setHeight(80);
		this.setX(400);
		this.setMovable(false);
	}

	private InventoryField createField(int cellPosition)
	{
		InventoryTextField inventoryField = new InventoryTextField("F" + String.valueOf(cellPosition + 1));
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
		linkedState.quickAccesButtonClicked(quickAccessButtons.get(cellPosition));
	}

	public void useButtonItem(int cellPosition, GameCharacter character)
	{
		Item item = quickAccessButtons.get(cellPosition).getItem();
		if (item == null)
			return;
		if (!(item instanceof ItemUseable))
			throw new CannotUseThisItemException(item);

		((ItemUseable) item).use(character);
	}
}
