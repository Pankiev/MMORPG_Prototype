package pl.mmorpg.prototype.server.quests;

import com.fasterxml.jackson.annotation.JsonProperty;

import pl.mmorpg.prototype.server.quests.events.AcceptQuestEvent;

public class AcceptQuestTask extends QuestTaskBase<AcceptQuestEvent>
{
    private boolean finished = false;
    @JsonProperty
    private final String questName;

    private AcceptQuestTask()
    {
        super(AcceptQuestEvent.class);
        questName = null;
    }
    
    public AcceptQuestTask(String questName)
    {
        super(AcceptQuestEvent.class);
        this.questName = questName;
    }
    
    @Override
    public boolean isApplicable(AcceptQuestEvent event)
    {
        return event.getQuestName().equalsIgnoreCase(questName);
    }

    @Override
    public void apply(AcceptQuestEvent event)
    {
        finished = true;
    }
    
    @Override
    public boolean isFinished()
    {
        return finished;
    }

}