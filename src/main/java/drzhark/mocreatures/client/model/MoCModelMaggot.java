package drzhark.mocreatures.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;


public class MoCModelMaggot extends ModelBase
{
    ModelRenderer Head;
    ModelRenderer Body;
    ModelRenderer Tail;
    ModelRenderer Tailtip;

    public MoCModelMaggot()
    {
        textureWidth = 32;
        textureHeight = 32;

        Head = new ModelRenderer(this, 0, 11);
        Head.addBox(-1F, -1F, -2F, 2, 2, 2);
        Head.setRotationPoint(0F, 23F, -2F);

        Body = new ModelRenderer(this, 0, 0);
        Body.addBox(-1.5F, -2F, 0F, 3, 3, 4);
        Body.setRotationPoint(0F, 23F, -2F);

        Tail = new ModelRenderer(this, 0, 7);
        Tail.addBox(-1F, -1F, 0F, 2, 2, 2);
        Tail.setRotationPoint(0F, 23F, 2F);
        
        Tailtip = new ModelRenderer(this, 8, 7);
        Tailtip.addBox(-0.5F, 0F, 0F, 1, 1, 1);
        Tailtip.setRotationPoint(0F, 23F, 4F);
    }

    @Override
	public void render(Entity entity, float f, float movementSpeed, float timer, float f3, float f4, float f5)
    {
        setRotationAngles(f, movementSpeed, timer, f3, f4, f5);
        
        
        GL11.glPushMatrix();
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glBlendFunc(770, 771);
        
        GL11.glScalef(1.0F, 1.0F, (float) (1.0F + (movementSpeed > 0.15 ? 0.15 * 3F : movementSpeed * 3F))); //modulus in if/else form:     if (movementSpeed > 0.15) {return 0.15 * 3F} else {return movementSpeed * 3F}
        
        Head.render(f5);
        Body.render(f5);
        Tail.render(f5);
        Tailtip.render(f5);
        GL11.glDisable(3042/*GL_BLEND*/);
        GL11.glPopMatrix();
        
        
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
    {

    }

}
