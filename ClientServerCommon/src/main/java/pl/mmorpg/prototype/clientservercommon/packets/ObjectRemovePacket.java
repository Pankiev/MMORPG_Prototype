package pl.mmorpg.prototype.clientservercommon.packets;

import pl.mmorpg.prototype.clientservercommon.registering.Registerable;

@Registerable
public class ObjectRemovePacket
{
	public long id;

	public ObjectRemovePacket()
	{
	}
	
	public ObjectRemovePacket(long id)
	{
		this.id = id;
	}
}
