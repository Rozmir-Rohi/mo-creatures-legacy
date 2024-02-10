package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.client.model.MoCModelWere;
import drzhark.mocreatures.client.model.MoCModelWereHuman;
import drzhark.mocreatures.entity.animal.MoCEntityMouse;
import drzhark.mocreatures.entity.witchery_integration.MoCEntityWerewolfPlayerDummyWitchery;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderWerewolfPlayerDummyWitchery extends RenderLiving {

    private final MoCModelWere tempWerewolf;

    public MoCRenderWerewolfPlayerDummyWitchery(MoCModelWereHuman modelwerehuman, ModelBase modelbase, float f)
    {
        super(modelbase, f);
        setRenderPassModel(modelwerehuman);
        //tempWerewolf = (MoCModelWerewolf) modelbase;
        tempWerewolf = (MoCModelWere) modelbase;
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d1, double d2, float f, float f1)
    {
        MoCEntityWerewolfPlayerDummyWitchery entitywerewolf = (MoCEntityWerewolfPlayerDummyWitchery) entityLiving;
        tempWerewolf.hunched = entitywerewolf.getIsHunched();
        
        super.doRender(entityLiving, d, d1, d2, f, f1);

    }

    protected int shouldRenderPass(MoCEntityWerewolfPlayerDummyWitchery entitywerewolf, int i)
    {
        int myType = entitywerewolf.getType();

        bindTexture(MoCreatures.proxy.getTexture("wereblank.png"));
        
        return 1;
    }
    
    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
    	MoCEntityWerewolfPlayerDummyWitchery entityWerewolfPlayerDummy = (MoCEntityWerewolfPlayerDummyWitchery) entityLiving;
        if (entityWerewolfPlayerDummy.isMountedOnPlayer())
        {
        	attachedToPlayer(entityLiving);
        }
    }
    
    
    protected void attachedToPlayer(EntityLivingBase entityLiving)
    {
        GL11.glTranslatef(0F, 3F, 0F);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entityLiving, int i, float f)
    {
        return shouldRenderPass((MoCEntityWerewolfPlayerDummyWitchery) entityLiving, i);
    }


    protected ResourceLocation getEntityTexture(Entity entity) {
        return ((MoCEntityWerewolfPlayerDummyWitchery)entity).getTexture();
    }
}
