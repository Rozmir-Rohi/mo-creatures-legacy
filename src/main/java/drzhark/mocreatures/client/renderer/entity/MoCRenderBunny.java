package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.animal.MoCEntityBunny;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderBunny extends MoCRenderMoC {

    @Override
	protected ResourceLocation getEntityTexture(Entity entity) {
        return ((MoCEntityBunny)entity).getTexture();
    }

    public MoCRenderBunny(ModelBase modelBase, float f)
    {
        super(modelBase, f);
    }

    @Override
    public void doRender(EntityLiving entityLiving, double x, double y, double z, float rotationYaw, float rotationPitch)
    {
        MoCEntityBunny entityBunny = (MoCEntityBunny) entityLiving;
        super.doRender(entityBunny, x, y, z, rotationYaw, rotationPitch);
    }

    @Override
    protected float handleRotationFloat(EntityLivingBase entityLiving, float f)
    {
        MoCEntityBunny entityBunny = (MoCEntityBunny) entityLiving;
        if (!entityBunny.getIsAdult())
        {
            stretch(entityBunny);
        }
        return entityLiving.ticksExisted + f;
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        rotateBunny((MoCEntityBunny) entityLiving);
        if (entityLiving.ridingEntity != null)
        {
            //GL11.glTranslatef(0.0F, 1.3F, 0.0F);
        }
        else
        {
            GL11.glTranslatef(0.0F, 0.1F, 0.0F);
        }
    }

    protected void rotateBunny(MoCEntityBunny entityBunny)
    {
        if (!entityBunny.onGround && (entityBunny.ridingEntity == null))
        {
            if (entityBunny.motionY > 0.5D)
            {
                GL11.glRotatef(35F, -1F, 0.0F, 0.0F);
            }
            else if (entityBunny.motionY < -0.5D)
            {
                GL11.glRotatef(-35F, -1F, 0.0F, 0.0F);
            }
            else
            {
                GL11.glRotatef((float) (entityBunny.motionY * 70D), -1F, 0.0F, 0.0F);
            }
        }
    }

    protected void stretch(MoCEntityBunny entityBunny)
    {
        float f = entityBunny.getMoCAge() * 0.01F;
        GL11.glScalef(f, f, f);
    }
}
