package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.interfaces.PreEnchantedTooltip;
import de.dafuqs.spectrum.registries.SpectrumDefaultEnchantments;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class BedrockFishingRodItem extends FishingRodItem implements PreEnchantedTooltip {

	public BedrockFishingRodItem(Settings settings) {
		super(settings);
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
		super.appendTooltip(itemStack, world, tooltip, tooltipContext);
		addPreEnchantedTooltip(tooltip, itemStack);
	}
	
	@Override
	public ItemStack getDefaultStack() {
		return SpectrumDefaultEnchantments.getDefaultEnchantedStack(this);
	}

}