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

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityBunny)par1Entity).getTexture();
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
        MoCEntityBunny entitybunny = (MoCEntityBunny) entityLiving;
        if (!entitybunny.getIsAdult())
        {
            stretch(entitybunny);
        }
        return entityLiving.ticksExisted + f;
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        rotBunny((MoCEntityBunny) entityLiving);
        if (entityLiving.ridingEntity != null)
        {
            //GL11.glTranslatef(0.0F, 1.3F, 0.0F);
        }
        else
        {
            GL11.glTranslatef(0.0F, 0.1F, 0.0F);
        }
    }

    protected void rotBunny(MoCEntityBunny entitybunny)
    {
        if (!entitybunny.onGround && (entitybunny.ridingEntity == null))
        {
            if (entitybunny.motionY > 0.5D)
            {
                GL11.glRotatef(35F, -1F, 0.0F, 0.0F);
            }
            else if (entitybunny.motionY < -0.5D)
            {
                GL11.glRotatef(-35F, -1F, 0.0F, 0.0F);
            }
            else
            {
                GL11.glRotatef((float) (entitybunny.motionY * 70D), -1F, 0.0F, 0.0F);
            }
        }
    }

    protected void stretch(MoCEntityBunny entitybunny)
    {
        float f = entitybunny.getMoCAge() * 0.01F;
        GL11.glScalef(f, f, f);
    }
}
