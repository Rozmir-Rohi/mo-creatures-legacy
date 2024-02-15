package drzhark.mocreatures.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.client.model.MoCModelWerewolf;
import drzhark.mocreatures.client.model.MoCModelWerewolfHuman;
import drzhark.mocreatures.entity.witchery_integration.MoCEntityWerewolfWitchery;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderWerewolfWitchery extends RenderLiving {

    private final MoCModelWerewolf werewolfModel;

    public MoCRenderWerewolfWitchery(MoCModelWerewolfHuman wereHumanModel, ModelBase modelBase, float f)
    {
        super(modelBase, f);
        setRenderPassModel(wereHumanModel);
        //tempWerewolf = (MoCModelWerewolf) modelBase;
        werewolfModel = (MoCModelWerewolf) modelBase;
    }

    @Override
    public void doRender(EntityLiving entityLiving, double x, double y, double z, float rotationYaw, float rotationPitch)
    {
        MoCEntityWerewolfWitchery entityWerewolf = (MoCEntityWerewolfWitchery) entityLiving;
        werewolfModel.hunched = entityWerewolf.getIsHunched();
        super.doRender(entityLiving, x, y, z, rotationYaw, rotationPitch);

    }

    protected int shouldRenderPass(MoCEntityWerewolfWitchery entityWerewolf, int i)
    {
        int myType = entityWerewolf.getType();

        bindTexture(MoCreatures.proxy.getTexture("wereblank.png"));
        
        return 1;
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entityLiving, int i, float f)
    {
        return shouldRenderPass((MoCEntityWerewolfWitchery) entityLiving, i);
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityWerewolfWitchery)par1Entity).getTexture();
    }
}
