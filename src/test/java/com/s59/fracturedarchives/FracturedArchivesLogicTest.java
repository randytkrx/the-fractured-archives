package com.s59.fracturedarchives;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class FracturedArchivesLogicTest
{
	@Test
	public void identifiesAscensionRockVariants()
	{
		assertTrue(FracturedArchivesPlugin.isAscensionRock(63268, "null"));
		assertTrue(FracturedArchivesPlugin.isAscensionRock(63271, "null"));
		assertTrue(FracturedArchivesPlugin.isAscensionRock(70000, "Ascension rocks"));
		assertFalse(FracturedArchivesPlugin.isAscensionRock(70000, "Rocks"));
	}

	@Test
	public void identifiesSprites()
	{
		assertTrue(FracturedArchivesPlugin.isAscensionSprite(16691, null));
		assertTrue(FracturedArchivesPlugin.isAscensionSprite(20000, "Archive sprite"));
		assertFalse(FracturedArchivesPlugin.isAscensionSprite(20000, "Miner"));
	}

	@Test
	public void requiresMineAction()
	{
		assertTrue(FracturedArchivesPlugin.hasAction(new String[]{null, "Mine", "Prospect"}, "Mine"));
		assertTrue(FracturedArchivesPlugin.hasAction(new String[]{"MINE"}, "Mine"));
		assertFalse(FracturedArchivesPlugin.hasAction(new String[]{"Examine"}, "Mine"));
		assertFalse(FracturedArchivesPlugin.hasAction(null, "Mine"));
	}
}
