package drzhark.mocreatures;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import drzhark.mocreatures.entity.IMoCEntity;
import net.minecraft.entity.player.EntityPlayer;

public class MoCPlayerTracker
{
    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event) {
        EntityPlayer player = event.player;
        if (player.ridingEntity != null && (player.ridingEntity instanceof IMoCEntity))
        {
            IMoCEntity mocEntity = (IMoCEntity)player.ridingEntity;
            mocEntity.riderIsDisconnecting(true);// = true;
        }
    }
}