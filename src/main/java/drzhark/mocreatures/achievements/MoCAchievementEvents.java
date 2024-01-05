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
	
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.heartdarkness)) {event.player.addStat(MoCAchievements.heart_darkness, 1);}
	
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.heartfire)) {event.player.addStat(MoCAchievements.heart_fire, 1);}
		
		if (isWyvernEgg(event.pickedUp.getEntityItem())) {event.player.addStat(MoCAchievements.wyvern_egg, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.bigcatclaw)) {event.player.addStat(MoCAchievements.big_cat_claw, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.sharkteeth)) {event.player.addStat(MoCAchievements.shark_tooth, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.katana)) {event.player.addStat(MoCAchievements.leonardo, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.sai)) {event.player.addStat(MoCAchievements.raphael, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.bo)) {event.player.addStat(MoCAchievements.donatello, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.nunchaku)) {event.player.addStat(MoCAchievements.michelangelo, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.silversword)) {event.player.addStat(MoCAchievements.silver_sword, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.fur)) {event.player.addStat(MoCAchievements.get_fur, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.animalHide)) {event.player.addStat(MoCAchievements.get_hide, 1);}
		
		if (event.pickedUp.getEntityItem().getItem().equals(MoCreatures.hideCroc)) {event.player.addStat(MoCAchievements.get_reptile_hide, 1);}
		
		if (
				event.pickedUp.getEntityItem().getItem() == MoCreatures.chitin
				|| event.pickedUp.getEntityItem().getItem() == MoCreatures.chitinCave
				|| event.pickedUp.getEntityItem().getItem() == MoCreatures.chitinFrost
				|| event.pickedUp.getEntityItem().getItem() == MoCreatures.chitinNether
				|| event.pickedUp.getEntityItem().getItem() == MoCreatures.scorpStingDirt
				|| event.pickedUp.getEntityItem().getItem() == MoCreatures.scorpStingCave
				|| event.pickedUp.getEntityItem().getItem() == MoCreatures.scorpStingFrost
				|| event.pickedUp.getEntityItem().getItem() == MoCreatures.scorpStingNether
			) {event.player.addStat(MoCAchievements.get_scorpian_material, 1);}
	
	}
	
	
	
	
	@SubscribeEvent
	public void MoCItemCraftedEvent(PlayerEvent.ItemCraftedEvent event)
	{
		if (event.crafting.getItem().equals(MoCreatures.horsesaddle)) {event.player.addStat(MoCAchievements.craft_saddle, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.essenceundead)) {event.player.addStat(MoCAchievements.essence_undead, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.amuletbone)) {event.player.addStat(MoCAchievements.amulet_bone, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.amuletghost)) {event.player.addStat(MoCAchievements.amulet_ghost, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.essencedarkness)) {event.player.addStat(MoCAchievements.essence_darkness, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.horsearmorcrystal)) {event.player.addStat(MoCAchievements.crystal_horse_armor, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.essencefire)) {event.player.addStat(MoCAchievements.essence_fire, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.essencelight))
		{
			event.player.addStat(MoCAchievements.essence_light, 1);
			event.player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle, 2)); //give the player back 2 bottles
		}
		
		if (event.crafting.getItem().equals(MoCreatures.amuletfairy)) {event.player.addStat(MoCAchievements.amulet_fairy, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.amuletpegasus)) {event.player.addStat(MoCAchievements.amulet_sky, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.medallion)) {event.player.addStat(MoCAchievements.craft_medallion, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.litterbox)) {event.player.addStat(MoCAchievements.kitty_litter_box, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.kittybed)) {event.player.addStat(MoCAchievements.kitty_bed, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.woolball)) {event.player.addStat(MoCAchievements.wool_ball, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.whip)) {event.player.addStat(MoCAchievements.craft_whip, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.fishnet)) {event.player.addStat(MoCAchievements.fish_net, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.sharksword)) {event.player.addStat(MoCAchievements.shark_sword, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.petamulet)) {event.player.addStat(MoCAchievements.pet_amulet, 1);}
		
		if (event.crafting.getItem().equals(MoCreatures.turtlesoup)) {event.player.addStat(MoCAchievements.cook_turtle, 1);}
		
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
				event.crafting.getItem() == MoCreatures.helmetCroc
				|| event.crafting.getItem() == MoCreatures.plateCroc
				|| event.crafting.getItem() == MoCreatures.legsCroc
				|| event.crafting.getItem() == MoCreatures.bootsCroc
			) {event.player.addStat(MoCAchievements.reptile_armor, 1);}
		
		if (
				event.crafting.getItem() == MoCreatures.helmetCroc
				|| event.crafting.getItem() == MoCreatures.plateCroc
				|| event.crafting.getItem() == MoCreatures.legsCroc
				|| event.crafting.getItem() == MoCreatures.bootsCroc
			) {event.player.addStat(MoCAchievements.reptile_armor, 1);}
		
		if (
				event.crafting.getItem() == MoCreatures.scorpSwordDirt
				|| event.crafting.getItem() == MoCreatures.scorpSwordCave
				|| event.crafting.getItem() == MoCreatures.scorpSwordFrost
				|| event.crafting.getItem() == MoCreatures.scorpSwordNether
			) {event.player.addStat(MoCAchievements.scorpian_sword, 1);}
		
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
			) {event.player.addStat(MoCAchievements.scorpian_armor, 1);}
		
	}
	
	
	
	
	@SubscribeEvent
	public void MoCItemSmeltedEvent(PlayerEvent.ItemSmeltedEvent event)
	{
		if (event.smelting.getItem().equals(MoCreatures.omelet)) {event.player.addStat(MoCAchievements.cook_omelette, 1);}
		
		if (event.smelting.getItem().equals(MoCreatures.cookedTurkey)) {event.player.addStat(MoCAchievements.cook_turkey, 1);}
		
		if (event.smelting.getItem().equals(MoCreatures.ostrichcooked)) {event.player.addStat(MoCAchievements.cook_ostrich, 1);}
		
		if (event.smelting.getItem().equals(MoCreatures.ratCooked)) {event.player.addStat(MoCAchievements.cook_rat, 1);}
		
		if (event.smelting.getItem().equals(MoCreatures.crabcooked)) {event.player.addStat(MoCAchievements.cook_crab, 1);}
	}

}

