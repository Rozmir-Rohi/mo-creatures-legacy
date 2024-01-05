package drzhark.mocreatures;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;


public class MoCThaumcraftAspects {

	public static void addThaumcraftAspects() {
// =====================================================================================
                //ITEMS
// =====================================================================================


ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.bigcatclaw), new AspectList().add(Aspect.BEAST, 1).add(Aspect.WEAPON, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.bo), new AspectList().add(Aspect.WEAPON, 3).add(Aspect.TREE, 3));


// Amulet -----------------------------------------------------------------------------------

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.amuletbone), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.UNDEAD, 2).add(Aspect.GREED, 1).add(Aspect.VOID, 4).add(Aspect.BEAST, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.amuletbonefull), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.UNDEAD, 2).add(Aspect.GREED, 1).add(Aspect.BEAST, 6).add(Aspect.VOID, 4));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.amuletfairy), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.AURA, 2).add(Aspect.GREED, 1).add(Aspect.VOID, 4).add(Aspect.BEAST, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.amuletfairyfull), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.AURA, 2).add(Aspect.GREED, 1).add(Aspect.BEAST, 6).add(Aspect.VOID, 4));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.amuletghost), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.SOUL, 2).add(Aspect.GREED, 1).add(Aspect.VOID, 4).add(Aspect.BEAST, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.amuletghostfull), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.SOUL, 2).add(Aspect.GREED, 1).add(Aspect.BEAST, 6).add(Aspect.VOID, 4));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.petamulet, 1, 0), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.GREED, 2).add(Aspect.VOID, 4).add(Aspect.BEAST, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.petamulet, 1, 1), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.GREED, 2).add(Aspect.BEAST, 6).add(Aspect.VOID, 4));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.amuletpegasus), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.AIR, 2).add(Aspect.GREED, 1).add(Aspect.VOID, 4).add(Aspect.BEAST, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.amuletpegasusfull), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.AIR, 2).add(Aspect.GREED, 1).add(Aspect.BEAST, 6).add(Aspect.VOID, 4));

// ---------------------------------------------------------------------------------------------


ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.horsesaddle), new AspectList().add(Aspect.TRAVEL, 3).add(Aspect.CRAFT, 1).add(Aspect.CLOTH, 3).add(Aspect.METAL, 2).add(Aspect.BEAST, 2));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.horsearmorcrystal), new AspectList().add(Aspect.ARMOR, 6).add(Aspect.BEAST, 2));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.elephantChest), new AspectList().add(Aspect.VOID, 4).add(Aspect.TRAVEL, 2).add(Aspect.CLOTH, 3).add(Aspect.BEAST, 2));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.elephantGarment), new AspectList().add(Aspect.CLOTH, 4).add(Aspect.CRAFT, 2).add(Aspect.ARMOR, 1).add(Aspect.BEAST, 2));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.elephantHarness), new AspectList().add(Aspect.TRAVEL, 3).add(Aspect.CLOTH, 3).add(Aspect.CRAFT, 2).add(Aspect.METAL, 1).add(Aspect.BEAST, 2));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.elephantHowdah), new AspectList().add(Aspect.CLOTH, 3).add(Aspect.CRAFT, 2).add(Aspect.TREE, 1).add(Aspect.BEAST, 2));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.essencedarkness), new AspectList().add(Aspect.DARKNESS, 4).add(Aspect.MAGIC, 3).add(Aspect.EXCHANGE, 3).add(Aspect.BEAST, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.essencefire), new AspectList().add(Aspect.FIRE, 4).add(Aspect.MAGIC, 3).add(Aspect.EXCHANGE, 3).add(Aspect.BEAST, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.essencelight), new AspectList().add(Aspect.AURA, 2).add(Aspect.LIGHT, 2).add(Aspect.MAGIC, 3).add(Aspect.EXCHANGE, 3).add(Aspect.BEAST, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.essenceundead), new AspectList().add(Aspect.UNDEAD, 4).add(Aspect.MAGIC, 3).add(Aspect.EXCHANGE, 3).add(Aspect.BEAST, 2));



