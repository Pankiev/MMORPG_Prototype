package pl.mmorpg.prototype.client.states;

import java.io.IOException;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryonet.Client;

import pl.mmorpg.prototype.client.packethandlers.PacketHandlerRegisterer;

public class ConnectionState implements State
{
	private final StateManager states;
	private final Client client;
	private final int maxTryouts = 3;
	private int currentTryout = 0;

	private Thread connectThread;
	private PacketHandlerRegisterer registerer;

	public ConnectionState(Client client, StateManager states, PacketHandlerRegisterer registerer,
			ConnectionInfo connectionInfo)
	{
		this.client = client;
		this.states = states;
		this.registerer = registerer;
		client.start();
		connectThread = new Thread(() -> tryConnecting(client, connectionInfo));
		connectThread.start();
	}

	private void tryConnecting(Client client, ConnectionInfo connectionInfo)
	{
		try
		{
			client.connect(3000, connectionInfo.getIp(), connectionInfo.getTcpPort(), connectionInfo.getUdpPort());
		} catch (IOException e)
		{
			currentTryout++;
			if (currentTryout < maxTryouts)
				tryConnecting(client, connectionInfo);
		}

	}

	@Override
	public void render(SpriteBatch batch)
	{
	}

	@Override
	public void update(float deltaTime)
	{
		if (client.isConnected())
			states.set(new AuthenticationState(client, states, registerer));
		else if (!connectThread.isAlive())
			states.set(new SettingsChoosingState(client, states, registerer));
	}

	@Override
	public void reactivate()
	{
	}

}
