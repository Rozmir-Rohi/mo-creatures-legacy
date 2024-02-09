package drzhark.mocreatures.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.animal.MoCEntityBigCat;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class MoCModelBigCat1 extends ModelQuadruped {

	public boolean sitting;
	
    public MoCModelBigCat1()
    {
        super(12, 0.0F);
        
        head = new ModelRenderer(this, 20, 0);
        head.addBox(-7F, -8F, -2F, 14, 14, 8, 0.0F);
        head.setRotationPoint(0.0F, 4F, -8F);
        
        body = new ModelRenderer(this, 20, 0);
        body.addBox(-6F, -11F, -8F, 12, 10, 10, 0.0F);
        body.setRotationPoint(0.0F, 5F, 2.0F);
    }
    
    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
    	sitting = ((MoCEntityBigCat) entity).getIsSitting();
        setRotationAngles(f, f1, f2, f3, f4, f5);
        head.render(f5);
        body.render(f5);
    }
    
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
    {
    	head.rotateAngleX = f4 / 57.29578F;
        head.rotateAngleY = f3 / 57.29578F;
    	
        if (!sitting)
        {
        	body.rotationPointX = 0.0F;
        	body.rotationPointY = 5F;
        	body.rotationPointZ = 2.0F;
        	body.rotateAngleX = 1.570796F;
        }
        
        else 
        {
        	body.rotateAngleX = 0.8726646F;
            body.rotationPointX = 0.0F;
            body.rotationPointY = 12F;
            body.rotationPointZ = 1.0F;
        }
    }

}
