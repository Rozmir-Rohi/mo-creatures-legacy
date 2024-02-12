package drzhark.mocreatures.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.client.model.MoCModelWere;
import drzhark.mocreatures.client.model.MoCModelWereHuman;
import drzhark.mocreatures.entity.witchery_integration.MoCEntityWerewolfWitchery;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderWerewolfWitchery extends RenderLiving {

    private final MoCModelWere werewolfModel;

    public MoCRenderWerewolfWitchery(MoCModelWereHuman werehumanModel, ModelBase modelBase, float f)
    {
        super(modelBase, f);
        setRenderPassModel(werehumanModel);
        //tempWerewolf = (MoCModelWerewolf) modelBase;
        werewolfModel = (MoCModelWere) modelBase;
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d1, double d2, float f, float f1)
    {
        MoCEntityWerewolfWitchery entitywerewolf = (MoCEntityWerewolfWitchery) entityLiving;
        werewolfModel.hunched = entitywerewolf.getIsHunched();
        super.doRender(entityLiving, d, d1, d2, f, f1);

    }

    protected int shouldRenderPass(MoCEntityWerewolfWitchery entitywerewolf, int i)
    {
        int myType = entitywerewolf.getType();

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
