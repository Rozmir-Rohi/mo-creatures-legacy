package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.client.model.MoCModelCrocodile;
import drzhark.mocreatures.entity.animal.MoCEntityCrocodile;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderCrocodile extends RenderLiving {

    public MoCRenderCrocodile(MoCModelCrocodile modelBase, float f)
    {
        super(modelBase, f);
        modelCrocodile = modelBase;
    }

    @Override
	protected ResourceLocation getEntityTexture(Entity entity) {
        return ((MoCEntityCrocodile)entity).getTexture();
    }

    @Override
    public void doRender(EntityLiving entityLiving, double x, double y, double z, float rotationYaw, float rotationPitch)
    {

        MoCEntityCrocodile entityCrocodile = (MoCEntityCrocodile) entityLiving;
        super.doRender(entityCrocodile, x, y, z, rotationYaw, rotationPitch);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        MoCEntityCrocodile entityCrocodile = (MoCEntityCrocodile) entityLiving;
        modelCrocodile.biteProgress = entityCrocodile.biteProgress;
        modelCrocodile.swimming = entityCrocodile.isSwimming();
        modelCrocodile.resting = entityCrocodile.getIsResting();
       
        if (entityCrocodile.isSpinning() && entityCrocodile.riddenByEntity instanceof EntityLivingBase)
        {
            spinCrocodileAndTheEntityInsideItsMouth(entityCrocodile, (EntityLivingBase) entityCrocodile.riddenByEntity);
        }
        
        stretch(entityCrocodile);
        if (entityCrocodile.getIsResting())
        {
            if (!entityCrocodile.isInsideOfMaterial(Material.water))
            {
                adjustHeight(entityCrocodile, 0.2F);
            }
            else
            {
                //adjustHeight(entityCrocodile, 0.1F);
            }

        }
    }

    protected void adjustHeight(EntityLiving entityLiving, float FHeight)
    {
        GL11.glTranslatef(0.0F, FHeight, 0.0F);
    }

    protected void spinCrocodileAndTheEntityInsideItsMouth(MoCEntityCrocodile entityCrocodile, EntityLivingBase entityLivingBasePreyInsideMouth)
    {
        int intSpin = entityCrocodile.spinInt;

        int intEndSpin = intSpin;
        if (intSpin >= 20)
        {
            intEndSpin = (20 - (intSpin - 20));
        }
        if (intEndSpin == 0)
        {
            intEndSpin = 1;
        }
        
        float rotationFactor = (((intEndSpin) - 1.0F) / 20F) * 1.6F;
        rotationFactor = MathHelper.sqrt_float(rotationFactor);
        
        if (rotationFactor > 1.0F)
        {
            rotationFactor = 1.0F;
        }
        GL11.glRotatef(rotationFactor * 90F, 0.0F, 0.0F, 1.0F);

        if (entityLivingBasePreyInsideMouth != null)
        {
            entityLivingBasePreyInsideMouth.deathTime = intEndSpin;  //this rotates the whole model of the prey by using deathTime
        }
    }
    

    protected void stretch(MoCEntityCrocodile entityCrocodile)
    {
        float f = entityCrocodile.getMoCAge() * 0.01F;
        GL11.glScalef(f, f, f);
    }

    public MoCModelCrocodile modelCrocodile;

}
