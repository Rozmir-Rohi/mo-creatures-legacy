package drzhark.mocreatures.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.client.model.MoCModelWere;
import drzhark.mocreatures.client.model.MoCModelWereHuman;
import drzhark.mocreatures.client.model.MoCModelWolf;
import drzhark.mocreatures.client.renderer.entity.MoCRenderWerewolfPlayerWitchery;
import drzhark.mocreatures.client.renderer.entity.MoCRenderWolfPlayerWitchery;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class MoCClientWitcheryPlayerWolfAndWerewolfReplacement {
	
	final Render renderPlayerWolf = new MoCRenderWolfPlayerWitchery(new MoCModelWolf(), 0.7F);
    final Render renderPlayerWerewolf = new MoCRenderWerewolfPlayerWitchery(new MoCModelWereHuman(), new MoCModelWere(), 0.7F);
    
    @SubscribeEvent
	public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
    {
    	if (MoCreatures.isWitcheryLoaded)
    	{
    		float modelYOffset = -1.625F;
    		
    		if (MoCreatures.proxy.replaceWitcheryPlayerWerewolf && MoCTools.isPlayerInWerewolfForm(event.entityPlayer) && !(event.entityPlayer.isPotionActive(Potion.invisibility)))
    		{
				renderPlayerWerewolf.doRender(event.entity, 0F, modelYOffset, 0F, 0F, 0.0625F);
    		}
    		
    		if (MoCreatures.proxy.replaceWitcheryPlayerWolf && MoCTools.isPlayerInWolfForm(event.entityPlayer) && !(event.entityPlayer.isPotionActive(Potion.invisibility)))
    		{
				renderPlayerWolf.doRender(event.entity, 0F, modelYOffset, 0F, 0F, 0.0625F);
    		}
    	}

	}

}
