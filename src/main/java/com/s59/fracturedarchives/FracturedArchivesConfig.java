package com.s59.fracturedarchives;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(FracturedArchivesConfig.GROUP)
public interface FracturedArchivesConfig extends Config
{
	String GROUP = "fracturedarchives";

	@Alpha
	@ConfigItem(
		keyName = "rockColor",
		name = "Rock color",
		description = "Color used to highlight Ascension rocks",
		position = 0
	)
	default Color rockColor()
	{
		return new Color(0, 190, 255, 120);
	}

	@Alpha
	@ConfigItem(
		keyName = "spriteColor",
		name = "Sprite rock color",
		description = "Color used to highlight rocks containing a sprite",
		position = 1
	)
	default Color spriteColor()
	{
		return new Color(255, 190, 0, 150);
	}

	@ConfigItem(
		keyName = "spriteNotifications",
		name = "Sprite notifications",
		description = "Notify when a sprite-bearing rock appears",
		position = 2
	)
	default boolean spriteNotifications()
	{
		return true;
	}
}
