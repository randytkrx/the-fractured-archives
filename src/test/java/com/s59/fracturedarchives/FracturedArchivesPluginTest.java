package com.s59.fracturedarchives;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class FracturedArchivesPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(FracturedArchivesPlugin.class);
		RuneLite.main(args);
	}
}
