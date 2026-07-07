package boblovespi.effectivedurability.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class ModMenuHandler implements ModMenuApi
{
	private static ControllerBuilder<Boolean> enableDisableController(Option<Boolean> o)
	{
		return BooleanControllerBuilder.create(o)
									   .formatValue(b -> Component.translatable(b ? "bob-effectivedurability.config.enabled" : "bob-effectivedurability.config.disabled"))
									   .coloured(true);
	}

	private static ControllerBuilder<TooltipMode> tooltipModeController(Option<TooltipMode> o)
	{
		return EnumControllerBuilder.create(o)
									.enumClass(TooltipMode.class)
									.formatValue(v -> Component.translatable("bob-effectivedurability.config.tooltip_mode." + v.name().toLowerCase()));
	}

	private static ControllerBuilder<Float> secondsController(Option<Float> o)
	{
		return FloatSliderControllerBuilder.create(o).range(-1f, 10f).step(0.25f).formatValue(v -> Component.literal(v + "s"));
	}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory()
	{
		Config.HANDLER.load();
		var defaults = Config.HANDLER.defaults();
		var inst = Config.HANDLER.instance();
		// @formatter:off
		return parent -> YetAnotherConfigLib
								 .createBuilder()
								 .title(Component.literal("Effective Durability Config"))
								 .category(
										 ConfigCategory
										 .createBuilder()
										 .name(Component.translatable("bob-effectivedurability.config.name"))
										 .option(
												 Option
												 .<Boolean>createBuilder()
												 .name(Component.translatable("bob-effectivedurability.config.always_enable_tooltip.name"))
												 .description(
														 OptionDescription.of(
																 Component.translatable("bob-effectivedurability.config.always_enable_tooltip.tooltip")))
												 .binding(defaults.alwaysEnableTooltip, () -> inst.alwaysEnableTooltip, b -> inst.alwaysEnableTooltip = b)
												 .controller(ModMenuHandler::enableDisableController)
												 .build()
												)
										 .option(
												 Option
												 .<Boolean>createBuilder()
												 .name(Component.translatable("bob-effectivedurability.config.show_crosshair.name"))
												 .description(
														 OptionDescription.of(
																 Component.translatable("bob-effectivedurability.config.show_crosshair.tooltip")))
												 .binding(defaults.showEffectiveDurabilityByCrosshair, () -> inst.showEffectiveDurabilityByCrosshair,
														 b -> inst.showEffectiveDurabilityByCrosshair = b)
												 .controller(ModMenuHandler::enableDisableController)
												 .build()
												)
										 .option(
												 Option
												 .<TooltipMode>createBuilder()
												 .name(Component.translatable("bob-effectivedurability.config.show_tooltip.name"))
												 .description(
														 OptionDescription.of(
																 Component.translatable("bob-effectivedurability.config.show_tooltip.tooltip")))
												 .binding(defaults.showEffectiveDurabilityInTooltip, () -> inst.showEffectiveDurabilityInTooltip,
														 b -> inst.showEffectiveDurabilityInTooltip = b)
												 .controller(ModMenuHandler::tooltipModeController)
												 .build()
												)
										 .option(
												 Option
												 .<Color>createBuilder()
												 .name(Component.translatable("bob-effectivedurability.config.primary_color.name"))
												 .description(
														 OptionDescription.of(
																 Component.translatable("bob-effectivedurability.config.primary_color.tooltip")))
												 .binding(new Color(defaults.primaryColor), () -> new Color(inst.primaryColor),
														 b -> inst.primaryColor = b.getRGB())
												 .controller(ColorControllerBuilder::create)
												 .build()
												)
										 .option(
												 Option
												 .<Color>createBuilder()
												 .name(Component.translatable("bob-effectivedurability.config.secondary_color.name"))
												 .description(
														 OptionDescription.of(
																 Component.translatable("bob-effectivedurability.config.secondary_color.tooltip")))
												 .binding(new Color(defaults.secondaryColor), () -> new Color(inst.secondaryColor),
														 b -> inst.secondaryColor = b.getRGB())
												 .controller(ColorControllerBuilder::create)
												 .build()
												)
										 .option(
												 Option
												 .<Float>createBuilder()
												 .name(Component.translatable("bob-effectivedurability.config.crosshair_time.name"))
												 .description(
														 OptionDescription.of(
																 Component.translatable("bob-effectivedurability.config.crosshair_time.tooltip")))
												 .binding(defaults.maxTime, () -> inst.maxTime,
														 b -> inst.maxTime = b)
												 .controller(ModMenuHandler::secondsController)
												 .build()
												)
										 .build()
										  )
								 .save(() -> Config.HANDLER.save())
								 .build()
								 .generateScreen(parent);
		// @formatter:on
	}
}
