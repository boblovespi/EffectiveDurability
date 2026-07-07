package boblovespi.effectivedurability.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class EffectiveDurabilityClient implements ClientModInitializer
{
	public static EffectiveDurabilityClient instance;
	private float timeSinceDurabilityChange;
	private int lastDurability = -1;

	@Override
	public void onInitializeClient()
	{
		instance = this;
		HudRenderCallback.EVENT.register(this::drawHudElements);
		ClientTickEvents.START_CLIENT_TICK.register(this::onClientTick);
	}

	private void onClientTick(Minecraft minecraft)
	{
		if (timeSinceDurabilityChange > 0)
			timeSinceDurabilityChange--;
	}

	private void drawHudElements(GuiGraphics graphics, float delta)
	{
		if (!Config.HANDLER.instance().showEffectiveDurabilityByCrosshair)
			return;
		var maxTime = Config.HANDLER.instance().maxTime * 20;
		if (maxTime < 0)
			maxTime = 1_000_000_000;
		var partialTime = Mth.clamp(timeSinceDurabilityChange - delta, 0, maxTime / 2);
		var minecraft = Minecraft.getInstance();
		var player = minecraft.player;
		if (player == null)
			return;

		var hand = player.getUsedItemHand();
		var item = player.getItemInHand(hand);
		if (!item.isDamageableItem())
		{
			lastDurability = -1;
			timeSinceDurabilityChange = 0;
			return;
		}
		var durabilityMult = MonumentHelper.getEffectiveDurabilityMult(item);
		var currentDurability = item.getMaxDamage() - item.getDamageValue();
		var effectiveCurrentDurability = currentDurability * durabilityMult;
		if (effectiveCurrentDurability != lastDurability)
		{
			lastDurability = effectiveCurrentDurability;
			timeSinceDurabilityChange = maxTime;
			partialTime = timeSinceDurabilityChange / 2;
		}

		if (minecraft.options.hideGui)
			return;
		if (minecraft.getDebugOverlay().showDebugScreen())
			return;
		if (timeSinceDurabilityChange <= 0)
			return;

		var font = minecraft.font;
		var opacity = Mth.clamp((int) (0xFF * partialTime * 2 / maxTime), 0x04, 0xFF);
		if (maxTime > 1000)
			opacity = 0xFF;
		opacity <<= 24;
		var text = String.valueOf(effectiveCurrentDurability);
		var width = font.width(text);
		var height = font.lineHeight;
		var scale = switch (minecraft.options.guiScale().get())
		{
			case 1 -> 1f;
			case 2 -> 0.5f;
			case 3 -> 2f / 3f;
			case 4 -> 0.5f;
			case 5 -> 3f / 5f;
			default -> 0.5f;
		};
		graphics.pose().pushPose();
		graphics.pose().translate(graphics.guiWidth() / 2 - 7 - width * scale, graphics.guiHeight() / 2 - height / 2 * scale + 1, 0);
		graphics.pose().scale(scale, scale, scale);
		graphics.drawString(font, text, 0, 0, opacity | 0xBBBBBB, false);
		graphics.pose().popPose();
	}

	public void updateTime()
	{
		if (lastDurability >= 0)
		{
			var maxTime = Config.HANDLER.instance().maxTime;
			timeSinceDurabilityChange = maxTime < 0 ? 1_000_000_000 : maxTime * 20;
		}
	}
}