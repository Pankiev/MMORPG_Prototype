package pl.mmorpg.prototype.server.packetshandling;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.script.ScriptException;

import com.esotericsoftware.kryonet.Connection;

import pl.mmorpg.prototype.clientservercommon.packets.ScriptCodePacket;
import pl.mmorpg.prototype.server.communication.PacketsMaker;
import pl.mmorpg.prototype.server.database.entities.User;
import pl.mmorpg.prototype.server.database.entities.UserRole;
import pl.mmorpg.prototype.server.quests.events.Event;
import pl.mmorpg.prototype.server.states.PlayState;

public class ScriptCodePacketHandler extends PacketHandlerBase<ScriptCodePacket>
{
	private PlayState playState;
	private Map<Integer, User> authenticatedClientsKeyClientId;

	public ScriptCodePacketHandler(PlayState playState, Map<Integer, User> authenticatedClientsKeyClientId)
	{
		this.playState = playState;
		this.authenticatedClientsKeyClientId = authenticatedClientsKeyClientId;
	}

	@Override
	public Collection<Event> handle(Connection connection, ScriptCodePacket packet)
	{
		User user = authenticatedClientsKeyClientId.get(connection.getID());
		if(!user.getRole().equals(UserRole.ADMIN))
		{
			connection.sendTCP(PacketsMaker.makeScriptExecutionErrorPacket("You must be admin to do that"));
	        return Collections.emptyList();
		}
		try
		{
			Object result = playState.executeCode(packet.getCode());
			if(result != null)
				connection.sendTCP(PacketsMaker.makeScriptResultInfoPacket(result.toString()));
			
		}catch(ScriptException e)
		{
			connection.sendTCP(PacketsMaker.makeScriptExecutionErrorPacket(e.getMessage()));
		}
        return Collections.emptyList();
	}

}
