package pl.mmorpg.prototype.client.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.esotericsoftware.kryonet.Client;

import pl.mmorpg.prototype.client.states.dialogs.RegisterationDialog;
import pl.mmorpg.prototype.clientservercommon.packets.RegisterationPacket;
import pl.mmorpg.prototype.clientservercommon.packets.RegisterationReplyPacket;

public class RegisterationState implements State
{
	private Client client;
	private StateManager states;
	private Stage stage = new Stage();
	private RegisterationDialog dialog = new RegisterationDialog(this);

	public RegisterationState(Client client, StateManager states)
	{
		this.client = client;
		this.states = states;
		dialog.show(stage);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(SpriteBatch batch)
	{
		stage.draw();
	}

	@Override
	public void update(float deltaTime)
	{
		stage.act();
	}

	public void removeItself()
	{
		states.pop();
	}

	public void registerationDialogSubmited(RegisterationPacket registerationData)
	{
		client.sendTCP(registerationData);
	}

	public void registerationReplyReceived(RegisterationReplyPacket replyPacket)
	{
		if (replyPacket.isRegistered)
			states.pop();
		else
		{
			dialog.setErrorMessage(replyPacket.errorMessage);
			dialog.show(stage);
		}
	}

	@Override
	public void reactivate()
	{
	}
}