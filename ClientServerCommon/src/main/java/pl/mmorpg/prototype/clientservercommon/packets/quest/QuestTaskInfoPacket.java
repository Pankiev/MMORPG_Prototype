package pl.mmorpg.prototype.clientservercommon.packets.quest;

import lombok.Data;
import pl.mmorpg.prototype.clientservercommon.registering.Registerable;

@Registerable
@Data
public class QuestTaskInfoPacket
{
    private String description;
    
    private float percentFinished;

    private QuestTaskInfoPacket[] nextTasks;
}
