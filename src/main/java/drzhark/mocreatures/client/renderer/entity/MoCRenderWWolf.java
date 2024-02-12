package drzhark.mocreatures.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.monster.MoCEntityWWolf;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderWWolf extends RenderLiving {

    public MoCRenderWWolf(ModelBase modelBase, float f)
    {
        super(modelBase, f);
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityWWolf)par1Entity).getTexture();
    }
}
