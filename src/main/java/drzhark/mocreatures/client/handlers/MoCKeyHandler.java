package drzhark.mocreatures.client.handlers;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import drzhark.mocreatures.client.MoCClientProxy;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageEntityDive;
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
        if (entityPlayer == null || entityPlayer.ridingEntity == null) return;
        if (FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().getChatOpen()) return; // if chatting return
        boolean kbJump = Keyboard.isKeyDown(MoCClientProxy.mc.gameSettings.keyBindJump.getKeyCode());
        boolean kbDive = Keyboard.isKeyDown(diveBinding.getKeyCode());
        boolean isJumpKeyDown = Keyboard.isKeyDown(MoCClientProxy.mc.gameSettings.keyBindJump.getKeyCode());
        //boolean kbDismount = kb.keyDescription.equals("MoCreatures Dismount");

        /**
         * this avoids double jumping
         */
        if (kbJump && entityPlayer != null && entityPlayer.ridingEntity != null && entityPlayer.ridingEntity instanceof IMoCEntity)
        {
            // keyCount = 0;
            // jump code needs to be executed client/server simultaneously to take
            ((IMoCEntity) entityPlayer.ridingEntity).makeEntityJump();
            MoCMessageHandler.INSTANCE.sendToServer(new MoCMessageEntityJump());
        }

        if (kbDive && entityPlayer != null && entityPlayer.ridingEntity != null && entityPlayer.ridingEntity instanceof IMoCEntity)
        {
          //  keyCount = 0;
            // jump code needs to be executed client/server simultaneously to take
            ((IMoCEntity) entityPlayer.ridingEntity).makeEntityDive();
            MoCMessageHandler.INSTANCE.sendToServer(new MoCMessageEntityDive());
        }
    }
}