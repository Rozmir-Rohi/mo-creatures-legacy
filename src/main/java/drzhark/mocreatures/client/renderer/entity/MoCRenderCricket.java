package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.ambient.MoCEntityCricket;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderCricket extends MoCRenderMoC {

    public MoCRenderCricket(ModelBase modelBase)
    {
        super(modelBase, 0.0F);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float par2)
    {
        rotateCricket((MoCEntityCricket) entityLiving);
    }

    protected void rotateCricket(MoCEntityCricket entityfirefly)
    {
        if (!entityfirefly.onGround)
        {
            if (entityfirefly.motionY > 0.5D)
            {
                GL11.glRotatef(35F, -1F, 0.0F, 0.0F);
            }
            else if (entityfirefly.motionY < -0.5D)
            {
                GL11.glRotatef(-35F, -1F, 0.0F, 0.0F);
            }
            else
            {
                GL11.glRotatef((float) (entityfirefly.motionY * 70D), -1F, 0.0F, 0.0F);
            }
        }
    }

    @Override
	protected ResourceLocation getEntityTexture(Entity entity) {
        return ((MoCEntityCricket)entity).getTexture();
    }
}