// Fish Bowls ------------------------------------------------------------------------------------

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fishbowl_e), new AspectList().add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 2));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fishbowl_w), new AspectList().add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 2).add(Aspect.WATER, 4));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fishbowl_1), new AspectList().add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 2).add(Aspect.WATER, 4).add(Aspect.BEAST, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fishbowl_2), new AspectList().add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 2).add(Aspect.WATER, 4).add(Aspect.BEAST, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fishbowl_3), new AspectList().add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 2).add(Aspect.WATER, 4).add(Aspect.BEAST, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fishbowl_4), new AspectList().add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 2).add(Aspect.WATER, 4).add(Aspect.BEAST, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fishbowl_5), new AspectList().add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 2).add(Aspect.WATER, 4).add(Aspect.BEAST, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fishbowl_6), new AspectList().add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 2).add(Aspect.WATER, 4).add(Aspect.BEAST, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fishbowl_7), new AspectList().add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 2).add(Aspect.WATER, 4).add(Aspect.BEAST, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fishbowl_8), new AspectList().add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 2).add(Aspect.WATER, 4).add(Aspect.BEAST, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fishbowl_9), new AspectList().add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 2).add(Aspect.WATER, 4).add(Aspect.BEAST, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fishbowl_10), new AspectList().add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 2).add(Aspect.WATER, 4).add(Aspect.BEAST, 1));

// --------------------------------------------------------------------------------------------------


// Edible Food ------------------------------------------------------------------------------------

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.crabraw), new AspectList().add(Aspect.FLESH, 4).add(Aspect.BEAST, 2).add(Aspect.LIFE, 2).add(Aspect.WATER, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.crabcooked), new AspectList().add(Aspect.HUNGER, 4).add(Aspect.FLESH, 2).add(Aspect.CRAFT, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.omelet), new AspectList().add(Aspect.HUNGER, 2).add(Aspect.FLESH, 1).add(Aspect.CRAFT, 1));

for (int i = 0; i <= 90; i++) //add aspects for all ids of moc egg from 0 to 90
{
	ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.mocegg, 1, i), new AspectList().add(Aspect.SLIME, 1).add(Aspect.LIFE, 2).add(Aspect.BEAST, 2));
}

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.ostrichraw), new AspectList().add(Aspect.FLESH, 2).add(Aspect.BEAST, 2).add(Aspect.LIFE, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.ostrichcooked), new AspectList().add(Aspect.HUNGER, 3).add(Aspect.FLESH, 2).add(Aspect.CRAFT, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.ratRaw), new AspectList().add(Aspect.FLESH, 1).add(Aspect.BEAST, 2).add(Aspect.LIFE, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.ratBurger), new AspectList().add(Aspect.HUNGER, 4).add(Aspect.CROP, 2).add(Aspect.FLESH, 1).add(Aspect.CRAFT, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.ratCooked), new AspectList().add(Aspect.HUNGER, 2).add(Aspect.FLESH, 1).add(Aspect.CRAFT, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.rawTurkey), new AspectList().add(Aspect.FLESH, 4).add(Aspect.BEAST, 2).add(Aspect.LIFE, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.cookedTurkey), new AspectList().add(Aspect.HUNGER, 4).add(Aspect.FLESH, 2).add(Aspect.CRAFT, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.turtleraw), new AspectList().add(Aspect.FLESH, 2).add(Aspect.BEAST, 2).add(Aspect.WATER, 1).add(Aspect.LIFE, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.turtlesoup), new AspectList().add(Aspect.HUNGER, 3).add(Aspect.FLESH, 1).add(Aspect.CRAFT, 1));

