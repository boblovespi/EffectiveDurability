package boblovespi.effectivedurability.client;

import boblovespi.effectivedurability.EffectiveDurability;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;

public class Config
{
	public static ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
																		 .id(EffectiveDurability.id("config"))
																		 .serializer(c -> GsonConfigSerializerBuilder.create(c)
																													 .setPath(FabricLoader.getInstance()
																																		  .getConfigDir()
																																		  .resolve(EffectiveDurability.MOD_ID +
																																				   ".json5"))
																													 .setJson5(true)
																													 .build())
																		 .build();
	@SerialEntry
	public boolean alwaysEnableTooltip = true;
	@SerialEntry
	public TooltipMode showEffectiveDurabilityInTooltip = TooltipMode.BOTH;
	@SerialEntry
	public boolean showEffectiveDurabilityByCrosshair = false;
	@SerialEntry
	public int primaryColor = 0xB4ECE7;
	@SerialEntry
	public int secondaryColor = 0x555555;
	@SerialEntry
	public float maxTime = 2f;
	@SerialEntry
	public int durabilityThreshold = 0;
}
