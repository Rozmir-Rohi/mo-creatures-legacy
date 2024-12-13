package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.client.MoCClientProxy;
import drzhark.mocreatures.client.model.MoCModelFishBowl;
import drzhark.mocreatures.entity.item.MoCEntityFishBowl;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderFishBowl extends RenderLiving {

    public MoCRenderFishBowl(MoCModelFishBowl modelfishbowl, float f)
    {
        super(modelfishbowl, f);

    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        MoCEntityFishBowl entityfishbowl = (MoCEntityFishBowl) entityLiving;
        //mycolor = entitykittybed.getSheetColor();
        //fishbowl.pickedUp = entityfishbowl.getPickedUp();
        if (MoCreatures.isServer() && (entityLiving.ridingEntity == MoCClientProxy.mc.thePlayer))
        {

            GL11.glTranslatef(0.0F, 1.1F, 0.0F);

        }
    }

    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityFishBowl)par1Entity).getTexture();
    }
}