// -------------------------------------------------------------------------------------------------



ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fishnet, 1, 0), new AspectList().add(Aspect.TRAP, 2).add(Aspect.TOOL, 1).add(Aspect.CLOTH, 2).add(Aspect.WATER, 1).add(Aspect.BEAST, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fishnet, 1, 1), new AspectList().add(Aspect.TRAP, 2).add(Aspect.TOOL, 1).add(Aspect.CLOTH, 2).add(Aspect.WATER, 2).add(Aspect.BEAST, 3));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.fur), new AspectList().add(Aspect.CLOTH, 2).add(Aspect.BEAST, 2).add(Aspect.ARMOR, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.helmetFur), new AspectList().add(Aspect.CLOTH, 7).add(Aspect.BEAST, 2).add(Aspect.ARMOR, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.chestFur), new AspectList().add(Aspect.CLOTH, 12).add(Aspect.BEAST, 6).add( Aspect.ARMOR, 3));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.legsFur), new AspectList().add(Aspect.CLOTH, 10).add(Aspect.BEAST, 5).add(Aspect.ARMOR, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.bootsFur), new AspectList().add(Aspect.CLOTH, 6).add(Aspect.BEAST, 2).add(Aspect.ARMOR, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.haystack), new AspectList().add(Aspect.HUNGER, 4).add(Aspect.CROP, 5).add(Aspect.ORDER, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.heartdarkness), new AspectList().add(Aspect.DARKNESS, 3).add(Aspect.FLESH, 2).add(Aspect.MAGIC, 1).add(Aspect.EXCHANGE, 1).add(Aspect.BEAST, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.heartfire), new AspectList().add(Aspect.FIRE, 3).add(Aspect.FLESH, 2).add(Aspect.MAGIC, 1).add(Aspect.EXCHANGE, 1).add(Aspect.BEAST, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.heartundead), new AspectList().add(Aspect.UNDEAD, 3).add(Aspect.FLESH, 2).add(Aspect.MAGIC, 1).add(Aspect.EXCHANGE, 1).add(Aspect.BEAST, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.animalHide), new AspectList().add(Aspect.CLOTH, 2).add(Aspect.BEAST, 2).add(Aspect.ARMOR, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.helmetHide), new AspectList().add(Aspect.CLOTH, 7).add(Aspect.BEAST, 2).add(Aspect.ARMOR, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.chestHide), new AspectList().add(Aspect.CLOTH, 12).add(Aspect.BEAST, 6).add( Aspect.ARMOR, 3));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.legsHide), new AspectList().add(Aspect.CLOTH, 10).add(Aspect.BEAST, 5).add(Aspect.ARMOR, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.bootsHide), new AspectList().add(Aspect.CLOTH, 6).add(Aspect.BEAST, 2).add(Aspect.ARMOR, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.katana), new AspectList().add(Aspect.WEAPON, 3).add(Aspect.METAL, 6));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.kittybed), new AspectList().add(Aspect.CLOTH, 1).add(Aspect.TREE, 3).add(Aspect.CRAFT, 1).add(Aspect.BEAST, 2));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.litterbox), new AspectList().add(Aspect.EARTH, 3).add(Aspect.TREE, 2));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.mammothPlatform), new AspectList().add(Aspect.TRAVEL, 3).add(Aspect.TREE, 3).add(Aspect.CLOTH, 2).add(Aspect.BEAST, 2));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.medallion), new AspectList().add(Aspect.CLOTH, 2).add(Aspect.METAL, 1).add(Aspect.GREED, 1).add(Aspect.BEAST, 2));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.nunchaku), new AspectList().add(Aspect.WEAPON, 3).add(Aspect.METAL, 6));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.petfood), new AspectList().add(Aspect.HUNGER, 1).add(Aspect.FLESH, 1).add(Aspect.BEAST, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.recordshuffle), new AspectList().add( Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.BEAST, 4).add(Aspect.GREED, 4));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.hideCroc), new AspectList().add( Aspect.ARMOR, 2).add(Aspect.BEAST, 2).add(Aspect.CLOTH, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.helmetCroc), new AspectList().add(Aspect.CLOTH, 3).add(Aspect.BEAST, 2).add(Aspect.ARMOR, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.plateCroc), new AspectList().add(Aspect.CLOTH, 6).add(Aspect.BEAST, 6).add(Aspect.ARMOR, 6));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.legsCroc), new AspectList().add(Aspect.CLOTH, 5).add(Aspect.BEAST, 5).add(Aspect.ARMOR, 5));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.bootsCroc), new AspectList().add(Aspect.CLOTH, 3).add( Aspect.BEAST, 2).add(Aspect.ARMOR, 2));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.sai), new AspectList().add(Aspect.WEAPON, 3).add(Aspect.METAL, 6));





