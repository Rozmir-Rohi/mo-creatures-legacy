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

    public MoCRenderMouse(ModelBase modelBase, float f)
    {
        super(modelBase, f);
    }

    @Override
    public void doRender(EntityLiving entityLiving, double x, double y, double z, float rotationYaw, float rotationPitch)
    {
        MoCEntityMouse entityMouse = (MoCEntityMouse) entityLiving;
        super.doRender(entityMouse, x, y, z, rotationYaw, rotationPitch);
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
        MoCEntityMouse entityMouse = (MoCEntityMouse) entityLiving;
        if (entityMouse.upsideDown())
        {
            upsideDown(entityLiving);

        }
        if (entityMouse.climbing())
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

    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityMouse)par1Entity).getTexture();
    }
}
