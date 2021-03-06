package pl.mmorpg.prototype.client.userinterface.dialogs.components.spell;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import pl.mmorpg.prototype.client.objects.graphic.helpers.HighlightedTextureCreator;
import pl.mmorpg.prototype.client.objects.icons.spells.Spell;
import pl.mmorpg.prototype.client.states.helpers.Settings;
import pl.mmorpg.prototype.client.userinterface.UserInterface;
import pl.mmorpg.prototype.client.userinterface.dialogs.DialogUtils;
import pl.mmorpg.prototype.client.userinterface.dialogs.components.LineBreaker;
import pl.mmorpg.prototype.client.userinterface.dialogs.components.inventory.ButtonField;

public class SpellListElement extends Table
{
	public SpellListElement(Spell spell, UserInterface userInterface)
	{
		addSpellIconField(spell, userInterface);
		addSpellInfo(spell);
	}

	private void addSpellIconField(Spell spell, UserInterface userInterface)
	{
		ButtonField<Spell> spellIconField = createSpellButtonField(spell, userInterface);
		this.add(spellIconField);
	}

	private ButtonField<Spell> createSpellButtonField(Spell spell, UserInterface userInterface)
	{
		ButtonField<Spell> spellButtonField = new ButtonField<>();
		spellButtonField.put(spell);
		spellButtonField.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				userInterface.spellListButtonWithIconClicked(spellButtonField);
			}
		});
		Texture texture = spell.getTexture();
		spellButtonField.addListener(new InputListener()
		{
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
			{
				Texture highlightingTexture = HighlightedTextureCreator.createHighlightedGraphic(new TextureRegion(texture));
				spell.setTexture(highlightingTexture);
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
			{
				spell.setTexture(texture);
			}

		});
		return spellButtonField;
	}

	private void addSpellInfo(Spell spell)
	{
		Table infoContainer = new Table();
		String humanReadableName = DialogUtils.humanReadableFromItemIdentifier(spell.getIdentifier().toString());
		infoContainer.add(new Label(humanReadableName, Settings.DEFAULT_SKIN)).padRight(10);
		infoContainer.add(new LineBreaker(spell.getDescription(), 40));
		infoContainer.padLeft(10);
		this.add(infoContainer);
	}
}
