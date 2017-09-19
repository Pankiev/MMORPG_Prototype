package pl.mmorpg.prototype.server.quests;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

import pl.mmorpg.prototype.server.database.entities.QuestTaskWrapper;
import pl.mmorpg.prototype.server.database.entities.jointables.CharactersQuests;
import pl.mmorpg.prototype.server.quests.events.Event;
import pl.mmorpg.prototype.server.quests.observers.QuestFinishedObserver;

public abstract class QuestTaskBase<T extends Event> implements QuestTask
{
    private transient CharactersQuests sourceTask;
    
    @JsonProperty
    private Collection<QuestTask> nextTasks = new ArrayList<>(1);
    
    private Class<T> desiredEventType;

    public QuestTaskBase(Class<T> desiredEventType)
    {
        this.desiredEventType = desiredEventType;
    }
    
    @Override
    public void addNextTask(QuestTask task)
    {
        nextTasks.add(task);        
    }
    
    @Override
    public boolean isLastTaskInQuest()
    {
        return nextTasks.isEmpty();
    }
    
    @Override
    public void proceedToNextTasks()
    {
        Collection<QuestTaskWrapper> dbQuestTasks = new ArrayList<>();
        for(QuestTask task : nextTasks)
        {
            QuestTaskWrapper wrapper = new QuestTaskWrapper();
            wrapper.setCharactersQuests(sourceTask);
            wrapper.setQuestTask(task);
            dbQuestTasks.add(wrapper);
        }
     
        sourceTask.setQuestTasks(dbQuestTasks);
    }
    
    @Override
    public Collection<QuestTask> getNextTasks()
    {
        return nextTasks;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean shouldProcess(Event e)
    {
        return e.getClass().equals(desiredEventType) && isApplicable((T)e);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void process(Event e)
    {
        apply((T)e);
    }
    
    public abstract boolean isApplicable(T event);
    
    public abstract void apply(T event);

    @Override
    public void setSourceTask(CharactersQuests sourceTask)
    {
        this.sourceTask = sourceTask;
    }

    @Override
    public void questFinished(QuestFinishedObserver observer)
    {
        observer.playerFinishedQuest(sourceTask.getCharacter().getId(), sourceTask.getQuest());
    }
    
}