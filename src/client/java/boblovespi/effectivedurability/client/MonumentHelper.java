package boblovespi.effectivedurability.client;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public final class MonumentHelper
{
	private MonumentHelper()
	{

	}

	public static int getInfusionLevel(ItemStack item, String infusion)
	{
		var tag = item.getTag();
		if (tag == null)
			return 0;
		tag = tag.getCompound("Monumenta");
		tag = tag.getCompound("PlayerModified");
		tag = tag.getCompound("Infusions");
		if (tag.contains(infusion))
		{
			tag = tag.getCompound(infusion);
			return tag.getInt("Level");
		}
		return 0;
	}

	public static int getEffectiveDurabilityMult(ItemStack item)
	{
		var unbreaking = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, item);
		var colossal = getInfusionLevel(item, "Colossal");
		return (unbreaking + 1) * (colossal + 1);
	}
}
