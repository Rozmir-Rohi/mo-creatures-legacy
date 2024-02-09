package drzhark.mocreatures.client;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import drzhark.mocreatures.MoCEntityData;
import drzhark.mocreatures.MoCProxy;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.client.gui.helpers.MoCGUIEntityNamer;
import drzhark.mocreatures.client.model.MoCModelAnt;
import drzhark.mocreatures.client.model.MoCModelBear;
import drzhark.mocreatures.client.model.MoCModelBee;
import drzhark.mocreatures.client.model.MoCModelBigCat1;
import drzhark.mocreatures.client.model.MoCModelBigCat2;
import drzhark.mocreatures.client.model.MoCModelBird;
import drzhark.mocreatures.client.model.MoCModelBoar;
import drzhark.mocreatures.client.model.MoCModelBunny;
import drzhark.mocreatures.client.model.MoCModelButterfly;
import drzhark.mocreatures.client.model.MoCModelCrab;
import drzhark.mocreatures.client.model.MoCModelCricket;
import drzhark.mocreatures.client.model.MoCModelCrocodile;
import drzhark.mocreatures.client.model.MoCModelDeer;
import drzhark.mocreatures.client.model.MoCModelDolphin;
import drzhark.mocreatures.client.model.MoCModelDragonfly;
import drzhark.mocreatures.client.model.MoCModelDuck;
import drzhark.mocreatures.client.model.MoCModelEgg;
import drzhark.mocreatures.client.model.MoCModelElephant;
import drzhark.mocreatures.client.model.MoCModelFirefly;
import drzhark.mocreatures.client.model.MoCModelFishBowl;
import drzhark.mocreatures.client.model.MoCModelFishy;
import drzhark.mocreatures.client.model.MoCModelFly;
import drzhark.mocreatures.client.model.MoCModelFox;
import drzhark.mocreatures.client.model.MoCModelGoat;
import drzhark.mocreatures.client.model.MoCModelGolem;
import drzhark.mocreatures.client.model.MoCModelJellyFish;
import drzhark.mocreatures.client.model.MoCModelKitty;
import drzhark.mocreatures.client.model.MoCModelKittyBed;
import drzhark.mocreatures.client.model.MoCModelKittyBed2;
import drzhark.mocreatures.client.model.MoCModelKomodo;
import drzhark.mocreatures.client.model.MoCModelLitterBox;
import drzhark.mocreatures.client.model.MoCModelMaggot;
import drzhark.mocreatures.client.model.MoCModelMediumFish;
import drzhark.mocreatures.client.model.MoCModelMiniGolem;
import drzhark.mocreatures.client.model.MoCModelMole;
import drzhark.mocreatures.client.model.MoCModelMouse;
import drzhark.mocreatures.client.model.MoCModelNewHorse;
import drzhark.mocreatures.client.model.MoCModelNewHorseMob;
import drzhark.mocreatures.client.model.MoCModelOgre;
import drzhark.mocreatures.client.model.MoCModelOstrich;
import drzhark.mocreatures.client.model.MoCModelPetScorpion;
import drzhark.mocreatures.client.model.MoCModelRaccoon;
import drzhark.mocreatures.client.model.MoCModelRat;
import drzhark.mocreatures.client.model.MoCModelRay;
import drzhark.mocreatures.client.model.MoCModelRoach;
import drzhark.mocreatures.client.model.MoCModelScorpion;
import drzhark.mocreatures.client.model.MoCModelShark;
import drzhark.mocreatures.client.model.MoCModelSilverSkeleton;
import drzhark.mocreatures.client.model.MoCModelSmallFish;
import drzhark.mocreatures.client.model.MoCModelSnail;
import drzhark.mocreatures.client.model.MoCModelSnake;
import drzhark.mocreatures.client.model.MoCModelTurkey;
import drzhark.mocreatures.client.model.MoCModelTurtle;
import drzhark.mocreatures.client.model.MoCModelWere;
import drzhark.mocreatures.client.model.MoCModelWereHuman;
import drzhark.mocreatures.client.model.MoCModelWolf;
import drzhark.mocreatures.client.model.MoCModelWraith;
import drzhark.mocreatures.client.model.MoCModelWyvern;
import drzhark.mocreatures.client.renderer.entity.MoCRenderBear;
import drzhark.mocreatures.client.renderer.entity.MoCRenderBigCat;
import drzhark.mocreatures.client.renderer.entity.MoCRenderBird;
import drzhark.mocreatures.client.renderer.entity.MoCRenderBoar;
import drzhark.mocreatures.client.renderer.entity.MoCRenderBunny;
import drzhark.mocreatures.client.renderer.entity.MoCRenderButterfly;
import drzhark.mocreatures.client.renderer.entity.MoCRenderCricket;
import drzhark.mocreatures.client.renderer.entity.MoCRenderCrocodile;
import drzhark.mocreatures.client.renderer.entity.MoCRenderDeer;
import drzhark.mocreatures.client.renderer.entity.MoCRenderDolphin;
import drzhark.mocreatures.client.renderer.entity.MoCRenderEgg;
import drzhark.mocreatures.client.renderer.entity.MoCRenderFirefly;
import drzhark.mocreatures.client.renderer.entity.MoCRenderFishBowl;
import drzhark.mocreatures.client.renderer.entity.MoCRenderGoat;
import drzhark.mocreatures.client.renderer.entity.MoCRenderGolem;
import drzhark.mocreatures.client.renderer.entity.MoCRenderHellRat;
import drzhark.mocreatures.client.renderer.entity.MoCRenderHorseMob;
import drzhark.mocreatures.client.renderer.entity.MoCRenderInsect;
import drzhark.mocreatures.client.renderer.entity.MoCRenderKitty;
import drzhark.mocreatures.client.renderer.entity.MoCRenderKittyBed;
import drzhark.mocreatures.client.renderer.entity.MoCRenderLitterBox;
import drzhark.mocreatures.client.renderer.entity.MoCRenderMoC;
import drzhark.mocreatures.client.renderer.entity.MoCRenderMouse;
import drzhark.mocreatures.client.renderer.entity.MoCRenderNewHorse;
import drzhark.mocreatures.client.renderer.entity.MoCRenderOstrich;
import drzhark.mocreatures.client.renderer.entity.MoCRenderPetScorpion;
import drzhark.mocreatures.client.renderer.entity.MoCRenderPlatform;
import drzhark.mocreatures.client.renderer.entity.MoCRenderRat;
import drzhark.mocreatures.client.renderer.entity.MoCRenderScorpion;
import drzhark.mocreatures.client.renderer.entity.MoCRenderShark;
import drzhark.mocreatures.client.renderer.entity.MoCRenderSmallFish;
import drzhark.mocreatures.client.renderer.entity.MoCRenderSnake;
import drzhark.mocreatures.client.renderer.entity.MoCRenderThrowableBlockForGolem;
import drzhark.mocreatures.client.renderer.entity.MoCRenderTurtle;
import drzhark.mocreatures.client.renderer.entity.MoCRenderWWolf;
import drzhark.mocreatures.client.renderer.entity.MoCRenderWerewolf;
import drzhark.mocreatures.client.renderer.entity.MoCRenderWraith;
import drzhark.mocreatures.client.renderer.texture.MoCTextures;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.ambient.MoCEntityAnt;
import drzhark.mocreatures.entity.ambient.MoCEntityBee;
import drzhark.mocreatures.entity.ambient.MoCEntityButterfly;
import drzhark.mocreatures.entity.ambient.MoCEntityCrab;
import drzhark.mocreatures.entity.ambient.MoCEntityCricket;
import drzhark.mocreatures.entity.ambient.MoCEntityDragonfly;
import drzhark.mocreatures.entity.ambient.MoCEntityFirefly;
import drzhark.mocreatures.entity.ambient.MoCEntityFly;
import drzhark.mocreatures.entity.ambient.MoCEntityMaggot;
import drzhark.mocreatures.entity.ambient.MoCEntityRoach;
import drzhark.mocreatures.entity.ambient.MoCEntitySnail;
import drzhark.mocreatures.entity.animal.MoCEntityBear;
import drzhark.mocreatures.entity.animal.MoCEntityBigCat;
import drzhark.mocreatures.entity.animal.MoCEntityBird;
import drzhark.mocreatures.entity.animal.MoCEntityBoar;
import drzhark.mocreatures.entity.animal.MoCEntityBunny;
import drzhark.mocreatures.entity.animal.MoCEntityCrocodile;
import drzhark.mocreatures.entity.animal.MoCEntityDeer;
import drzhark.mocreatures.entity.animal.MoCEntityDuck;
import drzhark.mocreatures.entity.animal.MoCEntityElephant;
import drzhark.mocreatures.entity.animal.MoCEntityFox;
import drzhark.mocreatures.entity.animal.MoCEntityGoat;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import drzhark.mocreatures.entity.animal.MoCEntityKitty;
import drzhark.mocreatures.entity.animal.MoCEntityKomodo;
import drzhark.mocreatures.entity.animal.MoCEntityMole;
import drzhark.mocreatures.entity.animal.MoCEntityMouse;
import drzhark.mocreatures.entity.animal.MoCEntityOstrich;
import drzhark.mocreatures.entity.animal.MoCEntityPetScorpion;
import drzhark.mocreatures.entity.animal.MoCEntityRaccoon;
import drzhark.mocreatures.entity.animal.MoCEntitySnake;
import drzhark.mocreatures.entity.animal.MoCEntityTurkey;
import drzhark.mocreatures.entity.animal.MoCEntityTurtle;
import drzhark.mocreatures.entity.animal.MoCEntityWyvern;
import drzhark.mocreatures.entity.aquatic.MoCEntityDolphin;
import drzhark.mocreatures.entity.aquatic.MoCEntityFishy;
import drzhark.mocreatures.entity.aquatic.MoCEntityJellyFish;
import drzhark.mocreatures.entity.aquatic.MoCEntityMediumFish;
import drzhark.mocreatures.entity.aquatic.MoCEntityPiranha;
import drzhark.mocreatures.entity.aquatic.MoCEntityRay;
import drzhark.mocreatures.entity.aquatic.MoCEntityShark;
import drzhark.mocreatures.entity.aquatic.MoCEntitySmallFish;
import drzhark.mocreatures.entity.item.MoCEntityEgg;
import drzhark.mocreatures.entity.item.MoCEntityFishBowl;
import drzhark.mocreatures.entity.item.MoCEntityKittyBed;
import drzhark.mocreatures.entity.item.MoCEntityLitterBox;
import drzhark.mocreatures.entity.item.MoCEntityPlatform;
import drzhark.mocreatures.entity.item.MoCEntityThrowableBlockForGolem;
import drzhark.mocreatures.entity.monster.MoCEntityFlameWraith;
import drzhark.mocreatures.entity.monster.MoCEntityGolem;
import drzhark.mocreatures.entity.monster.MoCEntityHellRat;
import drzhark.mocreatures.entity.monster.MoCEntityHorseMob;
import drzhark.mocreatures.entity.monster.MoCEntityMiniGolem;
import drzhark.mocreatures.entity.monster.MoCEntityOgre;
import drzhark.mocreatures.entity.monster.MoCEntityRat;
import drzhark.mocreatures.entity.monster.MoCEntityScorpion;
import drzhark.mocreatures.entity.monster.MoCEntitySilverSkeleton;
import drzhark.mocreatures.entity.monster.MoCEntityWWolf;
import drzhark.mocreatures.entity.monster.MoCEntityWerewolf;
import drzhark.mocreatures.entity.monster.MoCEntityWraith;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;

