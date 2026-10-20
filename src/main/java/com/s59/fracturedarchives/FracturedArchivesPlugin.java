package com.s59.fracturedarchives;

import com.google.inject.Provides;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "The Fractured Archives",
	description = "WIP - only mining is included right now",
	tags = {"fractured", "archives", "ascension", "mining"}
)
public class FracturedArchivesPlugin extends Plugin
{
	static final int STARTING_AREA_REGION = 16470;
	static final int KNOWN_ASCENSION_ROCK = 63268;
	static final int ASCENSION_SPRITE = 16691;
	private static final String ASCENSION_ROCKS = "ascension rocks";

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private FracturedArchivesOverlay overlay;

	@Inject
	private FracturedArchivesConfig config;

	@Getter
	private final Set<GameObject> rocks = new HashSet<>();

	@Getter
	private final Set<NPC> sprites = new HashSet<>();

	@Provides
	FracturedArchivesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FracturedArchivesConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		clientThread.invokeLater(this::scanScene);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		reset();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invokeLater(this::scanScene);
		}
		else if (event.getGameState() == GameState.HOPPING || event.getGameState() == GameState.LOGIN_SCREEN)
		{
			reset();
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		registerRock(event.getGameObject(), true);
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		rocks.remove(event.getGameObject());
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		final NPC npc = event.getNpc();
		if (isInStartingArea(npc.getWorldLocation().getRegionID()) && isAscensionSprite(npc.getId(), npc.getName()))
		{
			sprites.add(npc);
			notifySprite("A sprite has appeared near the Ascension rocks!");
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		sprites.remove(event.getNpc());
	}

	boolean isBonusRock(GameObject rock)
	{
		for (NPC sprite : sprites)
		{
			if (sprite.getWorldLocation().distanceTo(rock.getWorldLocation()) <= 1)
			{
				return true;
			}
		}
		return false;
	}

	private void registerRock(GameObject object, boolean notify)
	{
		if (!isInStartingArea(object.getWorldLocation().getRegionID()))
		{
			return;
		}

		final ObjectComposition composition = getObjectComposition(object);
		if (!isAscensionRock(object.getId(), composition.getName())
			|| !hasAction(composition.getActions(), "Mine"))
		{
			return;
		}

		if (rocks.add(object) && isBonusRock(object) && notify)
		{
			notifySprite("A sprite has appeared in an Ascension rock!");
		}
	}

	private ObjectComposition getObjectComposition(GameObject object)
	{
		ObjectComposition composition = client.getObjectDefinition(object.getId());
		if (composition.getImpostorIds() != null)
		{
			final ObjectComposition impostor = composition.getImpostor();
			if (impostor != null)
			{
				composition = impostor;
			}
		}
		return composition;
	}

	private void scanScene()
	{
		reset();
		if (client.getGameState() != GameState.LOGGED_IN || !isPlayerInStartingArea())
		{
			return;
		}

		for (Tile[][] plane : client.getScene().getTiles())
		{
			for (Tile[] row : plane)
			{
				for (Tile tile : row)
				{
					if (tile == null)
					{
						continue;
					}
					for (GameObject object : tile.getGameObjects())
					{
						if (object != null)
						{
							registerRock(object, false);
						}
					}
				}
			}
		}

		for (NPC npc : client.getNpcs())
		{
			if (isAscensionSprite(npc.getId(), npc.getName()))
			{
				sprites.add(npc);
			}
		}
	}

	private boolean isPlayerInStartingArea()
	{
		final Player player = client.getLocalPlayer();
		return player != null && isInStartingArea(player.getWorldLocation().getRegionID());
	}

	private void notifySprite(String message)
	{
		if (config.spriteNotifications())
		{
			notifier.notify(message);
		}
	}

	private void reset()
	{
		rocks.clear();
		sprites.clear();
	}

	static boolean isInStartingArea(int regionId)
	{
		return regionId == STARTING_AREA_REGION;
	}

	static boolean isAscensionRock(int id, String name)
	{
		return id >= KNOWN_ASCENSION_ROCK && id <= 63271 || ASCENSION_ROCKS.equals(normalizeName(name));
	}

	static boolean isSpriteName(String name)
	{
		return normalizeName(name).contains("sprite");
	}

	static boolean isAscensionSprite(int id, String name)
	{
		return id == ASCENSION_SPRITE || isSpriteName(name);
	}

	static boolean hasAction(String[] actions, String expectedAction)
	{
		if (actions == null)
		{
			return false;
		}

		for (String action : actions)
		{
			if (expectedAction.equalsIgnoreCase(action))
			{
				return true;
			}
		}
		return false;
	}

	private static String normalizeName(String name)
	{
		return name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
	}
}
