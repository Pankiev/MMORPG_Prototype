package pl.mmorpg.prototype.server.packetshandling;

import java.util.Map;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import pl.mmorpg.prototype.clientservercommon.packets.AuthenticationPacket;
import pl.mmorpg.prototype.clientservercommon.packets.AuthenticationReplyPacket;
import pl.mmorpg.prototype.server.UserInfo;
import pl.mmorpg.prototype.data.entities.User;
import pl.mmorpg.prototype.data.entities.UserRole;
import pl.mmorpg.prototype.server.helpers.Authenticator;
import pl.mmorpg.prototype.server.states.PlayState;

public class AuthenticationPacketHandler extends PacketHandlerBase<AuthenticationPacket>
{
	private final Map<Integer, UserInfo> loggedUsersKeyUserId;
	private final Map<Integer, User> authenticatedClientsKeyClientId;
	private final Server server;
	private PlayState playState;

	public AuthenticationPacketHandler(Map<Integer, UserInfo> loggedUsersKeyUserId,
			Map<Integer, User> authenticatedClientsKeyClientId,
			Server server, PlayState playState)
	{
		this.loggedUsersKeyUserId = loggedUsersKeyUserId;
		this.authenticatedClientsKeyClientId = authenticatedClientsKeyClientId;
		this.server = server;
		this.playState = playState;
	}

	@Override
	public void handle(Connection connection, AuthenticationPacket packet)
	{
		Authenticator authenticator = new Authenticator(loggedUsersKeyUserId, packet);
		try
		{
			authenticator.tryAuthenticating();		
		}
		catch(ExceptionInInitializerError e)
		{
			Log.error("Database connection problem");
			AuthenticationReplyPacket replyPacket = new AuthenticationReplyPacket();
			replyPacket.message = "Database connection problem";
			server.sendToTCP(connection.getID(), replyPacket);
		}
		
		if (authenticator.isAuthenticated())
		{
			User user = authenticator.getUser();
			UserInfo userInfo = new UserInfo();
			userInfo.user = user;
			loggedUsersKeyUserId.put(user.getId(), userInfo);
			authenticatedClientsKeyClientId.put(connection.getID(), user);
			if(user.getRole().equals(UserRole.ADMIN))
				playState.addGameCommandsHandler(userInfo);
		}
		AuthenticationReplyPacket replyPacket = authenticator.getReplyPacket();
		server.sendToTCP(connection.getID(), replyPacket);
	}
}
