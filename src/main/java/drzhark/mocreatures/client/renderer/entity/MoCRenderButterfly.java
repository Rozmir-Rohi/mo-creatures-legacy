
package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.ambient.MoCEntityButterfly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderButterfly extends MoCRenderInsect {

    public MoCRenderButterfly(ModelBase modelBase)
    {
        super(modelBase);

    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float par2)
    {
        MoCEntityButterfly butterfly = (MoCEntityButterfly) entityLiving;
        if (butterfly.isOnAir() || !butterfly.onGround)
        {
            adjustHeight(butterfly, butterfly.renderHeightAdjustmentWhenFlying());
        }
        if (butterfly.climbing())
        {
            rotateAnimal(butterfly);
        }
        stretch(butterfly);
    }

    protected void adjustHeight(EntityLiving entityLiving, float fHeight)
    {
        GL11.glTranslatef(0.0F, fHeight, 0.0F);
    }

    @Override
	protected ResourceLocation getEntityTexture(Entity entity) {
        return ((MoCEntityButterfly)entity).getTexture();
    }
}
