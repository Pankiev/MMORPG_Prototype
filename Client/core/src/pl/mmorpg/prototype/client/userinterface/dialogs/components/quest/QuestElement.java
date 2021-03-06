package pl.mmorpg.prototype.client.userinterface.dialogs.components.quest;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import pl.mmorpg.prototype.client.quests.Quest;
import pl.mmorpg.prototype.client.states.helpers.Settings;
import pl.mmorpg.prototype.client.userinterface.dialogs.components.LineBreaker;

public class QuestElement extends Table
{
    private final static int DESCRIPTION_LINE_LENGTH = 30;
    private final QuestProgress questProgress;
    private final Label questNameLabel;
    private final LineBreaker description;

    public QuestElement(Quest quest)
    {
        questNameLabel = new Label(quest.getQuestName(), Settings.DEFAULT_SKIN);
        this.add(questNameLabel);
        this.row();
        description = new LineBreaker(quest.getDescription(), DESCRIPTION_LINE_LENGTH);
        this.add(description);
        this.row();
        questProgress = new QuestProgress(quest.getProgress());
        this.add(questProgress);
        this.row();
        this.pack();
    }

    public void updateData(Quest quest)
    {
        questNameLabel.setText(quest.getQuestName());
        description.setText(quest.getDescription());
        questProgress.updateData(quest.getProgress());
        this.pack();
    }
}
