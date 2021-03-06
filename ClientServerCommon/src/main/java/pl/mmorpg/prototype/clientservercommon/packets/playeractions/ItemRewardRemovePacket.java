package pl.mmorpg.prototype.clientservercommon.packets.playeractions;

import lombok.Data;
import pl.mmorpg.prototype.clientservercommon.registering.Registerable;

@Registerable
@Data
public class ItemRewardRemovePacket
{
    private String itemIdentifier;
    private int numberOfItems;
}
