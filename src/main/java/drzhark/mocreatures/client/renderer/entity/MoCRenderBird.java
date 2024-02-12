package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.animal.MoCEntityBird;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderBird extends MoCRenderMoC {

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityBird)par1Entity).getTexture();
    }

    public MoCRenderBird(ModelBase modelBase, float f)
    {
        super(modelBase, f);
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d1, double d2, float f, float f1)
    {

        /*if (!worldObj.multiplayerWorld)
        MoCEntityBird entitybird = (MoCEntityBird)entityLiving;
        if(!entitybird.getTypeChosen())
        {
            entitybird.chooseType();
        }*/
        super.doRender(entityLiving, d, d1, d2, f, f1);

    }

    @Override
    protected float handleRotationFloat(EntityLivingBase entityLiving, float f)
    {
        MoCEntityBird entitybird = (MoCEntityBird) entityLiving;
        float f1 = entitybird.wingE + ((entitybird.wingB - entitybird.wingE) * f);
        float f2 = entitybird.wingD + ((entitybird.wingC - entitybird.wingD) * f);
        return (MathHelper.sin(f1) + 1.0F) * f2;
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        if (!entityLiving.worldObj.isRemote && (entityLiving.ridingEntity != null))
        {

            GL11.glTranslatef(0.0F, 1.3F, 0.0F);

        }
    }
}