// Scorpion --------------------------------------------------------------------------------

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.chitin), new AspectList().add(Aspect.ARMOR, 2).add(Aspect.BEAST, 2).add(Aspect.CLOTH, 1).add(Aspect.HEAL, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpHelmetDirt), new AspectList().add(Aspect.ARMOR, 2).add(Aspect.BEAST, 2).add(Aspect.CLOTH, 3).add(Aspect.HEAL, 3));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpPlateDirt), new AspectList().add(Aspect.ARMOR, 6).add(Aspect.BEAST, 6).add(Aspect.CLOTH, 6).add(Aspect.HEAL, 3));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpLegsDirt), new AspectList().add(Aspect.ARMOR, 5).add(Aspect.BEAST, 5).add(Aspect.CLOTH, 5).add(Aspect.HEAL, 3));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpBootsDirt), new AspectList().add(Aspect.ARMOR, 2).add(Aspect.BEAST, 2).add(Aspect.CLOTH, 3).add(Aspect.HEAL, 3));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpStingDirt), new AspectList().add(Aspect.BEAST, 1).add(Aspect.WEAPON, 1).add(Aspect.POISON, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpSwordDirt), new AspectList().add(Aspect.WEAPON, 4).add(Aspect.BEAST, 4).add(Aspect.CRYSTAL, 3).add(Aspect.POISON, 3));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.chitinFrost), new AspectList().add(Aspect.ARMOR, 2).add(Aspect.BEAST, 2).add(Aspect.CLOTH, 1).add(Aspect.WATER, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpHelmetFrost), new AspectList().add(Aspect.ARMOR, 2).add(Aspect.BEAST, 2).add(Aspect.CLOTH, 3).add(Aspect.WATER, 3));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpPlateFrost), new AspectList().add(Aspect.ARMOR, 6).add(Aspect.BEAST, 6).add(Aspect.CLOTH, 6).add(Aspect.WATER, 3));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpLegsFrost), new AspectList().add(Aspect.ARMOR, 5).add(Aspect.BEAST, 5).add(Aspect.CLOTH, 5).add(Aspect.WATER, 3));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpBootsFrost), new AspectList().add(Aspect.ARMOR, 2).add(Aspect.BEAST, 2).add(Aspect.CLOTH, 3).add(Aspect.BEAST, 2).add(Aspect.WATER, 3));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpStingFrost), new AspectList().add(Aspect.BEAST, 1).add(Aspect.WEAPON, 1).add(Aspect.TRAP, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpSwordFrost), new AspectList().add(Aspect.WEAPON, 4).add(Aspect.BEAST, 4).add(Aspect.CRYSTAL, 3).add(Aspect.TRAP, 3));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.chitinCave), new AspectList().add(Aspect.ARMOR, 2).add(Aspect.BEAST, 2).add(Aspect.CLOTH, 1).add(Aspect.SENSES, 1).add(Aspect.ENTROPY, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpHelmetCave), new AspectList().add(Aspect.ARMOR, 2).add(Aspect.BEAST, 2).add(Aspect.CLOTH, 3).add(Aspect.SENSES, 3).add(Aspect.ENTROPY, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpPlateCave), new AspectList().add(Aspect.ARMOR, 6).add(Aspect.BEAST, 6).add(Aspect.CLOTH, 6).add(Aspect.SENSES, 3).add(Aspect.ENTROPY, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpLegsCave), new AspectList().add(Aspect.ARMOR, 5).add(Aspect.BEAST, 5).add(Aspect.CLOTH, 5).add(Aspect.SENSES, 3).add(Aspect.ENTROPY, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpBootsCave), new AspectList().add(Aspect.ARMOR, 2).add(Aspect.BEAST, 2).add(Aspect.CLOTH, 3).add(Aspect.BEAST, 2).add(Aspect.SENSES, 3).add(Aspect.ENTROPY, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpStingCave), new AspectList().add(Aspect.BEAST, 1).add(Aspect.WEAPON, 1).add(Aspect.SENSES, 1).add(Aspect.DARKNESS, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpSwordCave), new AspectList().add(Aspect.WEAPON, 4).add(Aspect.BEAST, 4).add(Aspect.CRYSTAL, 3).add(Aspect.SENSES, 2).add(Aspect.DARKNESS, 3));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.chitinNether), new AspectList().add(Aspect.ARMOR, 2).add(Aspect.BEAST, 2).add(Aspect.CLOTH, 1).add(Aspect.FIRE, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpHelmetNether), new AspectList().add(Aspect.ARMOR, 2).add(Aspect.BEAST, 2).add(Aspect.CLOTH, 3).add(Aspect.FIRE, 3));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpPlateNether), new AspectList().add(Aspect.ARMOR, 6).add(Aspect.BEAST, 6).add(Aspect.CLOTH, 6).add(Aspect.FIRE, 3));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpLegsNether), new AspectList().add(Aspect.ARMOR, 5).add(Aspect.BEAST, 5).add(Aspect.CLOTH, 5).add(Aspect.FIRE, 3));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpBootsNether), new AspectList().add(Aspect.ARMOR, 2).add(Aspect.BEAST, 2).add(Aspect.CLOTH, 3).add(Aspect.BEAST, 2).add(Aspect.FIRE, 3));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpStingNether), new AspectList().add(Aspect.BEAST, 1).add(Aspect.WEAPON, 1).add(Aspect.FIRE, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scorpSwordNether), new AspectList().add(Aspect.WEAPON, 4).add(Aspect.BEAST, 4).add(Aspect.CRYSTAL, 3).add(Aspect.FIRE, 3));

