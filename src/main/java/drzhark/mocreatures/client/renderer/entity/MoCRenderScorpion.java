package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.client.MoCClientProxy;
import drzhark.mocreatures.client.model.MoCModelScorpion;
import drzhark.mocreatures.entity.monster.MoCEntityScorpion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderScorpion extends MoCRenderMoC {

    public MoCRenderScorpion(MoCModelScorpion modelBase, float f)
    {
        super(modelBase, f);
    }

    @Override
    public void doRender(EntityLiving entityLiving, double x, double y, double z, float rotationYaw, float rotationPitch)
    {
        MoCEntityScorpion entityScorpion = (MoCEntityScorpion) entityLiving;
        super.doRender(entityScorpion, x, y, z, rotationYaw, rotationPitch);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        MoCEntityScorpion entityScorpion = (MoCEntityScorpion) entityLiving;

        if (entityScorpion.climbing())
        {
            rotateAnimal(entityScorpion);
        }

        if (!entityScorpion.getIsAdult())
        {
            stretch(entityScorpion);
            if (entityScorpion.getIsPicked())
            {
                upsideDown(entityScorpion);
            }
        }
        else
        {
            adjustHeight(entityScorpion);
        }
    }

    protected void upsideDown(EntityLiving entityLiving)
    {
        GL11.glRotatef(-90F, -1F, 0.0F, 0.0F);

        if (entityLiving.ridingEntity == MoCClientProxy.mc.thePlayer)
        {
            GL11.glTranslatef(-0.55F, -1.9F, -0.7F);

        }
        else
        {
            GL11.glTranslatef(-1.555F, -0.5F, -0.5F);

        }

    }

    protected void adjustHeight(EntityLiving entityLiving)
    {
        GL11.glTranslatef(0.0F, -0.1F, 0.0F);
    }

    protected void rotateAnimal(EntityLiving entityLiving)
    {
        GL11.glRotatef(90F, -1F, 0.0F, 0.0F);
    }

    protected void stretch(MoCEntityScorpion entityScorpion)
    {

        float f = 1.1F;
        if (!entityScorpion.getIsAdult())
        {
            f = entityScorpion.getMoCAge() * 0.01F;
        }
        GL11.glScalef(f, f, f);
    }

    @Override
	protected ResourceLocation getEntityTexture(Entity entity) {
        return ((MoCEntityScorpion)entity).getTexture();
    }
}
