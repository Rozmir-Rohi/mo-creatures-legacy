package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.monster.MoCEntityRat;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderRat extends RenderLiving {

    public MoCRenderRat(ModelBase modelBase, float f)
    {
        super(modelBase, f);
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d1, double d2, float f, float f1)
    {
        MoCEntityRat entityrat = (MoCEntityRat) entityLiving;
        super.doRender(entityrat, d, d1, d2, f, f1);
    }

    @Override
    protected float handleRotationFloat(EntityLivingBase entityLiving, float f)
    {
        stretch(entityLiving);
        return entityLiving.ticksExisted + f;
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        MoCEntityRat entityrat = (MoCEntityRat) entityLiving;
        if (entityrat.climbing())
        {
            rotateAnimal(entityLiving);
        }
    }

    protected void rotateAnimal(EntityLivingBase entityLiving)
    {
        GL11.glRotatef(90F, -1F, 0.0F, 0.0F);
    }

    protected void stretch(EntityLivingBase entityLiving)
    {
        float f = 0.8F;
        GL11.glScalef(f, f, f);
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityRat)par1Entity).getTexture();
    }
}
