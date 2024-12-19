package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.client.model.MoCModelTurtle;
import drzhark.mocreatures.entity.animal.MoCEntityTurtle;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderTurtle extends MoCRenderMoC {

    public MoCModelTurtle turtly;

    public MoCRenderTurtle(MoCModelTurtle modelBase, float f)
    {
        super(modelBase, f);
        turtly = modelBase;
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        MoCEntityTurtle entityturtle = (MoCEntityTurtle) entityLiving;
        turtly.upsidedown = entityturtle.getIsUpsideDown();
        turtly.swingProgress = entityturtle.swingProgress;
        turtly.isHiding = entityturtle.getIsHiding();

        if (!entityLiving.worldObj.isRemote && (entityLiving.ridingEntity != null))
        {

            GL11.glTranslatef(0.0F, 1.3F, 0.0F);

        }
        if (entityturtle.getIsHiding())
        {
            adjustHeight(entityturtle, 0.15F * entityturtle.getMoCAge() * 0.01F);
        }
        else if (!entityturtle.getIsHiding() && !entityturtle.getIsUpsideDown() && !entityturtle.isInsideOfMaterial(Material.water))
        {
            adjustHeight(entityturtle, 0.05F * entityturtle.getMoCAge() * 0.01F);
        }
        if (entityturtle.getIsUpsideDown())
        {
            rotateAnimal(entityturtle);
        }

        stretch(entityturtle);

    }

    protected void rotateAnimal(MoCEntityTurtle entityturtle)
    {
        //GL11.glRotatef(180F, -1F, 0.0F, 0.0F); //head up 180
        //GL11.glRotatef(180F, 0.0F, -1.0F, 0.0F); //head around 180

        float f = entityturtle.swingProgress * 10F * entityturtle.getFlipDirection();
        float f2 = entityturtle.swingProgress / 30 * entityturtle.getFlipDirection();
        GL11.glRotatef(180F + f, 0.0F, 0.0F, -1.0F);
        GL11.glTranslatef(0.0F - f2, 0.5F * entityturtle.getMoCAge() * 0.01F, 0.0F);
    }

    protected void adjustHeight(EntityLiving entityLiving, float height)
    {
        GL11.glTranslatef(0.0F, height, 0.0F);
    }

    protected void stretch(MoCEntityTurtle entityturtle)
    {
        float f = entityturtle.getMoCAge() * 0.01F;
        GL11.glScalef(f, f, f);
    }

    @Override
	protected ResourceLocation getEntityTexture(Entity entity) {
        return ((MoCEntityTurtle)entity).getTexture();
    }
}
