package drzhark.mocreatures.achievements;

import cpw.mods.fml.common.FMLCommonHandler;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.AchievementPage;

public class MoCAchievements {

public static Achievement craft_saddle;

public static Achievement tier2_horse;
public static Achievement tier3_horse;
public static Achievement tier4_horse;
public static Achievement zebra;
public static Achievement zorse;

public static Achievement heart_undead;
public static Achievement essence_undead;
public static Achievement undead_horse;
public static Achievement amulet_bone;

public static Achievement ghost_horse;
public static Achievement amulet_ghost;

public static Achievement heart_fire;
public static Achievement essence_fire;
public static Achievement nightmare_horse;

public static Achievement heart_darkness;
public static Achievement essence_darkness;
public static Achievement bat_horse;
public static Achievement crystal_horse_armor;

public static Achievement essence_light;
public static Achievement pegasus;
public static Achievement unicorn;

public static Achievement amulet_sky;
public static Achievement dark_pegasus;


public static Achievement fairy_horse;
public static Achievement amulet_fairy;


public static Achievement wyvern_portal_staff;
public static Achievement wyvern_egg;

public static Achievement tame_elephant;
public static Achievement mount_elephant;
public static Achievement mammoth_platform;
public static Achievement elephant_garment;
public static Achievement elephant_tusks;
public static Achievement elephant_chest;
public static Achievement elephant_howdah;

public static Achievement craft_medallion;
public static Achievement tame_kitty;
public static Achievement kitty_litter_box;
public static Achievement kitty_litter;
public static Achievement kitty_bed;
public static Achievement pet_food;
public static Achievement wool_ball;

public static Achievement tame_big_cat;

public static Achievement big_cat_claw;
public static Achievement craft_whip;
public static Achievement indiana;

public static Achievement shark_tooth;
public static Achievement fish_net;
public static Achievement shark_sword;

public static Achievement catch_fish_in_fish_bowl;

public static Achievement tame_bird;
public static Achievement feed_snake_with_live_mouse;
public static Achievement tame_panda;
public static Achievement tame_scorpian;
public static Achievement ostrich_egg;

public static Achievement pet_amulet;
public static Achievement cook_omelette;
public static Achievement cook_turkey;
public static Achievement cook_ostrich;
public static Achievement cook_rat;
public static Achievement rat_burger;
public static Achievement cook_crab;
public static Achievement cook_turtle;
public static Achievement kill_wraith;
public static Achievement kill_ogre;
public static Achievement kill_werewolf;
public static Achievement kill_big_golem;
public static Achievement leonardo;
public static Achievement raphael;
public static Achievement donatello;
public static Achievement michelangelo;
public static Achievement silver_sword;
public static Achievement get_fur;
public static Achievement fur_armor;
public static Achievement get_hide;
public static Achievement hide_armor;
public static Achievement get_reptile_hide;
public static Achievement reptile_armor;
public static Achievement get_scorpian_material;
public static Achievement scorpian_sword;
public static Achievement scorpian_armor;



	
public static void initilization() 
{
	
	FMLCommonHandler.instance().bus().register(new MoCAchievementEvents());
	
	
	craft_saddle = new Achievement("achievement.craft_saddle", "craft_saddle", 0, 0, new ItemStack(MoCreatures.horsesaddle), (Achievement)null).initIndependentStat().registerStat();
	
	
	tier2_horse = new Achievement("achievement.tier2_horse", "tier2_horse", 7, 0, MoCreatures.achievement_icon_tier2_horse, (Achievement)null).initIndependentStat().registerStat();
	tier3_horse = new Achievement("achievement.tier3_horse", "tier3_horse", 9, 0, MoCreatures.achievement_icon_tier3_horse, tier2_horse).registerStat();
	tier4_horse = new Achievement("achievement.tier4_horse", "tier4_horse", 11, 0, MoCreatures.achievement_icon_tier4_horse, tier3_horse).registerStat();
	zebra = new Achievement("achievement.zebra", "zebra", 13, 0, MoCreatures.achievement_icon_zebra, tier4_horse).registerStat();
	zorse = new Achievement("achievement.zorse", "zorse", 15, 0, MoCreatures.achievement_icon_zorse, zebra).registerStat();
	
	heart_undead = new Achievement("achievement.heart_undead", "heart_undead", 15, 6, new ItemStack(MoCreatures.heartundead), AchievementList.buildSword).registerStat();
	essence_undead = new Achievement("achievement.essence_undead", "essence_undead", 15, 4, new ItemStack(MoCreatures.essenceundead), heart_undead).registerStat();
	undead_horse = new Achievement("achievement.undead_horse", "undead_horse", 15, 2, MoCreatures.achievement_icon_undead_horse, essence_undead).registerStat();
	amulet_bone = new Achievement("achievement.amulet_bone", "amulet_bone", 13, 2, new ItemStack(MoCreatures.amuletbone), undead_horse).registerStat();
	
	
	ghost_horse = new Achievement("achievement.ghost_horse", "ghost_horse", 15, 8, MoCreatures.achievement_icon_ghost_horse, (Achievement)null).initIndependentStat().registerStat();
	amulet_ghost = new Achievement("achievement.amulet_ghost", "amulet_ghost", 13, 8, new ItemStack(MoCreatures.amuletghost), ghost_horse).registerStat();
	
	
	
	heart_fire = new Achievement("achievement.heart_fire", "heart_fire", 17, 6, new ItemStack(MoCreatures.heartfire), AchievementList.buildSword).registerStat();
	essence_fire = new Achievement("achievement.essence_fire", "essence_fire", 17, 4, new ItemStack(MoCreatures.essencefire), heart_fire).registerStat();
	nightmare_horse =  new Achievement("achievement.nightmare_horse", "nightmare_horse", 17, 2, MoCreatures.achievement_icon_nightmare_horse, essence_fire).registerStat();
	
	
	heart_darkness = new Achievement("achievement.heart_darkness", "heart_darkness", 17, -6, new ItemStack(MoCreatures.heartdarkness), AchievementList.buildSword).registerStat();
	essence_darkness = new Achievement("achievement.essence_darkness", "essence_darkness", 17, -4, new ItemStack(MoCreatures.essencedarkness), heart_darkness).registerStat();
	bat_horse = new Achievement("achievement.bat_horse", "bat_horse", 17, -2, MoCreatures.achievement_icon_bat_horse, essence_darkness).registerStat();
	crystal_horse_armor = new Achievement("achievement.crystal_horse_armor", "crystal_horse_armor", 15, -2, new ItemStack(MoCreatures.horsearmorcrystal), bat_horse).registerStat();
	
	
	essence_light = new Achievement("achievement.essence_light", "essence_light", 19, 0, new ItemStack(MoCreatures.essencelight), (Achievement)null).initIndependentStat().registerStat();
	pegasus = new Achievement("achievement.pegasus", "pegasus", 21, -2, MoCreatures.achievement_icon_pegasus, essence_light).registerStat();
	unicorn = new Achievement("achievement.unicorn", "unicorn", 21, 2, MoCreatures.achievement_icon_unicorn, essence_light).registerStat();
	
	
	amulet_sky = new Achievement("achievement.amulet_sky", "amulet_sky", 21, -6, new ItemStack(MoCreatures.amuletpegasus), pegasus).registerStat();
	dark_pegasus = new Achievement("achievement.dark_pegasus", "dark_pegasus", 23, -4, MoCreatures.achievement_icon_dark_pegasus, pegasus).registerStat();
	
	fairy_horse = new Achievement("achievement.fairy_horse", "fairy_horse", 23, 0, MoCreatures.achievement_icon_fairy_horse, essence_light).setSpecial().registerStat();
	amulet_fairy = new Achievement("achievement.amulet_fairy", "amulet_fairy", 25, 0, new ItemStack(MoCreatures.amuletfairy), fairy_horse).registerStat();
	
	
	wyvern_portal_staff = new Achievement("achievement.wyvern_portal_staff", "wyvern_portal_staff", 19, 8, new ItemStack(MoCreatures.staffPortal), essence_light).registerStat();
	wyvern_egg = new Achievement("achievement.wyvern_egg", "wyvern_egg", 21, 8, new ItemStack(MoCreatures.mocegg), wyvern_portal_staff).setSpecial().registerStat();
	
	
	
	
	
	
	
	

	
	
	
	tame_elephant = new Achievement("achievement.tame_elephant", "tame_elephant", 7, -5, new ItemStack(MoCreatures.sugarlump), (Achievement)null).initIndependentStat().registerStat();
	mount_elephant = new Achievement("achievement.mount_elephant", "mount_elephant", 9, -5, new ItemStack(MoCreatures.elephantHarness), tame_elephant).registerStat();
	mammoth_platform = new Achievement("achievement.mammoth_platform", "mammoth_platform", 11, -7, new ItemStack(MoCreatures.mammothPlatform), mount_elephant).registerStat();
	elephant_garment = new Achievement("achievement.elephant_garment", "elephant_garment", 11, -6, new ItemStack(MoCreatures.elephantGarment), mount_elephant).registerStat();
	elephant_tusks = new Achievement("achievement.elephant_tusks", "elephant_tusks", 11, -5, new ItemStack(MoCreatures.tusksIron), mount_elephant).registerStat();
	elephant_chest = new Achievement("achievement.elephant_chest", "elephant_chest", 11, -4, new ItemStack(MoCreatures.elephantChest), mount_elephant).registerStat();
	elephant_howdah = new Achievement("achievement.elephant_howdah", "elephant_howdah", 13, -6, new ItemStack(MoCreatures.elephantHowdah), elephant_garment).registerStat();
	
	
	craft_medallion = new Achievement("achievement.craft_medallion", "craft_medallion", 2, 4, new ItemStack(MoCreatures.medallion), (Achievement)null).initIndependentStat().registerStat();
	tame_kitty = new Achievement("achievement.tame_kitty", "tame_kitty", 4, 6, new ItemStack(MoCreatures.achievement_icon_tame_kitty), craft_medallion).registerStat();
	kitty_litter_box = new Achievement("achievement.kitty_litter_box", "kitty_litter_box", 3, 8, new ItemStack(MoCreatures.litterbox), tame_kitty).registerStat();
	kitty_litter = new Achievement("achievement.kitty_litter", "kitty_litter", 3, 10, new ItemStack(Blocks.sand), kitty_litter_box).registerStat();
	kitty_bed = new Achievement("achievement.kitty_bed", "kitty_bed", 4, 8, new ItemStack(MoCreatures.kittybed), tame_kitty).registerStat();
	pet_food = new Achievement("achievement.pet_food", "pet_food", 4, 10, new ItemStack(MoCreatures.petfood), kitty_bed).registerStat();
	wool_ball = new Achievement("achievement.wool_ball", "wool_ball", 5, 8, new ItemStack(MoCreatures.woolball), tame_kitty).registerStat();
	
	tame_big_cat = new Achievement("achievement.tame_big_cat", "tame_big_cat", 0, 6, new ItemStack(MoCreatures.achievement_icon_tame_big_cat), craft_medallion).registerStat();
	
	big_cat_claw = new Achievement("achievement.big_cat_claw", "big_cat_claw", -2, 4, new ItemStack(MoCreatures.bigcatclaw), AchievementList.buildSword).registerStat();
	craft_whip = new Achievement("achievement.craft_whip", "craft_whip", -2, 6, new ItemStack(MoCreatures.whip), big_cat_claw).registerStat();
	indiana = new Achievement("achievement.indiana", "indiana", 0, 8, new ItemStack(MoCreatures.achievement_icon_indiana), craft_whip).registerStat();
	
	
	shark_tooth = new Achievement("achievement.shark_tooth", "shark_tooth", -6, 4, new ItemStack(MoCreatures.sharkteeth), AchievementList.buildSword).registerStat();
	fish_net = new Achievement("achievement.fish_net", "fish_net", -5, 6, new ItemStack(MoCreatures.fishnet), shark_tooth).registerStat();
	shark_sword = new Achievement("achievement.shark_sword", "shark_sword", -7, 6, new ItemStack(MoCreatures.sharksword), shark_tooth).registerStat();
	
	catch_fish_in_fish_bowl = new Achievement("achievement.catch_fish_in_fish_bowl", "catch_fish_in_fish_bowl", -9, 4, new ItemStack(MoCreatures.fishbowl_1), (Achievement)null).initIndependentStat().registerStat();
	
	pet_amulet = new Achievement("achievement.pet_amulet", "pet_amulet", 6, 4, new ItemStack(MoCreatures.petamulet), (Achievement)null).initIndependentStat().registerStat();
	
	
	
	tame_bird = new Achievement("achievement.tame_bird", "tame_bird", -7, 2, new ItemStack(MoCreatures.achievement_icon_tame_bird), (Achievement)null).initIndependentStat().registerStat();
	
	feed_snake_with_live_mouse = new Achievement("achievement.feed_snake_with_live_mouse", "feed_snake_with_live_mouse", -9, 2, new ItemStack(MoCreatures.achievement_icon_feed_snake_with_live_mouse), (Achievement)null).initIndependentStat().registerStat();
	
	tame_panda = new Achievement("achievement.tame_panda", "tame_panda", -11, 2,  new ItemStack(MoCreatures.achievement_icon_tame_panda), (Achievement)null).initIndependentStat().registerStat();
	
	tame_scorpian = new Achievement("achievement.tame_scorpian", "tame_scorpian", -13, 2,  new ItemStack(MoCreatures.achievement_icon_tame_scorpian), (Achievement)null).initIndependentStat().registerStat();
	
	ostrich_egg = new Achievement("achievement.ostrich_egg", "ostrich_egg", -15, 2, new ItemStack(MoCreatures.mocegg), (Achievement)null).initIndependentStat().registerStat();
	
	
	
	cook_omelette = new Achievement("achievement.cook_omelette", "cook_omelette", -7, -1, new ItemStack(MoCreatures.omelet), AchievementList.buildSword).registerStat();
	cook_turkey = new Achievement("achievement.cook_turkey", "cook_turkey", -7, 0, new ItemStack(MoCreatures.cookedTurkey), AchievementList.buildSword).registerStat();
	cook_ostrich = new Achievement("achievement.cook_ostrich", "cook_ostrich", -8, -1, new ItemStack(MoCreatures.ostrichcooked), AchievementList.buildSword).registerStat();
	cook_rat = new Achievement("achievement.cook_rat", "cook_rat", -9, -1, new ItemStack(MoCreatures.ratCooked), AchievementList.buildSword).registerStat();
	rat_burger = new Achievement("achievement.rat_burger", "rat_burger", -11, -1, new ItemStack(MoCreatures.ratBurger), cook_rat).registerStat();
	cook_crab = new Achievement("achievement.cook_crab", "cook_crab", -8, 0, new ItemStack(MoCreatures.crabcooked), AchievementList.buildSword).registerStat();
	cook_turtle = new Achievement("achievement.cook_turtle", "cook_turtle", -9, 0, new ItemStack(MoCreatures.turtlesoup), AchievementList.buildSword).registerStat();
	
	
	kill_wraith = new Achievement("achievement.kill_wraith", "kill_wraith", -7, -3, new ItemStack(MoCreatures.achievement_icon_kill_wraith), AchievementList.buildSword).registerStat();
	kill_ogre = new Achievement("achievement.kill_ogre", "kill_ogre", -7, -4, new ItemStack(MoCreatures.achievement_icon_kill_ogre), AchievementList.buildSword).registerStat();
	kill_werewolf = new Achievement("achievement.kill_werewolf", "kill_werewolf", -8, -3, new ItemStack(MoCreatures.achievement_icon_kill_werewolf), AchievementList.buildSword).registerStat();
	kill_big_golem = new Achievement("achievement.kill_big_golem", "kill_big_golem", -8, -4, new ItemStack(MoCreatures.achievement_icon_kill_big_golem), AchievementList.buildSword).setSpecial().registerStat();
	
	leonardo = new Achievement("achievement.leonardo", "leonardo", -7, -7, new ItemStack(MoCreatures.katana), AchievementList.buildSword).registerStat();
	raphael = new Achievement("achievement.raphael", "raphael", -7, -6, new ItemStack(MoCreatures.sai), AchievementList.buildSword).registerStat();
	donatello = new Achievement("achievement.donatello", "donatello", -8, -7, new ItemStack(MoCreatures.bo), AchievementList.buildSword).registerStat();
	michelangelo = new Achievement("achievement.michelangelo", "michelangelo", -8, -6, new ItemStack(MoCreatures.nunchaku), AchievementList.buildSword).registerStat();
	
	
	silver_sword = new Achievement("achievement.silver_sword", "silver_sword", -10, -7, new ItemStack(MoCreatures.silversword), AchievementList.buildSword).setSpecial().registerStat();
	
	
	
	get_fur = new Achievement("achievement.get_fur", "get_fur", 3, -5, new ItemStack(MoCreatures.fur), AchievementList.buildSword).registerStat(); 
	fur_armor = new Achievement("achievement.fur_armor", "fur_armor", 3, -7, new ItemStack(MoCreatures.helmetFur), get_fur).registerStat(); 
	
	get_hide = new Achievement("achievement.get_hide", "get_hide", 1, -5, new ItemStack(MoCreatures.animalHide), AchievementList.buildSword).registerStat();
	hide_armor = new Achievement("achievement.hide_armor", "hide_armor", 1, -7, new ItemStack(MoCreatures.helmetHide), get_hide).registerStat(); 
	
	get_reptile_hide = new Achievement("achievement.get_reptile_hide", "get_reptile_hide", -1, -5, new ItemStack(MoCreatures.hideCroc), AchievementList.buildSword).registerStat(); 
	reptile_armor = new Achievement("achievement.reptile_armor", "reptile_armor", -1, -7, new ItemStack(MoCreatures.helmetCroc), get_reptile_hide).registerStat(); 
	
	
	get_scorpian_material = new Achievement("achievement.get_scorpian_material", "get_scorpian_material", -3, -5, new ItemStack(MoCreatures.chitin), AchievementList.buildSword).registerStat();
	scorpian_sword = new Achievement("achievement.scorpian_sword", "scorpian_sword", -4, -6, new ItemStack(MoCreatures.scorpSwordDirt), get_scorpian_material).registerStat();
	scorpian_armor = new Achievement("achievement.scorpian_armor", "scorpian_armor", -3, -7, new ItemStack(MoCreatures.scorpHelmetDirt), get_scorpian_material).registerStat();
	
	
	AchievementPage.registerAchievementPage(new AchievementPage("Mo' Creatures", new Achievement[]{
					craft_saddle,
					
					tier2_horse,
					tier3_horse,
					tier4_horse,
					zebra,
					zorse,
					
					heart_undead,
					essence_undead,
					undead_horse,
					amulet_bone,
					
					ghost_horse,
					amulet_ghost,
					
					heart_fire,
					essence_fire,
					nightmare_horse,
					
					heart_darkness,
					essence_darkness,
					bat_horse,
					crystal_horse_armor,
					
					essence_light,
					pegasus,
					unicorn,
					
					amulet_sky,
					dark_pegasus,
					
					fairy_horse,
					amulet_fairy,
					
					wyvern_portal_staff,
					wyvern_egg,
					
					tame_elephant,
					mount_elephant,
					mammoth_platform,
					elephant_garment,
					elephant_tusks,
					elephant_chest,
					elephant_howdah,
					
					craft_medallion,
					tame_kitty,
					kitty_litter_box,
					kitty_litter,
					kitty_bed,
					pet_food,
					wool_ball,
					
					tame_big_cat,
					
					big_cat_claw,
					craft_whip,
					indiana,
					
					shark_tooth,
					fish_net,
					shark_sword,
					
					catch_fish_in_fish_bowl,
					
					tame_bird,
					feed_snake_with_live_mouse,
					tame_panda,
					tame_scorpian,
					ostrich_egg,
					
					pet_amulet,
					
					cook_omelette,
					cook_turkey,
					cook_ostrich,
					cook_rat,
					rat_burger,
					cook_crab,
					cook_turtle,
					
					kill_wraith,
					kill_ogre,
					kill_werewolf,
					kill_big_golem,
					
					leonardo,
					raphael,
					donatello,
					michelangelo,
					silver_sword,
					
					get_fur,
					fur_armor,
					
					get_hide,
					hide_armor,
					
					get_reptile_hide,
					reptile_armor,
					
					get_scorpian_material,
					scorpian_sword,
					scorpian_armor
	}));
}


}

