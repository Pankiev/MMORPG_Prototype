package pl.mmorpg.prototype.client.states;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.esotericsoftware.kryonet.Client;

import pl.mmorpg.prototype.client.communication.PacketsMaker;
import pl.mmorpg.prototype.client.input.InputMultiplexer;
import pl.mmorpg.prototype.client.input.InputProcessorAdapter;
import pl.mmorpg.prototype.client.input.NullInputHandler;
import pl.mmorpg.prototype.client.input.PlayInputContinuousHandler;
import pl.mmorpg.prototype.client.input.PlayInputSingleHandle;
import pl.mmorpg.prototype.client.items.Item;
import pl.mmorpg.prototype.client.items.ItemFactory;
import pl.mmorpg.prototype.client.objects.GameObject;
import pl.mmorpg.prototype.client.objects.NullPlayer;
import pl.mmorpg.prototype.client.objects.Player;
import pl.mmorpg.prototype.client.objects.graphic.BloodAnimation;
import pl.mmorpg.prototype.client.objects.graphic.ExperienceGainLabel;
import pl.mmorpg.prototype.client.objects.graphic.GameWorldLabel;
import pl.mmorpg.prototype.client.objects.graphic.GraphicGameObject;
import pl.mmorpg.prototype.client.objects.graphic.MonsterDamageLabel;
import pl.mmorpg.prototype.client.objects.monsters.Monster;
import pl.mmorpg.prototype.client.resources.Assets;
import pl.mmorpg.prototype.client.states.helpers.GameObjectsContainer;
import pl.mmorpg.prototype.client.userinterface.UserInterface;
import pl.mmorpg.prototype.clientservercommon.Settings;
import pl.mmorpg.prototype.clientservercommon.packets.CharacterChangePacket;
import pl.mmorpg.prototype.clientservercommon.packets.ChatMessagePacket;
import pl.mmorpg.prototype.clientservercommon.packets.ChatMessageReplyPacket;
import pl.mmorpg.prototype.clientservercommon.packets.DisconnectPacket;
import pl.mmorpg.prototype.clientservercommon.packets.LogoutPacket;
import pl.mmorpg.prototype.clientservercommon.packets.entities.CharacterItemDataPacket;
import pl.mmorpg.prototype.clientservercommon.packets.entities.UserCharacterDataPacket;
import pl.mmorpg.prototype.clientservercommon.packets.playeractions.ExperienceGainPacket;
import pl.mmorpg.prototype.clientservercommon.packets.playeractions.MonsterDamagePacket;
import pl.mmorpg.prototype.clientservercommon.packets.playeractions.MonsterTargetingPacket;

public class PlayState implements State, GameObjectsContainer
{
	private Client client;
	private StateManager states;
	private Player player;
	private Map<Long, GameObject> gameObjects = new ConcurrentHashMap<>();
	private Collection<GraphicGameObject> clientGraphics = new ConcurrentLinkedDeque<>();
	private InputProcessorAdapter inputHandler;
	private InputMultiplexer inputMultiplexer;
	private UserInterface userInterface;
	private TiledMapRenderer mapRenderer;
	private boolean isInitalized = false;

	private OrthographicCamera camera = new OrthographicCamera(900, 500);