// -------------------------------------------------------------------------------------------


ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scrollFreedom), new AspectList().add(Aspect.MIND, 2).add(Aspect.FLIGHT, 1).add(Aspect.BEAST, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.scrollOfSale), new AspectList().add(Aspect.MIND, 2).add(Aspect.EXCHANGE, 1).add(Aspect.BEAST, 2));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.sharkteeth), new AspectList().add(Aspect.BEAST, 1).add(Aspect.WEAPON, 1).add(Aspect.WATER, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.sharksword), new AspectList().add(Aspect.WEAPON, 3).add(Aspect.TREE, 3).add(Aspect.BEAST, 3));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.silversword), new AspectList().add(Aspect.WEAPON, 5).add(Aspect.METAL, 5).add(Aspect.POISON, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.sugarlump), new AspectList().add(Aspect.CROP, 4).add(Aspect.CRYSTAL, 4).add(Aspect.HUNGER, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.tusksWood), new AspectList().add(Aspect.WEAPON, 1).add(Aspect.TREE, 2).add(Aspect.BEAST, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.tusksIron), new AspectList().add(Aspect.WEAPON, 3).add(Aspect.METAL, 2).add(Aspect.BEAST, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.tusksDiamond), new AspectList().add(Aspect.WEAPON, 4).add(Aspect.CRYSTAL, 2).add(Aspect.GREED, 1).add(Aspect.BEAST, 2));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.unicornhorn), new AspectList().add(Aspect.AURA, 2).add(Aspect.BEAST, 1).add(Aspect.WEAPON, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.whip), new AspectList().add(Aspect.CLOTH, 3).add(Aspect.ORDER, 2).add(Aspect.BEAST, 2).add(Aspect.METAL, 1).add(Aspect.TOOL, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.woolball), new AspectList().add(Aspect.CLOTH, 2).add(Aspect.CRAFT, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.mocDirt), new AspectList().add(Aspect.EARTH, 2));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.mocGrass), new AspectList().add(Aspect.EARTH, 1).add(Aspect.PLANT, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.mocLog), new AspectList().add(Aspect.TREE, 4).add(Aspect.MAGIC, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.mocLeaf), new AspectList().add(Aspect.PLANT, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.mocStone), new AspectList().add(Aspect.EARTH, 2).add(Aspect.MAGIC, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.mocTallGrass), new AspectList().add(Aspect.PLANT, 1).add(Aspect.AIR, 1));
ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.mocPlank), new AspectList().add(Aspect.TREE, 1));

