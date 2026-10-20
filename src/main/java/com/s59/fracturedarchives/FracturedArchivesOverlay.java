package com.s59.fracturedarchives;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class FracturedArchivesOverlay extends Overlay
{
	private final FracturedArchivesPlugin plugin;
	private final FracturedArchivesConfig config;

	@Inject
	private FracturedArchivesOverlay(FracturedArchivesPlugin plugin, FracturedArchivesConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		for (GameObject rock : plugin.getRocks())
		{
			final Shape shape = rock.getClickbox();
			if (shape != null)
			{
				final boolean bonus = plugin.isBonusRock(rock);
				OverlayUtil.renderPolygon(graphics, shape, bonus ? config.spriteColor() : config.rockColor());
			}
		}

		for (NPC sprite : plugin.getSprites())
		{
			final Shape shape = sprite.getConvexHull();
			if (shape != null)
			{
				OverlayUtil.renderPolygon(graphics, shape, config.spriteColor());
			}
		}
		return null;
	}
}
