package drzhark.mocreatures.client.handlers;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import drzhark.mocreatures.client.MoCClientProxy;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageEntityDive;
import drzhark.mocreatures.network.message.MoCMessageEntityHorseJumpKeyDown;
import drzhark.mocreatures.network.message.MoCMessageEntityHorseJumpKeyUp;
import drzhark.mocreatures.network.message.MoCMessageEntityJump;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

public class MoCKeyHandler {
    int keyCount;
    static KeyBinding diveBinding = new KeyBinding(StatCollector.translateToLocal("keyBind.MoCreatures.dive"), Keyboard.KEY_F, "key.categories.movement");

    public MoCKeyHandler()
    {
        //the first value is an array of KeyBindings, the second is whether or not the call
        //keyDown should repeat as long as the key is down
        cpw.mods.fml.client.registry.ClientRegistry.registerKeyBinding(diveBinding);
    }

    
    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event)
    {
        Keyboard.enableRepeatEvents(true); // allow holding down key. Fixes flying
        EntityPlayer entityPlayer = MoCClientProxy.mc.thePlayer;
        
        if (entityPlayer == null || entityPlayer.ridingEntity == null) {return;}
        
        if (FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().getChatOpen()) {return;} // if chatting return
        
        boolean isJumpKeyDown = Keyboard.isKeyDown(MoCClientProxy.mc.gameSettings.keyBindJump.getKeyCode());
        boolean isDiveKeyDown = Keyboard.isKeyDown(diveBinding.getKeyCode());

        /**
         * this avoids double jumping
         */
        if (
        		entityPlayer != null
        		&& entityPlayer.ridingEntity != null
        		&& entityPlayer.ridingEntity instanceof MoCEntityHorse
        		&& !(((MoCEntityHorse) entityPlayer.ridingEntity).isFlyer())
        	)
        {
        	
        	if (isJumpKeyDown && !((MoCEntityHorse) entityPlayer.ridingEntity).getIsJumpKeyDown())
        	{
        		 // jump code needs to be executed client/server simultaneously to take
        		((MoCEntityHorse) entityPlayer.ridingEntity).setJumpKeyDown(true);
        		MoCMessageHandler.INSTANCE.sendToServer(new MoCMessageEntityHorseJumpKeyDown());
        	}
        	
        	if (!isJumpKeyDown && ((MoCEntityHorse) entityPlayer.ridingEntity).getIsJumpKeyDown())
        	{
        		 // jump code needs to be executed client/server simultaneously to take
        		((MoCEntityHorse) entityPlayer.ridingEntity).setJumpKeyDown(false);
        		MoCMessageHandler.INSTANCE.sendToServer(new MoCMessageEntityHorseJumpKeyUp());
        	}
        }
        	
        else if
        (
        		isJumpKeyDown
        		&& entityPlayer != null
        		&& entityPlayer.ridingEntity != null
        		&& entityPlayer.ridingEntity instanceof IMoCEntity
        		&& !(
        				entityPlayer.ridingEntity instanceof MoCEntityHorse
                		&& !(((MoCEntityHorse) entityPlayer.ridingEntity).isFlyer())
        			)
        	)
        {
    		// keyCount = 0;
            // jump code needs to be executed client/server simultaneously to take
            ((IMoCEntity) entityPlayer.ridingEntity).makeEntityJump();
            MoCMessageHandler.INSTANCE.sendToServer(new MoCMessageEntityJump());
    	}

        if (
        		isDiveKeyDown
        		&& entityPlayer != null
        		&& entityPlayer.ridingEntity != null
        		&& entityPlayer.ridingEntity instanceof IMoCEntity
        	)
        {
          //  keyCount = 0;
            // jump code needs to be executed client/server simultaneously to take
            ((IMoCEntity) entityPlayer.ridingEntity).makeEntityDive();
            MoCMessageHandler.INSTANCE.sendToServer(new MoCMessageEntityDive());
        }
    }
}