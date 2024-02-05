package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.animal.MoCEntityMouse;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderMouse extends MoCRenderMoC {

    public MoCRenderMouse(ModelBase modelbase, float f)
    {
        super(modelbase, f);
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d1, double d2, float f, float f1)
    {
        MoCEntityMouse entitymouse = (MoCEntityMouse) entityLiving;
        super.doRender(entitymouse, d, d1, d2, f, f1);
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
        MoCEntityMouse entitymouse = (MoCEntityMouse) entityLiving;
        if (entitymouse.upsideDown())
        {
            upsideDown(entityLiving);

        }
        if (entitymouse.climbing())
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
        float f = 0.6F;
        GL11.glScalef(f, f, f);
    }

    protected void upsideDown(EntityLivingBase entityLiving)
    {
        GL11.glRotatef(-90F, -1F, 0.0F, 0.0F);
        //GL11.glTranslatef(-0.55F, 0F, -0.7F);
        GL11.glTranslatef(-0.55F, 0F, 0F);
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityMouse)par1Entity).getTexture();
    }
}
