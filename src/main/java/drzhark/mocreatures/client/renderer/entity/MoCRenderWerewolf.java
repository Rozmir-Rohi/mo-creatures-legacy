package drzhark.mocreatures.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.client.model.MoCModelWerewolf;
import drzhark.mocreatures.client.model.MoCModelWerewolfHuman;
import drzhark.mocreatures.entity.monster.MoCEntityWerewolf;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderWerewolf extends RenderLiving {

    private final MoCModelWerewolf werewolfModel;

    public MoCRenderWerewolf(MoCModelWerewolfHuman werewolfHumanModel, ModelBase modelBase, float f)
    {
        super(modelBase, f);
        setRenderPassModel(werewolfHumanModel);
        werewolfModel = (MoCModelWerewolf) modelBase;
    }

    @Override
    public void doRender(EntityLiving entityLiving, double x, double y, double z, float rotationYaw, float rotationPitch)
    {
        MoCEntityWerewolf entityWerewolf = (MoCEntityWerewolf) entityLiving;
        werewolfModel.hunched = entityWerewolf.getIsHunched();
        super.doRender(entityLiving, x, y, z, rotationYaw, rotationPitch);

    }

    protected int shouldRenderPass(MoCEntityWerewolf entityWerewolf, int i)
    {
        int myType = entityWerewolf.getType();

        if (!entityWerewolf.getIsHumanForm())
        {
            bindTexture(MoCreatures.proxy.getTexture("wereblank.png"));
        }
        else
        {
            switch (myType)
            {

            case 1:
                bindTexture(MoCreatures.proxy.getTexture("weredude.png"));
                break;
            case 2:
                bindTexture(MoCreatures.proxy.getTexture("werehuman.png"));
                break;
            case 3:
                bindTexture(MoCreatures.proxy.getTexture("wereoldie.png"));
                break;
            case 4:
                bindTexture(MoCreatures.proxy.getTexture("werewoman.png"));
                break;
            default:
                bindTexture(MoCreatures.proxy.getTexture("wereoldie.png"));
            }

        }
        return 1;
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entityLiving, int i, float f)
    {
        return shouldRenderPass((MoCEntityWerewolf) entityLiving, i);
    }

    @Override
	protected ResourceLocation getEntityTexture(Entity entity) {
        return ((MoCEntityWerewolf)entity).getTexture();
    }
}
