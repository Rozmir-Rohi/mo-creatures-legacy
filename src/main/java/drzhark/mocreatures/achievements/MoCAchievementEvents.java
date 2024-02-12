package drzhark.mocreatures.achievements;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class MoCAchievementEvents {
	

	private boolean isWyvernEgg(ItemStack itemstack) //test if itemstack is a wyvern egg  (mocegg ID: 50-61)
    {
		if (itemstack.getItem().equals(MoCreatures.mocegg))
		{
			return (49 < itemstack.getItemDamage() && itemstack.getItemDamage() < 62);
		}
		
		return false;
    }
	
	@SubscribeEvent
	public void MoCItemPickupEvent(PlayerEvent.ItemPickupEvent event)
	{
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.heartundead)) {event.player.addStat(MoCAchievements.heart_undead, 1);}
	
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.heartDarkness)) {event.player.addStat(MoCAchievements.heart_darkness, 1);}
	
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.heartFire)) {event.player.addStat(MoCAchievements.heart_fire, 1);}
		
		if (isWyvernEgg(event.pickedUp.getEntityItem())) {event.player.addStat(MoCAchievements.wyvern_egg, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.bigcatClaw)) {event.player.addStat(MoCAchievements.big_cat_claw, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.sharkTeeth)) {event.player.addStat(MoCAchievements.shark_tooth, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.katana)) {event.player.addStat(MoCAchievements.leonardo, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.sai)) {event.player.addStat(MoCAchievements.raphael, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.bo)) {event.player.addStat(MoCAchievements.donatello, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.nunchaku)) {event.player.addStat(MoCAchievements.michelangelo, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.silverSword)) {event.player.addStat(MoCAchievements.silver_sword, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.fur)) {event.player.addStat(MoCAchievements.get_fur, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.hide)) {event.player.addStat(MoCAchievements.get_hide, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.hideReptile)) {event.player.addStat(MoCAchievements.get_reptile_hide, 1);}
		
		if (
				event.pickedUp.getEntityItem().getItem() == MoCreatures.chitin
				|| event.pickedUp.getEntityItem().getItem() == MoCreatures.chitinCave
				|| event.pickedUp.getEntityItem().getItem() == MoCreatures.chitinFrost
				|| event.pickedUp.getEntityItem().getItem() == MoCreatures.chitinNether
				|| event.pickedUp.getEntityItem().getItem() == MoCreatures.scorpStingDirt
				|| event.pickedUp.getEntityItem().getItem() == MoCreatures.scorpStingCave
				|| event.pickedUp.getEntityItem().getItem() == MoCreatures.scorpStingFrost
				|| event.pickedUp.getEntityItem().getItem() == MoCreatures.scorpStingNether
			) {event.player.addStat(MoCAchievements.get_scorpion_material, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.recordShuffle)) {event.player.addStat(MoCAchievements.zebra_record, 1);}
	
	}
	
	
	
	
	@SubscribeEvent
	public void MoCItemCraftedEvent(PlayerEvent.ItemCraftedEvent event)
	{
		if (event.crafting.getItem().equals(MoCreatures.craftedSaddle)) {event.player.addStat(MoCAchievements.craft_saddle, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.essenceUndead)) {event.player.addStat(MoCAchievements.essence_undead, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.amuletBone)) {event.player.addStat(MoCAchievements.amulet_bone, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.amuletGhost)) {event.player.addStat(MoCAchievements.amulet_ghost, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.essenceDarkness)) {event.player.addStat(MoCAchievements.essence_darkness, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.horseArmorCrystal)) {event.player.addStat(MoCAchievements.crystal_horse_armor, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.essenceFire)) {event.player.addStat(MoCAchievements.essence_fire, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.essenceLight))
		{
			event.player.addStat(MoCAchievements.essence_light, 1);
			event.player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle, 2)); //give the player back 2 bottles
		}
		
		if (event.crafting.getItem().equals(MoCreatures.amuletFairy)) {event.player.addStat(MoCAchievements.amulet_fairy, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.amuletPegasus)) {event.player.addStat(MoCAchievements.amulet_sky, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.medallion)) {event.player.addStat(MoCAchievements.craft_medallion, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.litterbox)) {event.player.addStat(MoCAchievements.kitty_litter_box, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.kittybed)) {event.player.addStat(MoCAchievements.kitty_bed, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.woolball)) {event.player.addStat(MoCAchievements.wool_ball, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.whip)) {event.player.addStat(MoCAchievements.craft_whip, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.fishNet)) {event.player.addStat(MoCAchievements.fish_net, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.sharkSword)) {event.player.addStat(MoCAchievements.shark_sword, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.petAmulet)) {event.player.addStat(MoCAchievements.pet_amulet, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.turtleSoup)) {event.player.addStat(MoCAchievements.cook_turtle, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.ratBurger)) {event.player.addStat(MoCAchievements.rat_burger, 1);}
		
		if (
				event.crafting.getItem() == MoCreatures.helmetFur
				|| event.crafting.getItem() == MoCreatures.chestFur
				|| event.crafting.getItem() == MoCreatures.legsFur
				|| event.crafting.getItem() == MoCreatures.bootsFur
			) {event.player.addStat(MoCAchievements.fur_armor, 1);}
		
		if (
				event.crafting.getItem() == MoCreatures.helmetHide
				|| event.crafting.getItem() == MoCreatures.chestHide
				|| event.crafting.getItem() == MoCreatures.legsHide
				|| event.crafting.getItem() == MoCreatures.bootsHide
			) {event.player.addStat(MoCAchievements.hide_armor, 1);}
		
		if (
				event.crafting.getItem() == MoCreatures.helmetReptile
				|| event.crafting.getItem() == MoCreatures.plateReptile
				|| event.crafting.getItem() == MoCreatures.legsReptile
				|| event.crafting.getItem() == MoCreatures.bootsReptile
			) {event.player.addStat(MoCAchievements.reptile_armor, 1);}
		
		if (
				event.crafting.getItem() == MoCreatures.helmetReptile
				|| event.crafting.getItem() == MoCreatures.plateReptile
				|| event.crafting.getItem() == MoCreatures.legsReptile
				|| event.crafting.getItem() == MoCreatures.bootsReptile
			) {event.player.addStat(MoCAchievements.reptile_armor, 1);}
		
		if (
				event.crafting.getItem() == MoCreatures.scorpSwordDirt
				|| event.crafting.getItem() == MoCreatures.scorpSwordCave
				|| event.crafting.getItem() == MoCreatures.scorpSwordFrost
				|| event.crafting.getItem() == MoCreatures.scorpSwordNether
			) {event.player.addStat(MoCAchievements.scorpion_sword, 1);}
		
		if (
				event.crafting.getItem() == MoCreatures.scorpHelmetDirt
				|| event.crafting.getItem() == MoCreatures.scorpPlateDirt
				|| event.crafting.getItem() == MoCreatures.scorpLegsDirt
				|| event.crafting.getItem() == MoCreatures.scorpBootsDirt
				|| event.crafting.getItem() == MoCreatures.scorpHelmetCave
				|| event.crafting.getItem() == MoCreatures.scorpPlateCave
				|| event.crafting.getItem() == MoCreatures.scorpLegsCave
				|| event.crafting.getItem() == MoCreatures.scorpBootsCave
				|| event.crafting.getItem() == MoCreatures.scorpHelmetFrost
				|| event.crafting.getItem() == MoCreatures.scorpPlateFrost
				|| event.crafting.getItem() == MoCreatures.scorpLegsFrost
				|| event.crafting.getItem() == MoCreatures.scorpBootsFrost
				|| event.crafting.getItem() == MoCreatures.scorpHelmetNether
				|| event.crafting.getItem() == MoCreatures.scorpPlateNether
				|| event.crafting.getItem() == MoCreatures.scorpLegsNether
				|| event.crafting.getItem() == MoCreatures.scorpBootsNether
			) {event.player.addStat(MoCAchievements.scorpion_armor, 1);}
		
	}
	
	
	
	
	@SubscribeEvent
	public void MoCItemSmeltedEvent(PlayerEvent.ItemSmeltedEvent event)
	{
		if (event.smelting.getItem().equals(MoCreatures.omelet)) {event.player.addStat(MoCAchievements.cook_omelette, 1);}
		
		if (event.smelting.getItem().equals(MoCreatures.turkeyCooked)) {event.player.addStat(MoCAchievements.cook_turkey, 1);}
		
		if (event.smelting.getItem().equals(MoCreatures.ostrichCooked)) {event.player.addStat(MoCAchievements.cook_ostrich, 1);}
		
		if (event.smelting.getItem().equals(MoCreatures.ratCooked)) {event.player.addStat(MoCAchievements.cook_rat, 1);}
		
		if (event.smelting.getItem().equals(MoCreatures.crabCooked)) {event.player.addStat(MoCAchievements.cook_crab, 1);}
	}

}

