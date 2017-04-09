package pl.mmorpg.prototype.client.communication;

import pl.mmorpg.prototype.clientservercommon.packets.CharacterMonsterTargetingPacket;
import pl.mmorpg.prototype.clientservercommon.packets.ChatMessagePacket;
import pl.mmorpg.prototype.clientservercommon.packets.ObjectRemovePacket;
import pl.mmorpg.prototype.clientservercommon.packets.movement.ObjectRepositionPacket;

public class PacketsMaker
{

    public static ObjectRemovePacket makeRemovalPacket(int id)
    {
        return new ObjectRemovePacket(id);
    }

    public static ObjectRepositionPacket makeRepositionPacket(long id, float x, float y)
    {
        ObjectRepositionPacket packet = new ObjectRepositionPacket();
        packet.id = id;
        packet.x = x;
        packet.y = y;
        return packet;
    }

	public static ChatMessagePacket makeChatMessagePacket(String message)
	{
		ChatMessagePacket packet = new ChatMessagePacket();
		packet.setMessage(message);
		return packet;
	}

	public static CharacterMonsterTargetingPacket makeTargetingPacket(float x, float y)
	{
		CharacterMonsterTargetingPacket packet = new CharacterMonsterTargetingPacket();
		packet.gameX = (int)x;
		packet.gameY = (int)y;
		return packet;
	}
}