ThaumcraftApi.registerObjectTag(new ItemStack(MoCreatures.staffPortal), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.MAGIC, 2).add(Aspect.TOOL, 1));


// =====================================================================================
                //MOBS
// =====================================================================================

if (MoCreatures.proxy.replaceVanillaCreepers)
{
	ThaumcraftApi.registerEntityTag("MoCreatures.CreeperExtension", new AspectList().add(Aspect.PLANT, 2).add(Aspect.FIRE, 2)); 
}
	
// Water Animals ------------------------------------------------------------------------------------
ThaumcraftApi.registerEntityTag("MoCreatures.Fishy", new AspectList().add(Aspect.WATER, 2).add(Aspect.BEAST, 2)); 
ThaumcraftApi.registerEntityTag("MoCreatures.SmallFish", new AspectList().add(Aspect.WATER, 2).add(Aspect.BEAST, 2)); 
ThaumcraftApi.registerEntityTag("MoCreatures.Piranha", new AspectList().add(Aspect.WATER, 2).add(Aspect.BEAST, 2).add(Aspect.WEAPON, 1)); 

ThaumcraftApi.registerEntityTag("MoCreatures.MediumFish", new AspectList().add(Aspect.WATER, 3).add(Aspect.BEAST, 3));  

ThaumcraftApi.registerEntityTag("MoCreatures.Crab", new AspectList().add(Aspect.WATER, 2).add(Aspect.EARTH, 2).add(Aspect.BEAST, 2)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Ray", new AspectList().add(Aspect.WATER, 3).add(Aspect.BEAST, 3));  

ThaumcraftApi.registerEntityTag("MoCreatures.JellyFish", new AspectList().add(Aspect.WATER, 2).add(Aspect.POISON, 2).add(Aspect.BEAST, 2)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Dolphin", new AspectList().add(Aspect.WATER, 4).add(Aspect.BEAST, 4)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Shark", new AspectList().add(Aspect.WEAPON, 2).add(Aspect.WATER, 4).add(Aspect.BEAST, 4));  



// -------------------------------------------------------------------------------------------------------




// Small Insects ------------------------------------------------------------------------------------

ThaumcraftApi.registerEntityTag("MoCreatures.Ant", new AspectList().add(Aspect.EARTH, 1).add(Aspect.BEAST, 1)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Bee", new AspectList().add(Aspect.FLIGHT, 1).add(Aspect.BEAST, 1)); 

ThaumcraftApi.registerEntityTag("MoCreatures.ButterFly", new AspectList().add(Aspect.FLIGHT, 1).add(Aspect.BEAST, 1));

ThaumcraftApi.registerEntityTag("MoCreatures.Cricket", new AspectList().add(Aspect.FLIGHT, 1).add(Aspect.EARTH, 1).add(Aspect.BEAST, 1));

ThaumcraftApi.registerEntityTag("MoCreatures.DragonFly", new AspectList().add(Aspect.FLIGHT, 1).add(Aspect.BEAST, 1)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Firefly", new AspectList().add(Aspect.FLIGHT, 1).add(Aspect.LIGHT, 1).add(Aspect.BEAST, 1)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Fly", new AspectList().add(Aspect.FLIGHT, 1).add(Aspect.ENTROPY, 1).add(Aspect.BEAST, 1)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Maggot", new AspectList().add(Aspect.WATER, 1).add(Aspect.ENTROPY, 1).add(Aspect.BEAST, 1)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Roach", new AspectList().add(Aspect.ENTROPY, 1).add(Aspect.FLIGHT, 1).add(Aspect.BEAST, 1)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Snail", new AspectList().add(Aspect.WATER, 1).add(Aspect.SLIME, 1).add(Aspect.BEAST, 1)); 

// -------------------------------------------------------------------------------------------------------




// Land Animals ------------------------------------------------------------------------------------
ThaumcraftApi.registerEntityTag("MoCreatures.Bird", new AspectList().add(Aspect.FLIGHT, 2).add(Aspect.AIR, 2).add(Aspect.BEAST, 2)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Bear", new AspectList().add(Aspect.WEAPON, 1).add(Aspect.EARTH, 4).add(Aspect.BEAST, 4)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Boar", new AspectList().add(Aspect.EARTH, 3).add(Aspect.BEAST, 2));

ThaumcraftApi.registerEntityTag("MoCreatures.Bunny", new AspectList().add(Aspect.MOTION, 2).add(Aspect.EARTH, 2).add(Aspect.BEAST, 2));

ThaumcraftApi.registerEntityTag("MoCreatures.Deer", new AspectList().add(Aspect.AIR, 2).add(Aspect.EARTH, 4).add(Aspect.BEAST, 4));  

ThaumcraftApi.registerEntityTag("MoCreatures.Elephant", new AspectList().add(Aspect.EARTH, 10).add(Aspect.BEAST, 10)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Fox", new AspectList().add(Aspect.WEAPON, 1).add(Aspect.EARTH, 3).add(Aspect.BEAST, 3)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Goat", new AspectList().add(Aspect.HUNGER, 2).add(Aspect.EARTH, 2).add(Aspect.BEAST, 2));  

ThaumcraftApi.registerEntityTag("MoCreatures.Kitty", new AspectList().add(Aspect.AIR, 1).add(Aspect.ENTROPY, 3).add(Aspect.BEAST, 3)); 

ThaumcraftApi.registerEntityTag("MoCreatures.KomodoDragon", new AspectList().add(Aspect.POISON, 2).add(Aspect.EARTH, 4).add(Aspect.WATER, 1).add(Aspect.BEAST, 4));

ThaumcraftApi.registerEntityTag("MoCreatures.BigCat", new AspectList().add(Aspect.WEAPON, 3).add(Aspect.EARTH, 4).add(Aspect.BEAST, 4)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Mole", new AspectList().add(Aspect.EARTH, 2).add(Aspect.BEAST, 2)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Mouse", new AspectList().add(Aspect.EARTH, 1).add(Aspect.BEAST, 2)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Ostrich", new AspectList().add(Aspect.FLIGHT, 1).add(Aspect.EARTH, 3).add(Aspect.BEAST, 8)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Raccoon", new AspectList().add(Aspect.GREED, 2).add(Aspect.EARTH, 2).add(Aspect.BEAST, 2)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Snake", new AspectList().add(Aspect.POISON, 2).add(Aspect.EARTH, 2).add(Aspect.BEAST, 2)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Turkey", new AspectList().add(Aspect.FLIGHT, 2).add(Aspect.EARTH, 2).add(Aspect.BEAST, 2));  

ThaumcraftApi.registerEntityTag("MoCreatures.WildHorse", new AspectList().add(Aspect.EARTH, 1).add(Aspect.AIR, 1).add(Aspect.BEAST, 4)); 

// -------------------------------------------------------------------------------------------------------




// River/Swamp Animals ------------------------------------------------------------------------------------

ThaumcraftApi.registerEntityTag("MoCreatures.Turtle", new AspectList().add(Aspect.WATER, 2).add(Aspect.ARMOR, 6).add(Aspect.BEAST, 2));

ThaumcraftApi.registerEntityTag("MoCreatures.Duck", new AspectList().add(Aspect.FLIGHT, 2).add(Aspect.WATER, 1).add(Aspect.BEAST, 2));

ThaumcraftApi.registerEntityTag("MoCreatures.Crocodile", new AspectList().add(Aspect.WEAPON, 4).add(Aspect.TRAP, 2).add(Aspect.EARTH, 4).add(Aspect.WATER, 4).add(Aspect.BEAST, 4)); 

// -------------------------------------------------------------------------------------------------------




// Magical Creatures------------------------------------------------------------------------------------ 

ThaumcraftApi.registerEntityTag("MoCreatures.Wyvern", new AspectList().add(Aspect.POISON, 2).add(Aspect.WEAPON, 2).add(Aspect.FLIGHT, 12).add(Aspect.BEAST, 4));
// -------------------------------------------------------------------------------------------------------




// Monsters ------------------------------------------------------------------------------------
ThaumcraftApi.registerEntityTag("MoCreatures.Ogre", new AspectList().add(Aspect.DARKNESS, 2).add(Aspect.ENTROPY, 6).add(Aspect.BEAST, 6));

ThaumcraftApi.registerEntityTag("MoCreatures.HorseMob", new AspectList().add(Aspect.EARTH, 1).add(Aspect.AIR, 1).add(Aspect.BEAST, 4).add(Aspect.ENTROPY, 4));

ThaumcraftApi.registerEntityTag("MoCreatures.MiniGolem", new AspectList().add(Aspect.EARTH, 2).add(Aspect.MAGIC, 2).add(Aspect.MECHANISM, 2).add(Aspect.METAL, 4)); 

ThaumcraftApi.registerEntityTag("MoCreatures.BigGolem", new AspectList().add(Aspect.EARTH, 4).add(Aspect.MAGIC, 8).add(Aspect.MECHANISM, 6).add(Aspect.METAL, 8)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Rat", new AspectList().add(Aspect.EARTH, 2).add(Aspect.BEAST, 2).add(Aspect.ENTROPY, 2)); 
ThaumcraftApi.registerEntityTag("MoCreatures.HellRat", new AspectList().add(Aspect.FIRE, 2).add(Aspect.EARTH, 3).add(Aspect.BEAST, 2)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Scorpion", new AspectList().add(Aspect.ENTROPY, 4).add(Aspect.BEAST, 4));
ThaumcraftApi.registerEntityTag("MoCreatures.PetScorpion", new AspectList().add(Aspect.ENTROPY, 4).add(Aspect.BEAST, 4));  

ThaumcraftApi.registerEntityTag("MoCreatures.SilverSkeleton", new AspectList().add(Aspect.POISON, 2).add(Aspect.METAL, 3).add(Aspect.MAN, 1).add(Aspect.UNDEAD, 3)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Werewolf", new AspectList().add(Aspect.MAN, 3).add(Aspect.EXCHANGE, 2).add(Aspect.DARKNESS, 4).add(Aspect.BEAST, 4)); 

ThaumcraftApi.registerEntityTag("MoCreatures.Wraith", new AspectList().add(Aspect.SOUL, 3).add(Aspect.MAN, 1).add(Aspect.UNDEAD, 3));
ThaumcraftApi.registerEntityTag("MoCreatures.FlameWraith", new AspectList().add(Aspect.FIRE, 3).add(Aspect.SOUL, 3).add(Aspect.MAN, 1).add(Aspect.UNDEAD, 3));  

ThaumcraftApi.registerEntityTag("MoCreatures.WWolf", new AspectList().add(Aspect.WEAPON, 2).add(Aspect.EARTH, 4).add(Aspect.BEAST, 4)); 
// -------------------------------------------------------------------------------------------------------
	}
}