public class MoCClientProxy extends MoCProxy {

    public static Minecraft mc = Minecraft.getMinecraft();
    public static MoCClientProxy instance;
    public static MoCTextures mocTextures = new MoCTextures();

    public MoCClientProxy()
    {
        instance = this;
    }

    @Override
    public void registerRenderers()
    {
    }

    @Override
    public void initTextures()
    {
        mocTextures.loadTextures();
    }

    @Override
    public ResourceLocation getTexture(String texture)
    {
        return mocTextures.getTexture(texture);
    }

    @Override
    public void registerRenderInformation()
    {
        // Register your custom renderers
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityBunny.class, new MoCRenderBunny(new MoCModelBunny(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityBird.class, new MoCRenderBird(new MoCModelBird(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityTurtle.class, new MoCRenderTurtle(new MoCModelTurtle(), 0.4F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityMouse.class, new MoCRenderMouse(new MoCModelMouse(), 0.1F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntitySnake.class, new MoCRenderSnake(new MoCModelSnake(), 0.0F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityTurkey.class, new MoCRenderMoC(new MoCModelTurkey(), 0.4F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityButterfly.class, new MoCRenderButterfly(new MoCModelButterfly()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityHorse.class, new MoCRenderNewHorse(new MoCModelNewHorse()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityHorseMob.class, new MoCRenderHorseMob(new MoCModelNewHorseMob()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityBoar.class, new MoCRenderBoar(new MoCModelBoar(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityBear.class, new MoCRenderBear(new MoCModelBear(), 0.7F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityDuck.class, new MoCRenderMoC(new MoCModelDuck(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityBigCat.class, new MoCRenderBigCat(new MoCModelBigCat2(), new MoCModelBigCat1(), 0.7F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityDeer.class, new MoCRenderDeer(new MoCModelDeer(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityWWolf.class, new MoCRenderWWolf(new MoCModelWolf(), 0.7F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityWraith.class, new MoCRenderWraith(new MoCModelWraith(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityFlameWraith.class, new MoCRenderWraith(new MoCModelWraith(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityWerewolf.class, new MoCRenderWerewolf(new MoCModelWereHuman(), new MoCModelWere(), 0.7F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityFox.class, new MoCRenderMoC(new MoCModelFox(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityShark.class, new MoCRenderShark(new MoCModelShark(), 0.6F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityDolphin.class, new MoCRenderDolphin(new MoCModelDolphin(), 0.6F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityFishy.class, new MoCRenderMoC(new MoCModelFishy(), 0.1F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityEgg.class, new MoCRenderEgg(new MoCModelEgg(), 0.0F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityKitty.class, new MoCRenderKitty(new MoCModelKitty(0.0F, 15F), 0.4F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityKittyBed.class, new MoCRenderKittyBed(new MoCModelKittyBed(), new MoCModelKittyBed2(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityLitterBox.class, new MoCRenderLitterBox(new MoCModelLitterBox(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityRat.class, new MoCRenderRat(new MoCModelRat(), 0.2F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityHellRat.class, new MoCRenderHellRat(new MoCModelRat(), 0.4F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityScorpion.class, new MoCRenderScorpion(new MoCModelScorpion(), 0.6F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityCrocodile.class, new MoCRenderCrocodile(new MoCModelCrocodile(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityRay.class, new MoCRenderMoC(new MoCModelRay(), 0.4F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityJellyFish.class, new MoCRenderMoC(new MoCModelJellyFish(), 0.1F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityGoat.class, new MoCRenderGoat(new MoCModelGoat(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityFishBowl.class, new MoCRenderFishBowl(new MoCModelFishBowl(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityOstrich.class, new MoCRenderOstrich(new MoCModelOstrich(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityBee.class, new MoCRenderInsect(new MoCModelBee()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityFly.class, new MoCRenderInsect(new MoCModelFly()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityDragonfly.class, new MoCRenderInsect(new MoCModelDragonfly()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityFirefly.class, new MoCRenderFirefly(new MoCModelFirefly()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityCricket.class, new MoCRenderCricket(new MoCModelCricket()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntitySnail.class, new MoCRenderMoC(new MoCModelSnail(), 0.0F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityGolem.class, new MoCRenderGolem(new MoCModelGolem(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityThrowableBlockForGolem.class, new MoCRenderThrowableBlockForGolem());
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityPetScorpion.class, new MoCRenderPetScorpion(new MoCModelPetScorpion(), 0.6F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityPlatform.class, new MoCRenderPlatform());
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityElephant.class, new MoCRenderMoC(new MoCModelElephant(), 0.7F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityKomodo.class, new MoCRenderMoC(new MoCModelKomodo(), 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityWyvern.class, new MoCRenderMoC(new MoCModelWyvern(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityOgre.class, new MoCRenderMoC(new MoCModelOgre(), 0.6F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityRoach.class, new MoCRenderInsect(new MoCModelRoach()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityMaggot.class, new MoCRenderMoC(new MoCModelMaggot(), 0F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityCrab.class, new MoCRenderMoC(new MoCModelCrab(), 0.2F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityRaccoon.class, new MoCRenderMoC(new MoCModelRaccoon(), 0.4F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityMiniGolem.class, new MoCRenderMoC(new MoCModelMiniGolem(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntitySilverSkeleton.class, new MoCRenderMoC(new MoCModelSilverSkeleton(), 0.6F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityAnt.class, new MoCRenderInsect(new MoCModelAnt()));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityMediumFish.class, new MoCRenderMoC(new MoCModelMediumFish(), 0.2F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntitySmallFish.class, new MoCRenderSmallFish(new MoCModelSmallFish(), 0.1F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityPiranha.class, new MoCRenderMoC(new MoCModelSmallFish(), 0.1F));
        RenderingRegistry.registerEntityRenderingHandler(MoCEntityMole.class, new MoCRenderMoC(new MoCModelMole(), 0.3F));
        
    }

    @Override
    //public EntityClientPlayerMP getPlayer()
    public EntityPlayer getPlayer()
    {
        return mc.thePlayer;
    }

    /**
     * Sets the name client side. Name is synchronized with datawatchers
     * 
     * @param player
     * @param mocAnimal
     */
    @Override
    public void setName(EntityPlayer player, IMoCEntity mocAnimal)
    {
        mc.displayGuiScreen(new MoCGUIEntityNamer(mocAnimal, mocAnimal.getName()));

    }

    

    @Override
    public void UndeadFX(Entity entity)
    {
        //if (!((Boolean) MoCreatures.particleFX.get()).booleanValue()) return;
        int densityInt = (MoCreatures.proxy.getParticleFX());
        if (densityInt == 0) { return; }

        int i = (densityInt / 2) * (entity.worldObj.rand.nextInt(2) + 1);
        if (i == 0)
        {
            i = 1;
        }
        if (i > 10)
        {
            i = 10;
        }
        for (int x = 0; x < i; x++)
        {
            MoCEntityFXUndead FXUndead = new MoCEntityFXUndead(entity.worldObj, entity.posX, entity.posY + (double) (entity.worldObj.rand.nextFloat() * entity.height), entity.posZ);
            mc.effectRenderer.addEffect(FXUndead);

        }
    }

    @Override
    public void StarFX(MoCEntityHorse entity)
    {
        int densityInt = MoCreatures.proxy.getParticleFX();
        if (densityInt == 0) { return; }

        if ((entity.getType() >= 50 && entity.getType() < 60) || entity.getType() == 36)
        {

            float fRed = entity.colorFX(1, entity.getType());
            float fGreen = entity.colorFX(2, entity.getType());
            float fBlue = entity.colorFX(3, entity.getType());

            int i = densityInt * entity.worldObj.rand.nextInt(2);// + 2;
            for (int x = 0; x < i; x++)
            {
                MoCEntityFXStar FXStar = new MoCEntityFXStar(mc.theWorld, entity.posX, entity.posY + (double) (entity.worldObj.rand.nextFloat() * entity.height), entity.posZ, fRed, fGreen, fBlue);
                mc.effectRenderer.addEffect(FXStar);

            }

        }
    }

    @Override
    public void LavaFX(Entity entity)
    {
        int densityInt = (MoCreatures.proxy.getParticleFX());
        if (densityInt == 0) { return; }
        double var2 = entity.worldObj.rand.nextGaussian() * 0.02D;
        double var4 = entity.worldObj.rand.nextGaussian() * 0.02D;
        double var6 = entity.worldObj.rand.nextGaussian() * 0.02D;
        mc.theWorld.spawnParticle("lava", entity.posX + (double) (entity.worldObj.rand.nextFloat() * entity.width) - (double) entity.width, entity.posY + 0.5D + (double) (entity.worldObj.rand.nextFloat() * entity.height), entity.posZ + (double) (entity.worldObj.rand.nextFloat() * entity.width) - (double) entity.width, var2, var4, var6);

    }

    @Override
    public void VanishFX(MoCEntityHorse entity)
    {
        int densityInt = (MoCreatures.proxy.getParticleFX());
        if (densityInt == 0) { return;
        }

        for (int var6 = 0; var6 < densityInt * 8; ++var6)
        {
            double newPosX = ((float) entity.posX + entity.worldObj.rand.nextFloat());
            double newPosY = 0.7D + ((float) entity.posY + entity.worldObj.rand.nextFloat());
            double newPosZ = ((float) entity.posZ + entity.worldObj.rand.nextFloat());
            int var19 = entity.worldObj.rand.nextInt(2) * 2 - 1;
            double speedY = ((double) entity.worldObj.rand.nextFloat() - 0.5D) * 0.5D;
            double speedX = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);
            double speedZ = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);

            MoCEntityFXVanish FXVanish = new MoCEntityFXVanish(entity.worldObj, newPosX, newPosY, newPosZ, speedX, speedY, speedZ, entity.colorFX(1, entity.getType()), entity.colorFX(2, entity.getType()), entity.colorFX(3, entity.getType()), false);
            mc.effectRenderer.addEffect(FXVanish);
        }
    }

    @Override
    public void MaterializeFX(MoCEntityHorse entity)
    {
        int densityInt = (MoCreatures.proxy.getParticleFX());
        if (densityInt == 0) { return; }

        for (int var6 = 0; var6 < (densityInt * 50); ++var6)
        {
            double newPosX = ((float) entity.posX + entity.worldObj.rand.nextFloat());
            double newPosY = 0.7D + ((float) entity.posY + entity.worldObj.rand.nextFloat());
            double newPosZ = ((float) entity.posZ + entity.worldObj.rand.nextFloat());
            int var19 = entity.worldObj.rand.nextInt(2) * 2 - 1;
            double speedY = ((double) entity.worldObj.rand.nextFloat() - 0.5D) * 0.5D;
            double speedX = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);
            double speedZ = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);

            MoCEntityFXVanish FXVanish = new MoCEntityFXVanish(mc.theWorld, newPosX, newPosY, newPosZ, speedX, speedY, speedZ, entity.colorFX(1, entity.getType()), entity.colorFX(2, entity.getType()), entity.colorFX(3, entity.getType()), true);
            mc.effectRenderer.addEffect(FXVanish);
        }
        
    }

    @Override
    public void VacuumFX(MoCEntityGolem entity)
    {
        int densityInt = (MoCreatures.proxy.getParticleFX());
        if (densityInt == 0) { return; }

        for (int var1 = 0; var1 < 2; ++var1)
        {
            double newPosX = entity.posX - (1.5 * Math.cos((MoCTools.realAngle(entity.rotationYaw - 90F)) / 57.29578F));
            double newPosZ = entity.posZ - (1.5 * Math.sin((MoCTools.realAngle(entity.rotationYaw - 90F)) / 57.29578F));
            double newPosY = entity.posY + ((double) entity.height - 0.8D - (double) entity.getAdjustedYOffset() * 1.8);// + (entity.worldObj.rand.nextDouble() * ((double) entity.height - (double) entity.getAdjustedYOffset() * 2));
            //adjustedYOffset from 0 (tallest) to 1.45 (on the ground)
            //height = 4F

            double speedX = (entity.worldObj.rand.nextDouble() - 0.5D) * 4.0D;
            double speedY = -entity.worldObj.rand.nextDouble();
            double speedZ = (entity.worldObj.rand.nextDouble() - 0.5D) * 4.0D;
            MoCEntityFXVacuum FXVacuum = new MoCEntityFXVacuum(mc.theWorld, newPosX, newPosY, newPosZ, speedX, speedY, speedZ, entity.colorFX(1), entity.colorFX(2), entity.colorFX(3), 146);
            mc.effectRenderer.addEffect(FXVacuum);
        }
    }

     
    @Override
    public void hammerFX(EntityPlayer entity)
    {
        int densityInt = (MoCreatures.proxy.getParticleFX());
        if (densityInt == 0) { return; }

        for (int var6 = 0; var6 < (densityInt * 10); ++var6)
        {
            double newPosX = ((float) entity.posX + entity.worldObj.rand.nextFloat());
            double newPosY = 0.3D + ((float) entity.posY + entity.worldObj.rand.nextFloat());
            double newPosZ = ((float) entity.posZ + entity.worldObj.rand.nextFloat());
            int var19 = entity.worldObj.rand.nextInt(2) * 2 - 1;
            double speedY = ((double) entity.worldObj.rand.nextFloat() - 0.5D) * 0.5D;
            double speedX = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);
            double speedZ = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);

            EntitySpellParticleFX hammerFX = new EntitySpellParticleFX(mc.theWorld, newPosX, newPosY, newPosZ, speedX, speedY, speedZ);
            ((EntitySpellParticleFX)hammerFX).setBaseSpellTextureIndex(144);
            ((EntityFX)hammerFX).setRBGColorF(74F/256F, 145F/256F, 71F/256F);
            mc.effectRenderer.addEffect(hammerFX);
        }
        
    }
    
    
    @Override
    public void teleportFX(EntityPlayer entity)
    {
        int densityInt = (MoCreatures.proxy.getParticleFX());
        if (densityInt == 0) { return; }

        for (int var6 = 0; var6 < (densityInt * 50); ++var6)
        {
            double newPosX = ((float) entity.posX + entity.worldObj.rand.nextFloat());
            double newPosY = 0.7D + ((float) entity.posY + entity.worldObj.rand.nextFloat());
            double newPosZ = ((float) entity.posZ + entity.worldObj.rand.nextFloat());
            int var19 = entity.worldObj.rand.nextInt(2) * 2 - 1;
            double speedY = ((double) entity.worldObj.rand.nextFloat() - 0.5D) * 0.5D;
            double speedX = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);
            double speedZ = (double) (entity.worldObj.rand.nextFloat() * 2.0F * (float) var19);

            MoCEntityFXVanish hammerFX = new MoCEntityFXVanish(mc.theWorld, newPosX, newPosY, newPosZ, speedX, speedY, speedZ, 189F/256F, 110F/256F, 229F/256F, true);
            mc.effectRenderer.addEffect(hammerFX);
        }
        
    }
    @Override
    public int getProxyMode()
    {
        return 2;
    }


    private static final String BUTTON_GENERAL_SETTINGS = "General Settings";
    private static final String BUTTON_ID_SETTINGS = "ID Settings";
    private static final String BUTTON_CREATURES = "Creatures";
    private static final String BUTTON_CREATURE_GENERAL_SETTINGS = "Creature General Settings";
    private static final String BUTTON_CREATURE_SPAWN_SETTINGS = "Creature Spawn Settings";
    private static final String BUTTON_MONSTER_GENERAL_SETTINGS = "Monster General Settings";
    private static final String BUTTON_MONSTER_SPAWN_SETTINGS = "Monster Spawn Settings";
    private static final String BUTTON_WATERMOB_GENERAL_SETTINGS = "Water Mob General Settings";
    private static final String BUTTON_WATERMOB_SPAWN_SETTINGS = "Water Mob Spawn Settings";
    private static final String BUTTON_AMBIENT_SPAWN_SETTINGS = "Ambient Spawn Settings";
    private static final String BUTTON_OWNERSHIP_SETTINGS = "Ownership Settings";
    private static final String BUTTON_DEFAULTS = "Reset to Defaults";
    private static final String MOC_SCREEN_TITLE = "DrZhark's Mo'Creatures" ;

    public static final List<String> entityTypes = Arrays.asList("CREATURE", "MONSTER", "WATERCREATURE", "AMBIENT");

    public MoCEntityData currentSelectedEntity;

    @Override
    public void ConfigInit(FMLPreInitializationEvent event) {
        super.ConfigInit(event);
    }

    @Override
    public int getParticleFX()
    {
        return particleFX;
    }

    public boolean getDisplayPetName()
    {
        return displayPetName;
    }

    public boolean getDisplayPetIcons()
    {
        return displayPetIcons;
    }

    public boolean getDisplayPetHealth(EntityLiving entityLiving)
    {
    	if (displayPetHealth == true)
    	{
	        if (entityLiving.getHealth() == entityLiving.getMaxHealth())
	        {
	        	return false;
	        }
	        else
	        {
	        	return true;
	        }
    	}
    	else {return false;}
    }

    @Override
    public boolean getAnimateTextures()
    {
        return animateTextures;
    }

    @Override
    public void printMessageToPlayer(String msg)
    {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation(msg));
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}