	public PlayState(StateManager states, Client client)
	{
		this.client = client;
		inputHandler = new NullInputHandler();
		player = new NullPlayer();
		this.states = states;
		inputMultiplexer = new InputMultiplexer();
		camera.setToOrtho(false);

		TiledMap map = Assets.get("Map/tiled.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map, Assets.getBatch());
		mapRenderer.setView(camera);

	}

	public void initialize(UserCharacterDataPacket character)
	{
		player = new Player(character.getId());
		player.initialize(character);
		gameObjects.put((long) character.getId(), player);
		inputHandler = new PlayInputContinuousHandler(client, player);
		userInterface = new UserInterface(this, character);
		inputMultiplexer.addProcessor(new PlayInputSingleHandle(userInterface.getDialogs(), player));
		inputMultiplexer.addProcessor(userInterface.getStage());
		inputMultiplexer.addProcessor(inputHandler);
		isInitalized = true;
	}

	public boolean isInitialized()
	{
		return isInitalized;
	}

	@Override
	public void render(SpriteBatch batch)
	{
		batch.setProjectionMatrix(camera.combined);
		mapRenderer.render(new int[] { 0 });
		batch.begin();
		for (GameObject object : gameObjects.values())
			object.render(batch);
		for (GraphicGameObject object : clientGraphics)
			object.render(batch);

		batch.end();
		mapRenderer.render(new int[] { 1, 2, 3, 4 });
		userInterface.draw(batch);
	}

	@Override
	public void update(float deltaTime)
	{
		for (GameObject object : gameObjects.values())
			object.update(deltaTime);
		for (GraphicGameObject object : clientGraphics)
			object.update(deltaTime);
		clientGraphics = clientGraphics.stream().filter(object -> object.isAlive()).collect(Collectors.toList());

		camera.viewportWidth = 900;
		camera.viewportHeight = 500;
		camera.position.set(player.getX() - player.getWidth() / 2, player.getY() - player.getHeight() / 2, 0);
		camera.update();
		inputHandler.process();
		userInterface.update();
	}

	@Override
	public void add(GameObject object)
	{
		gameObjects.put(object.getId(), object);
	}

	@Override
	public void removeObject(long id)
	{
		GameObject object = gameObjects.remove(id);
		if (object == player)
			playerHasDied();
		else if (player.hasLockedOnTarget(object))
			player.releaseTarget();
	}

	private void playerHasDied()
	{
		this.userWantsToChangeCharacter();

	}

	@Override
	public Map<Long, GameObject> getGameObjects()
	{
		return gameObjects;
	}

	@Override
	public GameObject getObject(long id)
	{
		return gameObjects.get(id);
	}

	@Override
	public void reactivate()
	{
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	public void userWantsToDisconnect()
	{
		client.sendTCP(new DisconnectPacket());
		reset();
		states.push(new SettingsChoosingState(client, states));

	}

	private void reset()
	{
		isInitalized = false;
		inputHandler = new NullInputHandler();
		gameObjects.clear();
		userInterface.clear();
		inputMultiplexer.clear();
	}

	public void userWantsToChangeCharacter()
	{
		client.sendTCP(new CharacterChangePacket());
		states.push(new ChoosingCharacterState(client, states));
		reset();
	}

	public void userWantsToLogOut()
	{
		client.sendTCP(new LogoutPacket());
		states.push(new AuthenticationState(client, states));
		reset();
	}

	public void newItemPacketReceived(CharacterItemDataPacket itemData)
	{
		Item newItem = ItemFactory.produceItem(itemData);
		userInterface.addItemToInventory(newItem);
	}

	public void userWantsToSendMessage(String message)
	{
		ChatMessagePacket packet = PacketsMaker.makeChatMessagePacket(message);
		client.sendTCP(packet);
	}

	public void chatMessagePacketReceived(ChatMessageReplyPacket packet)
	{
		userInterface.addMessageToDialogChat(packet);
		GameObject source = gameObjects.get(packet.sourceCharacterId);
		GameWorldLabel message = new GameWorldLabel(packet.getMessage(), source);
		clientGraphics.add(message);
	}

	public void userClickedOnGameBoard(float x, float y)
	{
		float gameBoardX = player.getX() - camera.viewportWidth / 2 + x * camera.viewportWidth / Settings.GAME_WIDTH;
		float gameBoardY = player.getY() - camera.viewportHeight / 2 + y * camera.viewportHeight / Settings.GAME_HEIGHT;
		MonsterTargetingPacket packet = PacketsMaker.makeTargetingPacket(gameBoardX, gameBoardY);
		client.sendTCP(packet);
	}

	public void monsterTargeted(Long monsterId)
	{
		GameObject target = gameObjects.get(monsterId);
		player.lockOnTarget(target);
		System.out.println("Monster targeted " + gameObjects.get(monsterId));
	}

	public boolean has(long targetId)
	{
		return gameObjects.get(targetId) != null;
	}

	public void monsterDamagePacketReceived(MonsterDamagePacket packet)
	{
		Monster attackTarget = (Monster) gameObjects.get(packet.getTargetId());
		attackTarget.gotHitBy(packet.getDamage());
		GraphicGameObject damageNumber = new MonsterDamageLabel(packet.getDamage(), attackTarget);
		GraphicGameObject bloodAnimation = new BloodAnimation(attackTarget);
		clientGraphics.add(damageNumber);
		clientGraphics.add(bloodAnimation);

		if (attackTarget == player)
			userInterface.updateHitPointManaPointDialog();
	}

	public void experienceGainPacketReceived(ExperienceGainPacket packet)
	{
		Player target = (Player) gameObjects.get(packet.getTargetId());
		GraphicGameObject experienceGainLabel = new ExperienceGainLabel(String.valueOf(packet.getExperience()), target);
		clientGraphics.add(experienceGainLabel);
		if (target == player)
		{
			target.addExperience(packet.getExperience());
			userInterface.updateStatsDialog();
		}
	}

}
