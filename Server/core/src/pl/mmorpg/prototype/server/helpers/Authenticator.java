package pl.mmorpg.prototype.server.helpers;

import java.util.Map;
import java.util.Optional;

import pl.mmorpg.prototype.SpringContext;
import pl.mmorpg.prototype.clientservercommon.packets.AuthenticationPacket;
import pl.mmorpg.prototype.clientservercommon.packets.AuthenticationReplyPacket;
import pl.mmorpg.prototype.server.UserInfo;
import pl.mmorpg.prototype.data.entities.User;
import pl.mmorpg.prototype.data.entities.repositories.UserRepository;
import pl.mmorpg.prototype.server.exceptions.NotAuthenticatedException;

public class Authenticator
{
    private Map<Integer, UserInfo> authenticatedUsers;
    private AuthenticationPacket packet;

    private AuthenticationReplyPacket replyPacket;
    private User authenticatedUser;

    public Authenticator(Map<Integer, UserInfo> authenticatedUsers, AuthenticationPacket packet)
    {
        this.authenticatedUsers = authenticatedUsers;
        this.packet = packet;
    }

    public AuthenticationReplyPacket tryAuthenticating()
    {
        replyPacket = new AuthenticationReplyPacket();

        UserRepository userRepo = SpringContext.getBean(UserRepository.class);
        Optional<User> user = userRepo.findByUsername(packet.username);
        if(!user.isPresent())
        {
            replyPacket.message = "Wrong username";
            return replyPacket;
        }

        if (user.get().getPassword().compareTo(packet.password) == 0)
        {
            if (authenticatedUsers.containsKey(user.get().getId()))
                replyPacket.message = "User is already logged in!";
            else
            {
                replyPacket.userId = user.get().getId();
                replyPacket.isAuthenticated = true;
                authenticatedUser = user.get();
            }
        } else
            replyPacket.message = "Wrong password";
        return replyPacket;
    }

    public boolean isAuthenticated()
    {
        return replyPacket.isAuthenticated;
    }

    public User getUser()
    {
        if (!isAuthenticated())
            throw new NotAuthenticatedException();
        return authenticatedUser;
    }

    public AuthenticationReplyPacket getReplyPacket()
    {
        return replyPacket;
    }
}
