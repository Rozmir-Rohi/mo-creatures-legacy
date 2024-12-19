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
        croc = modelBase;
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
        MoCEntityCrocodile entitycrocodile = (MoCEntityCrocodile) entityLiving;
        croc.biteProgress = entitycrocodile.biteProgress;
        croc.swimming = entitycrocodile.isSwimming();
        croc.resting = entitycrocodile.getIsResting();
        if (entitycrocodile.isSpinning() && entitycrocodile.riddenByEntity instanceof EntityPlayer)
        {
            spinCrocWithPlayer(entitycrocodile, (EntityPlayer) entitycrocodile.riddenByEntity);
        }
        
        if (entitycrocodile.isSpinning() && !(entitycrocodile.riddenByEntity instanceof EntityPlayer))
        {
            spinCrocWithCreature(entitycrocodile, (EntityLiving) entitycrocodile.riddenByEntity);
        }
        
        stretch(entitycrocodile);
        if (entitycrocodile.getIsResting())
        {
            if (!entitycrocodile.isInsideOfMaterial(Material.water))
            {
                adjustHeight(entitycrocodile, 0.2F);
            }
            else
            {
                //adjustHeight(entitycrocodile, 0.1F);
            }

        }
        /*        if(!entitycrocodile.getIsAdult())
                {
                    
                }
        */
    }

    protected void rotateAnimal(MoCEntityCrocodile entitycrocodile)
    {

        //float f = entitycrocodile.swingProgress *10F *entitycrocodile.getFlipDirection();
        //float f2 = entitycrocodile.swingProgress /30 *entitycrocodile.getFlipDirection();
        //GL11.glRotatef(180F + f, 0.0F, 0.0F, -1.0F); 
        //GL11.glTranslatef(0.0F-f2, 0.5F, 0.0F);
    }

    protected void adjustHeight(EntityLiving entityLiving, float FHeight)
    {
        GL11.glTranslatef(0.0F, FHeight, 0.0F);
    }

    protected void spinCrocWithCreature(MoCEntityCrocodile croc, EntityLiving prey)
    {
        int intSpin = croc.spinInt;

        int direction = 1;
        if (intSpin > 40)
        {
            intSpin -= 40;
            direction = -1;
        }
        int intEndSpin = intSpin;
        if (intSpin >= 20)
        {
            intEndSpin = (20 - (intSpin - 20));
        }
        if (intEndSpin == 0)
        {
            intEndSpin = 1;
        }
        float f3 = (((intEndSpin) - 1.0F) / 20F) * 1.6F;
        f3 = MathHelper.sqrt_float(f3);
        if (f3 > 1.0F)
        {
            f3 = 1.0F;
        }
        f3 *= direction;
        GL11.glRotatef(f3 * 90F, 0.0F, 0.0F, 1.0F);

        if (prey != null)
        {
            prey.deathTime = intEndSpin;
        }
    }
    
    protected void spinCrocWithPlayer(MoCEntityCrocodile croc, EntityPlayer prey)
    {
        int intSpin = croc.spinInt;

        int direction = 1;
        if (intSpin > 40)
        {
            intSpin -= 40;
            direction = -1;
        }
        int intEndSpin = intSpin;
        if (intSpin >= 20)
        {
            intEndSpin = (20 - (intSpin - 20));
        }
        if (intEndSpin == 0)
        {
            intEndSpin = 1;
        }
        float f3 = (((intEndSpin) - 1.0F) / 20F) * 1.6F;
        f3 = MathHelper.sqrt_float(f3);
        if (f3 > 1.0F)
        {
            f3 = 1.0F;
        }
        f3 *= direction;
        GL11.glRotatef(f3 * 90F, 0.0F, 0.0F, 1.0F);

        if (prey != null)
        {
            prey.deathTime = intEndSpin;
        }
    }
    

    protected void stretch(MoCEntityCrocodile entitycrocodile)
    {
        float f = entitycrocodile.getMoCAge() * 0.01F;
        GL11.glScalef(f, f, f);
    }

    public MoCModelCrocodile croc;

}
