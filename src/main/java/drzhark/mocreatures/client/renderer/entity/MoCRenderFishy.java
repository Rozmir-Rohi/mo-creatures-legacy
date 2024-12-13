package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.aquatic.MoCEntityFishy;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderFishy extends RenderLiving {

    public MoCRenderFishy(ModelBase modelBase, float f)
    {
        super(modelBase, f);
    }

    @Override
    public void doRender(EntityLiving entityLiving, double x, double y, double z, float rotationYaw, float rotationPitch)
    {
        MoCEntityFishy entityFishy = (MoCEntityFishy) entityLiving;
        if (entityFishy.getType() == 0)// && !MoCreatures.mc.isMultiplayerWorld())
        {
            entityFishy.selectType();
        }
        super.doRender(entityFishy, x, y, z, rotationYaw, rotationPitch);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        GL11.glTranslatef(0.0F, 0.3F, 0.0F);
    }

    @Override
    protected float handleRotationFloat(EntityLivingBase entityLiving, float f)
    {
        MoCEntityFishy entityFishy = (MoCEntityFishy) entityLiving;
        if (!entityFishy.getIsAdult())
        {
            stretch(entityFishy);
        }
        return entityLiving.ticksExisted + f;
    }

    protected void stretch(MoCEntityFishy entityFishy)
    {
        GL11.glScalef(entityFishy.getMoCAge() * 0.01F, entityFishy.getMoCAge() * 0.01F, entityFishy.getMoCAge() * 0.01F);
    }

    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityFishy)par1Entity).getTexture();
    }
}
