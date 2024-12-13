package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.animal.MoCEntitySnake;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderSnake extends MoCRenderMoC {

    public MoCRenderSnake(ModelBase modelBase, float f)
    {
        super(modelBase, 0.0F);
        //tempSnake = (MoCModelSnake) modelBase;
    }

    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntitySnake)par1Entity).getTexture();
    }

    protected void adjustHeight(EntityLiving entityLiving, float FHeight)
    {
        GL11.glTranslatef(0.0F, FHeight, 0.0F);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        MoCEntitySnake entitysnake = (MoCEntitySnake) entityLiving;
        stretch(entitysnake);

        /*if(mod_mocreatures.mc.isMultiplayerWorld() && (entitysnake.pickedUp()))
        {

            GL11.glTranslatef(0.0F, 1.4F, 0.0F);

        }*/

        if (entitysnake.pickedUp())// && entitysnake.getSizeF() < 0.6F)
        {
            float xOff = (entitysnake.getSizeF() - 1.0F);
            if (xOff > 0.0F)
            {
                xOff = 0.0F;
            }
            if (entitysnake.worldObj.isRemote)
            {
                GL11.glTranslatef(xOff, 0.0F, 0F);
            }
            else
            {
                GL11.glTranslatef(xOff, 0F, 0.0F);
                //-0.5 puts it in the right shoulder
            }
        }

        if (entitysnake.isInsideOfMaterial(Material.water))
        {
            adjustHeight(entitysnake, -0.25F);
        }

        super.preRenderCallback(entityLiving, f);

    }

    protected void stretch(MoCEntitySnake entitysnake)
    {
        float f = entitysnake.getSizeF();
        GL11.glScalef(f, f, f);
    }
}