package de.dafuqs.spectrum.items;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.progression.advancement.HasAdvancementCriterion;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.Map;

public class ManualItem extends Item {


	public ManualItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient && user instanceof ServerPlayerEntity) {

			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) user;

			// Workaround for new advancement unlocks getting added after spectrum has been installed
			reprocessAdvancementUnlocks(serverPlayerEntity);

			// if the player has never opened the book before
			// automatically open the introduction page
			if(isNewPlayer(serverPlayerEntity)) {
				openManual(serverPlayerEntity, new Identifier(SpectrumCommon.MOD_ID, "general/intro"), 0);
			} else {
				openManual(serverPlayerEntity);
			}

			user.incrementStat(Stats.USED.getOrCreateStat(this));

			return TypedActionResult.success(user.getStackInHand(hand));
		} else {
			return TypedActionResult.consume(user.getStackInHand(hand));
		}
	}

	private boolean isNewPlayer(ServerPlayerEntity serverPlayerEntity) {
		return serverPlayerEntity.getStatHandler().getStat(Stats.USED, this) == 0;
	}

	private void openManual(ServerPlayerEntity serverPlayerEntity) {
		PatchouliAPI.get().openBookGUI(serverPlayerEntity, new Identifier(SpectrumCommon.MOD_ID, "manual"));
	}

	private void openManual(ServerPlayerEntity serverPlayerEntity, Identifier entry, int page) {
		PatchouliAPI.get().openBookEntry(serverPlayerEntity, new Identifier(SpectrumCommon.MOD_ID, "manual"), entry, page);
	}

	public static void reprocessAdvancementUnlocks(ServerPlayerEntity serverPlayerEntity) {
		PlayerAdvancementTracker tracker = serverPlayerEntity.getAdvancementTracker();

		// "has advancement" criteria with nonexistent advancements
		for(Advancement advancement : SpectrumCommon.minecraftServer.getAdvancementLoader().getAdvancements()) {
			if(advancement.getId().getNamespace().equals(SpectrumCommon.MOD_ID)) {
				AdvancementProgress hasAdvancement = tracker.getProgress(advancement);
				if(!hasAdvancement.isDone()) {
					for (Map.Entry<String, AdvancementCriterion> criterionEntry : advancement.getCriteria().entrySet()) {
						CriterionConditions conditions = criterionEntry.getValue().getConditions();
						if (conditions.getId().equals(HasAdvancementCriterion.ID) && conditions instanceof HasAdvancementCriterion.Conditions hasAdvancementConditions) {
							Identifier advancementIdentifier = hasAdvancementConditions.getAdvancementIdentifier();
							Advancement advancementCriterionAdvancement = SpectrumCommon.minecraftServer.getAdvancementLoader().get(advancementIdentifier);
							if (advancementCriterionAdvancement != null) {
								AdvancementProgress hasAdvancementCriterionAdvancement = tracker.getProgress(advancementCriterionAdvancement);
								if (hasAdvancementCriterionAdvancement.isDone()) {
									tracker.grantCriterion(advancement, criterionEntry.getKey());
								}
							}
						}
					}
				}
			}
		}
	}

}