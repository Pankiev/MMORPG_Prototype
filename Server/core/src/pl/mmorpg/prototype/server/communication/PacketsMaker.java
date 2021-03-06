package pl.mmorpg.prototype.server.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.mmorpg.prototype.clientservercommon.EquipmentPosition;
import pl.mmorpg.prototype.clientservercommon.packets.*;
import pl.mmorpg.prototype.clientservercommon.packets.damage.FireDamagePacket;
import pl.mmorpg.prototype.clientservercommon.packets.damage.NormalDamagePacket;
import pl.mmorpg.prototype.clientservercommon.packets.entities.CharacterItemDataPacket;
import pl.mmorpg.prototype.clientservercommon.packets.entities.InventoryPositionPacket;
import pl.mmorpg.prototype.clientservercommon.packets.entities.QuestDataPacket;
import pl.mmorpg.prototype.clientservercommon.packets.entities.UserCharacterDataPacket;
import pl.mmorpg.prototype.clientservercommon.packets.levelup.LevelUpPacket;
import pl.mmorpg.prototype.clientservercommon.packets.levelup.LevelUpPointOnDexteritySpentSuccessfullyPacket;
import pl.mmorpg.prototype.clientservercommon.packets.levelup.LevelUpPointOnIntelligenceSpentSuccessfullyPacket;
import pl.mmorpg.prototype.clientservercommon.packets.levelup.LevelUpPointOnStrengthSpentSuccessfullyPacket;
import pl.mmorpg.prototype.clientservercommon.packets.movement.ObjectRepositionPacket;
import pl.mmorpg.prototype.clientservercommon.packets.playeractions.*;
import pl.mmorpg.prototype.clientservercommon.packets.quest.*;
import pl.mmorpg.prototype.clientservercommon.packets.quest.event.QuestAcceptedEventPacket;
import pl.mmorpg.prototype.clientservercommon.packets.quest.event.MonsterKilledEventPacket;
import pl.mmorpg.prototype.clientservercommon.packets.quest.event.NpcDialogEventPacket;
import pl.mmorpg.prototype.data.entities.Character;
import pl.mmorpg.prototype.data.entities.*;
import pl.mmorpg.prototype.data.entities.components.InventoryPosition;
import pl.mmorpg.prototype.data.entities.jointables.CharactersQuests;
import pl.mmorpg.prototype.server.objects.GameObject;
import pl.mmorpg.prototype.server.objects.PlayerCharacter;
import pl.mmorpg.prototype.server.objects.containers.GameContainer;
import pl.mmorpg.prototype.server.objects.ineractivestaticobjects.QuestBoard;
import pl.mmorpg.prototype.server.objects.items.equipment.EquipableItem;
import pl.mmorpg.prototype.server.objects.items.Item;
import pl.mmorpg.prototype.server.objects.items.StackableItem;
import pl.mmorpg.prototype.server.objects.monsters.Monster;
import pl.mmorpg.prototype.server.objects.monsters.npcs.ShopItemWrapper;
import pl.mmorpg.prototype.server.objects.monsters.spells.objects.ThrowableObject;
import pl.mmorpg.prototype.server.quests.QuestTask;
import pl.mmorpg.prototype.server.quests.events.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PacketsMaker
{
	public static ObjectCreationPacket makeCreationPacket(GameObject object)
	{
		ObjectCreationPacket packet = new ObjectCreationPacket();
		packet.id = object.getId();
		packet.identifier = object.getIdentifier();
		packet.x = object.getX();
		packet.y = object.getY();
		return packet;
	}

	public static ThrowableObjectCreationPacket makeCreationPacket(ThrowableObject throwableObject, Monster target)
	{
		ThrowableObjectCreationPacket packet = new ThrowableObjectCreationPacket();
		packet.id = throwableObject.getId();
		packet.identifier = throwableObject.getIdentifier();
		packet.x = throwableObject.getX();
		packet.y = throwableObject.getY();
		packet.targetData = makeObjectBasicData(target);
		return packet;
	}

	private static ObjectBasicData makeObjectBasicData(GameObject object)
	{
		ObjectBasicData data = new ObjectBasicData();
		data.id = object.getId();
		data.identifier = object.getIdentifier();
		data.x = object.getX();
		data.y = object.getY();
		return data;
	}

	public static MonsterCreationPacket makeCreationPacket(Monster monster)
	{
		MonsterCreationPacket packet = new MonsterCreationPacket();
		packet.id = monster.getId();
		packet.identifier = monster.getIdentifier();
		packet.x = monster.getX();
		packet.y = monster.getY();
		packet.properties = monster.getProperties();
		return packet;
	}

	public static PlayerCreationPacket makeCreationPacket(PlayerCharacter player)
	{
		PlayerCreationPacket packet = new PlayerCreationPacket();
		packet.id = player.getId();
		packet.identifier = player.getIdentifier();
		packet.x = player.getX();
		packet.y = player.getY();
		packet.properties = player.getProperties();
		packet.data = PacketsMaker.makeCharacterPacket(player.getUserCharacterData());
		return packet;
	}

	public static ObjectRemovePacket makeRemovalPacket(long id)
	{
		return new ObjectRemovePacket(id);
	}

	public static ObjectRepositionPacket makeRepositionPacket(GameObject gameObject)
	{
		return makeRepositionPacket(gameObject.getId(), gameObject.getX(), gameObject.getY());
	}

	public static ObjectRepositionPacket makeRepositionPacket(long id, float x, float y)
	{
		ObjectRepositionPacket packet = new ObjectRepositionPacket();
		packet.id = id;
		packet.x = x;
		packet.y = y;
		return packet;
	}

	public static UserCharacterDataPacket makeCharacterPacket(Character character)
	{
		UserCharacterDataPacket packet = new UserCharacterDataPacket();
		packet.setId(character.getId());
		packet.setLevel(character.getLevel());
		packet.setHitPoints(character.getHitPoints());
		packet.setManaPoints(character.getManaPoints());
		packet.setNickname(character.getNickname());
		packet.setExperience(character.getExperience());
		packet.setStrength(character.getStrength());
		packet.setIntelligence(character.getIntelligence());
		packet.setDexterity(character.getDexterity());
		packet.setLevelUpPoints(character.getLevelUpPoints());
		packet.setGold(character.getGold());
		packet.setStartingX(character.getLastLocationX());
		packet.setStartingY(character.getLastLocationY());
		packet.setStartingMap(character.getLastLocationMap());
		return packet;
	}

	public static InventoryPositionPacket makeInventoryPositionPacket(InventoryPosition inventoryPosition)
	{
		if (inventoryPosition == null)
			return null;
		return makeInventoryPositionPacket(inventoryPosition.getInventoryPageNumber(),
				inventoryPosition.getInventoryX(), inventoryPosition.getInventoryY());
	}

	public static InventoryPositionPacket makeInventoryPositionPacket(int inventoryPage, int inventoryX, int inventoryY)
	{
		InventoryPositionPacket packet = new InventoryPositionPacket();
		packet.setInventoryPageNumber(inventoryPage);
		packet.setInventoryX(inventoryX);
		packet.setInventoryY(inventoryY);
		return packet;
	}

	public static CharacterItemDataPacket makeItemPacket(Item item)
	{
		return makeItemPacket(item, item.getInventoryPosition());
	}

	public static CharacterItemDataPacket makeItemPacket(Item item, InventoryPosition inventoryPosition)
	{
		CharacterItemDataPacket packet = new CharacterItemDataPacket();
		packet.setId(item.getId());
		packet.setIdentifier(item.getIdentifier().toString());
		if (item instanceof StackableItem)
			packet.setCount(((StackableItem) item).getCount());
		if (item instanceof EquipableItem)
			packet.setEquipmentPosition(((EquipableItem) item).getEquipmentPosition().toString());
		else
			packet.setEquipmentPosition(EquipmentPosition.NONE.toString());
		packet.setInventoryPosition(makeInventoryPositionPacket(inventoryPosition));
		return packet;
	}

	public static MonsterTargetingReplyPacket makeTargetingReplyPacket(GameObject target)
	{
		if (target == null)
			throw new NullPointerException("Target cannot be null");
		MonsterTargetingReplyPacket packet = new MonsterTargetingReplyPacket();
		packet.monsterId = target.getId();
		return packet;
	}

	public static NormalDamagePacket makeNormalDamagePacket(long id, int damage)
	{
		NormalDamagePacket packet = new NormalDamagePacket();
		packet.setTargetId(id);
		packet.setDamage(damage);
		return packet;
	}

	public static ExperienceGainPacket makeExperienceGainPacket(long id, int experienceGain)
	{
		ExperienceGainPacket packet = new ExperienceGainPacket();
		packet.setTargetId(id);
		packet.setExperience(experienceGain);
		return packet;
	}

	public static LevelUpPacket makeLevelUpPacket(long id, int levelUpPoints)
	{
		LevelUpPacket levelUpPacket = new LevelUpPacket();
		levelUpPacket.setTargetId(id);
		levelUpPacket.setLevelUpPoints(levelUpPoints);
		return levelUpPacket;
	}

	public static LevelUpPointOnStrengthSpentSuccessfullyPacket makeLevelUpPointOnStrengthSpentSuccessfullyPacket()
	{
		return new LevelUpPointOnStrengthSpentSuccessfullyPacket();
	}

	public static LevelUpPointOnIntelligenceSpentSuccessfullyPacket makeLevelUpPointOnIntelligenceSpentSuccessfullyPacket()
	{
		return new LevelUpPointOnIntelligenceSpentSuccessfullyPacket();
	}

	public static LevelUpPointOnDexteritySpentSuccessfullyPacket makeLevelUpPointOnDexteritySpentSuccessfullyPacket()
	{
		return new LevelUpPointOnDexteritySpentSuccessfullyPacket();
	}

	public static HpChangeByItemUsagePacket makeHpNotifiedIncreasePacket(int delta, long targetId)
	{
		HpChangeByItemUsagePacket packet = new HpChangeByItemUsagePacket();
		packet.setHpChange(delta);
		packet.setMonsterTargetId(targetId);
		return packet;
	}

	public static MpChangeByItemUsagePacket makeMpChangeByItemUsagePacket(int delta, long targetId)
	{
		MpChangeByItemUsagePacket packet = new MpChangeByItemUsagePacket();
		packet.setMpChange(delta);
		packet.setMonsterTargetId(targetId);
		return packet;
	}

	public static FireDamagePacket makeFireDamagePacket(long targetId, int spellDamage)
	{
		FireDamagePacket fireDamagePacket = new FireDamagePacket();
		fireDamagePacket.setTargetId(targetId);
		fireDamagePacket.setDamage(spellDamage);
		return fireDamagePacket;
	}

	public static ManaDrainPacket makeManaDrainPacket(int manaDrain)
	{
		ManaDrainPacket packet = new ManaDrainPacket();
		packet.manaDrained = manaDrain;
		return packet;
	}

	public static ContainerContentPacket makeOpenContainerPacket(GameContainer container)
	{
		Collection<Item> items = container.getItems().values();
		CharacterItemDataPacket[] containerContent = createContainerContentPackets(items);
		ContainerContentPacket packet = new ContainerContentPacket();
		packet.setContentItems(containerContent);
		packet.setContainerId(container.getId());
		packet.setGoldAmount(container.getGoldAmount());
		return packet;
	}

	private static CharacterItemDataPacket[] createContainerContentPackets(Collection<Item> items)
	{
		CharacterItemDataPacket[] containerContent = new CharacterItemDataPacket[items.size()];
		Iterator<Item> it = items.iterator();
		for (int i = 0; i < containerContent.length; i++)
		{
			Item item = it.next();
			int inventoryPositionX = i;
			containerContent[i] = makeItemPacketWithStandardPosition(inventoryPositionX, item);
		}
		return containerContent;
	}

	private static CharacterItemDataPacket makeItemPacketWithStandardPosition(int inventoryPositionX, Item item)
	{
		int inventoryPage = 1;
		int inventoryPositionY = 1;
		InventoryPosition inventoryPosition = new InventoryPosition(inventoryPage, inventoryPositionX, inventoryPositionY);
		return makeItemPacket(item, inventoryPosition);
	}

	public static ContainerItemRemovalPacket makeContainerItemRemovalPacket(long containerId, long itemId)
	{
		ContainerItemRemovalPacket packet = new ContainerItemRemovalPacket();
		packet.setContainerId(containerId);
		packet.setItemId(itemId);
		return packet;
	}

	public static UnacceptableOperationPacket makeUnacceptableOperationPacket(String errorMessage)
	{
		UnacceptableOperationPacket packet = new UnacceptableOperationPacket();
		packet.setErrorMessage(errorMessage);
		return packet;
	}

	public static ContainerGoldRemovalPacket makeContainerGoldRemovalPacket(long containerId, int gold)
	{
		ContainerGoldRemovalPacket packet = new ContainerGoldRemovalPacket();
		packet.setContainerId(containerId);
		packet.setGoldAmount(gold);
		return packet;
	}

	public static GoldReceivePacket makeGoldReceivePacket(int gold)
	{
		GoldReceivePacket packet = new GoldReceivePacket();
		packet.setGoldAmount(gold);
		return packet;
	}

	public static HpUpdatePacket makeHpUpdatePacket(long monsterId, int hp)
	{
		HpUpdatePacket packet = new HpUpdatePacket();
		packet.setNewHp(hp);
		packet.setId(monsterId);
		return packet;
	}

	public static MpUpdatePacket makeMpUpdatePacket(long monsterId, int mp)
	{
		MpUpdatePacket packet = new MpUpdatePacket();
		packet.setNewMp(mp);
		packet.setId(monsterId);
		return packet;
	}

	public static ShopItemsPacket makeShopItemsPacket(Collection<ShopItemWrapper> availableItems, long shopId)
	{
		ShopItemPacket[] itemsArray = availableItems.stream().map(PacketsMaker::makeShopItemPacket)
				.toArray(ShopItemPacket[]::new);

		ShopItemsPacket packet = new ShopItemsPacket();
		packet.setShopItems(itemsArray);
		packet.setShopId(shopId);
		return packet;
	}

	public static ShopItemPacket makeShopItemPacket(ShopItemWrapper itemWrapper)
	{
		ShopItemPacket singleItemPacketWrapper = new ShopItemPacket();
		CharacterItemDataPacket itemPacket = makeItemPacketWithStandardPosition(1, itemWrapper.getItem());
		singleItemPacketWrapper.setItem(itemPacket);
		singleItemPacketWrapper.setPrice(itemWrapper.getPrice());
		return singleItemPacketWrapper;
	}

	public static GoldAmountChangePacket makeGoldAmountChangePacket(int newGoldAmount)
	{
		GoldAmountChangePacket packet = new GoldAmountChangePacket();
		packet.setNewGoldAmount(newGoldAmount);
		return packet;
	}

	public static ScriptExecutionErrorPacket makeScriptExecutionErrorPacket(String error)
	{
		ScriptExecutionErrorPacket packet = new ScriptExecutionErrorPacket();
		packet.setError(error);
		return packet;
	}

	public static InventoryItemRepositionPacket makeInventoryItemRepositionPacket(InventoryPosition sourcePosition,
			InventoryPosition destinationPosition)
	{
		InventoryItemRepositionPacket packet = new InventoryItemRepositionPacket();

		packet.setSourcePageNumber(sourcePosition.getInventoryPageNumber());
		packet.setSourcePageX(sourcePosition.getInventoryX());
		packet.setSourcePageY(sourcePosition.getInventoryY());

		packet.setDestinationPageNumber(destinationPosition.getInventoryPageNumber());
		packet.setDestinationPageX(destinationPosition.getInventoryX());
		packet.setDestinationPageY(destinationPosition.getInventoryY());

		return packet;
	}

	public static InventoryItemSwapPacket makeInventoryItemSwapPacket(InventoryPosition firstPosition,
			InventoryPosition secondPosition)
	{
		InventoryItemSwapPacket packet = new InventoryItemSwapPacket();

		packet.setFirstPositionPageNumber(firstPosition.getInventoryPageNumber());
		packet.setFirstPositionPageX(firstPosition.getInventoryX());
		packet.setFirstPositionPageY(firstPosition.getInventoryY());

		packet.setSecondPositionPageNumber(secondPosition.getInventoryPageNumber());
		packet.setSecondPositionPageX(secondPosition.getInventoryX());
		packet.setSecondPositionPageY(secondPosition.getInventoryY());

		return packet;
	}

	public static ItemPutInQuickAccessBarPacket makeItemQuickAccessBarConfigElementPacket(
			ItemQuickAccessBarConfigurationElement element)
	{
		ItemPutInQuickAccessBarPacket packet = new ItemPutInQuickAccessBarPacket();
		packet.setCellPosition(element.getFieldPosition());
		packet.setItemIdentifier(element.getItemIdentifier().toString());
		return packet;
	}

	public static SpellPutInQuickAccessBarPacket makeSpellQuickAccessBarConfigElementPacket(
			SpellQuickAccessBarConfigurationElement element)
	{
		SpellPutInQuickAccessBarPacket packet = new SpellPutInQuickAccessBarPacket();
		packet.setCellPosition(element.getFieldPosition());
		packet.setSpellIdentifier(element.getSpell().getIdentifier());
		return packet;
	}

	public static ScriptResultInfoPacket makeScriptResultInfoPacket(String message)
	{
		ScriptResultInfoPacket packet = new ScriptResultInfoPacket();
		packet.setMessage(message);
		return packet;
	}

	public static QuestBoardInfoPacket makeQuestBoardInfoPacket(QuestBoard questBoard)
	{
		Predicate<Quest> shouldIncludeQuest = q -> true;
		return makeQuestBoardInfoPacket(questBoard, shouldIncludeQuest);
	}

	public static QuestBoardInfoPacket makeQuestBoardInfoPacket(QuestBoard questBoard,
			Predicate<Quest> shouldBeIncluded)
	{
		QuestDataPacket[] quests = questBoard.getQuests().stream()
				.filter(shouldBeIncluded)
				.map(PacketsMaker::makeQuestDataPacket)
				.toArray(QuestDataPacket[]::new);

		return makeQuestBoardInfoPacket(quests, questBoard.getId());
	}

	public static QuestDataPacket makeQuestDataPacket(Quest quest)
	{
		QuestDataPacket packet = new QuestDataPacket();
		packet.setDescription(quest.getDescription());
		packet.setName(quest.getName());
		return packet;
	}

	private static QuestBoardInfoPacket makeQuestBoardInfoPacket(QuestDataPacket[] quests, long questBoardId)
	{
		QuestBoardInfoPacket packet = new QuestBoardInfoPacket();
		packet.setQuests(quests);
		packet.setQuestBoardId(questBoardId);
		return packet;
	}

	public static QuestFinishedRewardPacket makeQuestFinishedRewardPacket(Quest quest)
	{
		QuestFinishedRewardPacket packet = new QuestFinishedRewardPacket();
		packet.setGoldReward(quest.getGoldReward());
		packet.setQuestName(quest.getName());
		ItemRewardPacket[] itemReward = quest.getItemsReward().stream()
				.map(PacketsMaker::makeItemRewardPacket)
				.toArray(ItemRewardPacket[]::new);
		packet.setItemReward(itemReward);
		return packet;
	}

	public static ItemRewardPacket makeItemRewardPacket(ItemReward itemReward)
	{
		ItemRewardPacket packet = new ItemRewardPacket();
		packet.setItemIdentifier(itemReward.getItemIdentifier().toString());
		packet.setNumberOfItems(itemReward.getNumberOfItems());
		return packet;
	}

	public static ItemRewardRemovePacket makeItemRewardRemovePacket(String itemIdentifier, int numberOfItems)
	{
		ItemRewardRemovePacket packet = new ItemRewardRemovePacket();
		packet.setItemIdentifier(itemIdentifier);
		packet.setNumberOfItems(numberOfItems);
		return packet;
	}

	public static QuestRewardGoldRemovalPacket makeQuestRewardGoldRemovalPacket(int howMuchGold)
	{
		QuestRewardGoldRemovalPacket packet = new QuestRewardGoldRemovalPacket();
		packet.setGoldAmount(howMuchGold);
		return packet;
	}

	public static QuestStateInfoPacket[] makeQuestStateInfoPackets(Collection<CharactersQuests> quests)
	{
		return quests.stream()
				.map(PacketsMaker::makeQuestStateInfoPacket)
				.toArray(QuestStateInfoPacket[]::new);
	}

	public static QuestStateInfoPacket makeQuestStateInfoPacket(CharactersQuests characterQuest)
	{
		QuestStateInfoPacket packet = new QuestStateInfoPacket();
		Quest quest = characterQuest.getQuest();
		packet.setDescription(quest.getDescription());
		packet.setQuestName(quest.getName());
		QuestTask rootTask = combineQuestDefinitionWithActualQuestState(characterQuest);
		packet.setRootTask(makeQuestTaskInfoPacket(rootTask));
		packet.setFinishedQuestTasksPath(characterQuest.getFinishedQuestTasksPath());
		return packet;
	}

	private static QuestTask combineQuestDefinitionWithActualQuestState(CharactersQuests characterQuest)
	{
		QuestTask rootTask = deepCopy((QuestTask) characterQuest.getQuest().getQuestTask());
		int[] finishedQuestTasksPathIndexes = Stream.of(characterQuest.getFinishedQuestTasksPath().split(","))
				.mapToInt(Integer::valueOf)
				.toArray();
		QuestTask parentOfCurrentlyActiveQuestTasks = getParentOfCurrentlyActiveQuestTasks(rootTask,
				finishedQuestTasksPathIndexes);
		parentOfCurrentlyActiveQuestTasks.getNextTasks().clear();
		List<QuestTask> currentlyActiveQuestTasks = characterQuest.getQuestTasks().stream()
				.map(QuestTaskWrapper::getQuestTask)
				.map(task -> (QuestTask) task)
				.collect(Collectors.toList());
		parentOfCurrentlyActiveQuestTasks.getNextTasks().addAll(currentlyActiveQuestTasks);
		return rootTask;
	}

	private static QuestTask getParentOfCurrentlyActiveQuestTasks(QuestTask rootTask,
			int[] finishedQuestTasksPathIndexes)
	{
		QuestTask currentTask = rootTask;
		for (int i = 0; i < finishedQuestTasksPathIndexes.length - 1; i++)
			currentTask = currentTask.getNextTasks().get(finishedQuestTasksPathIndexes[i]);
		return currentTask;
	}

	private static QuestTask deepCopy(QuestTask questTask)
	{
		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			byte[] bytes = objectMapper.writeValueAsBytes(questTask);
			return objectMapper.readValue(bytes, QuestTask.class);
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static QuestTaskInfoPacket makeQuestTaskInfoPacket(QuestTask questTask)
	{
		QuestTaskInfoPacket packet = new QuestTaskInfoPacket();
		packet.setDescription(questTask.getDescription());
		packet.setPercentFinished(questTask.getPercentFinished());
		QuestTaskInfoPacket[] nextTasks = questTask.getNextTasks().stream()
				.map(PacketsMaker::makeQuestTaskInfoPacket)
				.toArray(QuestTaskInfoPacket[]::new);
		packet.setNextTasks(nextTasks);
		return packet;
	}

	public static InventoryItemStackPacket makeInventoryItemStackPacket(InventoryPosition firstPosition,
			InventoryPosition secondPosition)
	{
		InventoryItemStackPacket packet = new InventoryItemStackPacket();

		packet.setFirstPositionPageNumber(firstPosition.getInventoryPageNumber());
		packet.setFirstPositionPageX(firstPosition.getInventoryX());
		packet.setFirstPositionPageY(firstPosition.getInventoryY());

		packet.setSecondPositionPageNumber(secondPosition.getInventoryPageNumber());
		packet.setSecondPositionPageX(secondPosition.getInventoryX());
		packet.setSecondPositionPageY(secondPosition.getInventoryY());

		return packet;
	}

	public static KnownSpellInfoPacket makeKnownSpellInfoPacket(CharacterSpell spell)
	{
		KnownSpellInfoPacket packet = new KnownSpellInfoPacket();
		packet.setSpellIdentifer(spell.getIdentifier());
		return packet;
	}

	public static StackableItemAmountChangePacket makeStackableItemAmountChangePacket(int itemRemovalAmount,
			InventoryPosition position)
	{
		StackableItemAmountChangePacket packet = new StackableItemAmountChangePacket();
		packet.setItemAmount(itemRemovalAmount);
		packet.setItemPageNumber(position.getInventoryPageNumber());
		packet.setItemPageX(position.getInventoryX());
		packet.setItemPageY(position.getInventoryY());
		return packet;
	}

	public static QuestAcceptedEventPacket makeQuestAcceptedEventPacket(CharactersQuests quest)
	{
		QuestAcceptedEventPacket packet = new QuestAcceptedEventPacket();
		packet.setQuestName(quest.getQuest().getName());
		packet.setQuestStatePacket(makeQuestStateInfoPacket(quest));
		return packet;
	}

	public static MonsterKilledEventPacket makeMonsterKilledEventPacket(CharactersQuests quest, MonsterKilledEvent event)
	{
		MonsterKilledEventPacket packet = new MonsterKilledEventPacket();
		packet.setQuestName(quest.getQuest().getName());
		packet.setMonsterIdentifier(event.getMonsterIdentifier());
		return packet;
	}

	public static NpcDialogEventPacket makeNpcDialogEventPacket(CharactersQuests quest, String speech,
			Collection<String> possibleAnswers, NpcDialogEvent event)
	{
		String[] possibleAnswersInArray = possibleAnswers.toArray(new String[0]);
		return makeNpcDialogEventPacket(quest, speech, possibleAnswersInArray, event);
	}

	public static NpcDialogEventPacket makeNpcDialogEventPacket(CharactersQuests quest, String speech,
			String[] possibleAnswers, NpcDialogEvent event)
	{
		NpcDialogEventPacket packet = new NpcDialogEventPacket();
		packet.setQuestName(quest.getQuest().getName());
		packet.setNpcId(event.getNpc().getId());
		packet.setSpeech(speech);
		packet.setPossibleAnswers(possibleAnswers);
		return packet;
	}

	public static ItemEquippedSuccessfullyPacket makeItemEquippedSuccessfullyPacket(long itemId, String equipmentPosition)
	{
		ItemEquippedSuccessfullyPacket packet = new ItemEquippedSuccessfullyPacket();
		packet.setItemId(itemId);
		packet.setEquipmentPosition(equipmentPosition);
		return packet;
	}

	public static ItemTookOffSuccessfullyPacket makeItemTookOffSuccessfullyPacket(EquipmentPosition equipmentPosition, InventoryPositionPacket destinationPosition)
	{
		ItemTookOffSuccessfullyPacket packet = new ItemTookOffSuccessfullyPacket();
		packet.setEquipmentPosition(equipmentPosition.toString());
		packet.setDestinationPosition(destinationPosition);
		return packet;
	}
}
