package drzhark.mocreatures.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;

@SideOnly(Side.CLIENT)
public class MoCModelWerewolfHuman extends ModelBiped {

    public MoCModelWerewolfHuman()
    {
        super(0.0F, 0.0F, 64, 32);
    }
}
