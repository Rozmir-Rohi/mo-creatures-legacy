package drzhark.mocreatures.client.model;



import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.animal.MoCEntityDuck;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class MoCModelDuck extends ModelBase {
    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer rightLeg;
    public ModelRenderer leftLeg;
    public ModelRenderer rightWing;
    public ModelRenderer leftWing;
    public ModelRenderer bill;
    public ModelRenderer chin;

    public MoCModelDuck()
    {
        byte var1 = 16;
        head = new ModelRenderer(this, 0, 0);
        head.addBox(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F);
        head.setRotationPoint(0.0F, -1 + var1, -4.0F);
        bill = new ModelRenderer(this, 14, 0);
        bill.addBox(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F);
        bill.setRotationPoint(0.0F, -1 + var1, -4.0F);
        chin = new ModelRenderer(this, 14, 4);
        chin.addBox(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
        chin.setRotationPoint(0.0F, -1 + var1, -4.0F);
        body = new ModelRenderer(this, 0, 9);
        body.addBox(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
        body.setRotationPoint(0.0F, var1, 0.0F);
        rightLeg = new ModelRenderer(this, 26, 0);
        rightLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
        rightLeg.setRotationPoint(-2.0F, 3 + var1, 1.0F);
        leftLeg = new ModelRenderer(this, 26, 0);
        leftLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
        leftLeg.setRotationPoint(1.0F, 3 + var1, 1.0F);
        rightWing = new ModelRenderer(this, 24, 13);
        rightWing.addBox(0.0F, 0.0F, -3.0F, 1, 4, 6);
        rightWing.setRotationPoint(-4.0F, -3 + var1, 0.0F);
        leftWing = new ModelRenderer(this, 24, 13);
        leftWing.addBox(-1.0F, 0.0F, -3.0F, 1, 4, 6);
        leftWing.setRotationPoint(4.0F, -3 + var1, 0.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        MoCEntityDuck entityDuck = (MoCEntityDuck) entity;
        boolean isFlappingWings = entityDuck.isOnAir() && entityDuck.motionY < 0.0D;
        setRotationAngles(par2, par3, par4, par5, par6, par7, isFlappingWings);

        head.render(par7);
        bill.render(par7);
        chin.render(par7);
        body.render(par7);
        rightLeg.render(par7);
        leftLeg.render(par7);
        rightWing.render(par7);
        leftWing.render(par7);

    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are
     * used for animating the movement of arms and legs, where par1 represents
     * the time(so that arms and legs swing back and forth) and par2 represents
     * how "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, boolean isFlappingWings)
    {
        head.rotateAngleX = -(par5 / (180F / (float) Math.PI));
        head.rotateAngleY = par4 / (180F / (float) Math.PI);
        bill.rotateAngleX = head.rotateAngleX;
        bill.rotateAngleY = head.rotateAngleY;
        chin.rotateAngleX = head.rotateAngleX;
        chin.rotateAngleY = head.rotateAngleY;
        body.rotateAngleX = ((float) Math.PI / 2F);
        rightLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        leftLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        if (isFlappingWings)
        {
            Float WingRot = MathHelper.cos((par3 * 1.4F) + (float) Math.PI) * 0.6F;
            rightWing.rotateAngleZ = 0.5F + WingRot;
            leftWing.rotateAngleZ = -0.5F - WingRot;
        }
        else
        {
            rightWing.rotateAngleZ = 0F;
            leftWing.rotateAngleZ = 0F;
        }
    }
}