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
    public void doRender(EntityLiving entityLiving, double d, double d1, double d2, float f, float f1)
    {
        MoCEntityScorpion entityscorpion = (MoCEntityScorpion) entityLiving;
        super.doRender(entityscorpion, d, d1, d2, f, f1);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        MoCEntityScorpion entityscorpion = (MoCEntityScorpion) entityLiving;

        if (entityscorpion.climbing())
        {
            rotateAnimal(entityscorpion);
        }

        if (!entityscorpion.getIsAdult())
        {
            stretch(entityscorpion);
            if (entityscorpion.getIsPicked())
            {
                upsideDown(entityscorpion);
            }
        }
        else
        {
            adjustHeight(entityscorpion);
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

    protected void stretch(MoCEntityScorpion entityscorpion)
    {

        float f = 1.1F;
        if (!entityscorpion.getIsAdult())
        {
            f = entityscorpion.getMoCAge() * 0.01F;
        }
        GL11.glScalef(f, f, f);
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityScorpion)par1Entity).getTexture();
    }
}
