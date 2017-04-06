package pl.mmorpg.prototype.server;

import java.io.IOException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;

import pl.mmorpg.prototype.clientservercommon.Settings;
import pl.mmorpg.prototype.clientservercommon.registering.PacketsRegisterer;
import pl.mmorpg.prototype.server.exceptions.CannotBindServerException;
import pl.mmorpg.prototype.server.resources.Assets;
import pl.mmorpg.prototype.server.states.PlayState;
import pl.mmorpg.prototype.server.states.StateManager;

public class GameServer extends ApplicationAdapter
{
	private SpriteBatch batch;
	private StateManager states;
	private Server server;
	private PlayState playState;

	@Override
	public void create()
	{
		Assets.loadAssets();
		states = new StateManager();
		batch = Assets.getBatch();
		server = initializeServer();
		playState = new PlayState(server, states);
		server.addListener(new ServerListener(server, playState));
		bindServer();
		states.push(playState);
	}

	private Server initializeServer()
	{
		server = new Server();
		Kryo serverKryo = server.getKryo();
		serverKryo = PacketsRegisterer.registerAllAnnotated(serverKryo);
		return server;
	}

	private void bindServer()
	{
		server.start();
		try
		{
			server.bind(Settings.TCP_PORT, Settings.UDP_PORT);
		} catch (IOException e)
		{
			throw new CannotBindServerException(e.getMessage());
		}
	}

	@Override
	public void render()
	{
		update();
		clearScreen();
		batch.begin();
		states.render(batch);
		batch.end();
	}

	private void update()
	{
		states.update(Gdx.graphics.getDeltaTime());
	}

	private void clearScreen()
	{
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void dispose()
	{
		Assets.dispose();
		batch.dispose();
	}

}
