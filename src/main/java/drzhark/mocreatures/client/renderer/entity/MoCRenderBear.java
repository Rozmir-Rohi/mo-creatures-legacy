package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.client.model.MoCModelBear;
import drzhark.mocreatures.entity.animal.MoCEntityBear;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderBear extends MoCRenderMoC {

    public MoCRenderBear(MoCModelBear modelbase, float f)
    {
        super(modelbase, f);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityliving, float f)
    {
        MoCEntityBear entitybear = (MoCEntityBear) entityliving;
        stretch(entitybear);
        super.preRenderCallback(entityliving, f);

    }

    protected void stretch(MoCEntityBear entitybear)
    {
        float sizeFactor = entitybear.getMoCAge() * 0.01F;
        if (entitybear.getIsAdult())
        {
            sizeFactor = 1.0F;
        }
        sizeFactor *= entitybear.getBearSize();
        GL11.glScalef(sizeFactor, sizeFactor, sizeFactor);
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityBear)par1Entity).getTexture();
    }
}
