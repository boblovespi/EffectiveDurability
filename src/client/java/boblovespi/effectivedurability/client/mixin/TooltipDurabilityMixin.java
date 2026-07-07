package boblovespi.effectivedurability.client.mixin;

import boblovespi.effectivedurability.client.Config;
import boblovespi.effectivedurability.client.MonumentHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class TooltipDurabilityMixin
{
	@Shadow
	public abstract int getMaxDamage();

	@Shadow
	public abstract int getDamageValue();

	@Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 15))
	private boolean redirectAddDurabilityComponent(List<Component> list, Object obj)
	{
		var config = Config.HANDLER.instance();
		var currentDurability = getMaxDamage() - getDamageValue();
		switch (config.showEffectiveDurabilityInTooltip)
		{
			case VANILLA -> list.add(Component.translatable("item.durability", currentDurability, getMaxDamage()).withColor(config.primaryColor));
			case EFFECTIVE ->
			{
				var durabilityMult = MonumentHelper.getEffectiveDurabilityMult((ItemStack) (Object) this);
				var effectiveCurrentDurability = currentDurability * durabilityMult;
				var effectiveMaxDurability = getMaxDamage() * durabilityMult;
				list.add(Component.translatable("item.durability", effectiveCurrentDurability, effectiveMaxDurability).withColor(config.primaryColor));
			}
			case BOTH ->
			{
				var durabilityMult = MonumentHelper.getEffectiveDurabilityMult((ItemStack) (Object) this);
				if (durabilityMult > 1)
				{
					var effectiveCurrentDurability = currentDurability * durabilityMult;
					var effectiveMaxDurability = getMaxDamage() * durabilityMult;
					var first = Component.translatable("item.durability", effectiveCurrentDurability, effectiveMaxDurability).withColor(config.primaryColor);
					var second = Component.translatable("bob-effectivedurability.item.durability_only_values", currentDurability, getMaxDamage()).withColor(config.secondaryColor);
					list.add(Component.translatable("bob-effectivedurability.item.durability_both", first, second));
				}
				else
				{
					list.add(Component.translatable("item.durability", currentDurability, getMaxDamage()).withColor(config.primaryColor));
				}
			}
		}
		return true;
	}

	@Inject(method = "getTooltipLines", at = @At("RETURN"))
	private void onGetTooltipLines(Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir)
	{
		var config = Config.HANDLER.instance();
		if (config.alwaysEnableTooltip && !tooltipFlag.isAdvanced())
		{
			var list = cir.getReturnValue();
			var currentDurability = getMaxDamage() - getDamageValue();
			switch (config.showEffectiveDurabilityInTooltip)
			{
				case VANILLA -> list.add(Component.translatable("item.durability", currentDurability, getMaxDamage()).withColor(config.primaryColor));
				case EFFECTIVE ->
				{
					var durabilityMult = MonumentHelper.getEffectiveDurabilityMult((ItemStack) (Object) this);
					var effectiveCurrentDurability = currentDurability * durabilityMult;
					var effectiveMaxDurability = getMaxDamage() * durabilityMult;
					list.add(Component.translatable("item.durability", effectiveCurrentDurability, effectiveMaxDurability).withColor(config.primaryColor));
				}
				case BOTH ->
				{
					var durabilityMult = MonumentHelper.getEffectiveDurabilityMult((ItemStack) (Object) this);
					if (durabilityMult > 1)
					{
						var effectiveCurrentDurability = currentDurability * durabilityMult;
						var effectiveMaxDurability = getMaxDamage() * durabilityMult;
						var first = Component.translatable("item.durability", effectiveCurrentDurability, effectiveMaxDurability).withColor(config.primaryColor);
						var second = Component.translatable("bob-effectivedurability.item.durability_only_values", currentDurability, getMaxDamage())
											  .withColor(config.secondaryColor);
						list.add(Component.translatable("bob-effectivedurability.item.durability_both", first, second));
					}
					else
					{
						list.add(Component.translatable("item.durability", currentDurability, getMaxDamage()).withColor(config.primaryColor));
					}
				}
			}
		}
	}
}
