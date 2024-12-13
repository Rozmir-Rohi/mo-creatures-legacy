package drzhark.mocreatures.client.model;



import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.item.MoCEntityFishBowl;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class MoCModelFishBowl extends ModelBase {
    //fields
    ModelRenderer BowlTap;
    ModelRenderer Bowl;
    ModelRenderer Water;
    private final int numberFish = 10;

    public ModelRenderer Body[];
    public ModelRenderer Tail[];

    //public boolean pickedUp;

    public MoCModelFishBowl()
    {
        textureWidth = 128;
        textureHeight = 64;
        Body = new ModelRenderer[numberFish];
        Tail = new ModelRenderer[numberFish];

        BowlTap = new ModelRenderer(this, 64, 24);
        BowlTap.addBox(-7.5F, -7.5F, -7.5F, 11, 2, 11);
        BowlTap.setRotationPoint(2F, 15F, 2F);
        //BowlTap.setTextureSize(128, 64);

        //BowlTap.mirror = true;
        //setRotation(BowlTap, 0F, 0F, 0F);
        Bowl = new ModelRenderer(this, 0, 33);
        Bowl.addBox(-8F, -8F, -6F, 16, 15, 16);
        Bowl.setRotationPoint(0F, 17F, -2F);
        // Bowl.setTextureSize(128, 64);
        //Bowl.mirror = true;
        //setRotation(BowlTap, 0F, 0F, 0F);
        Water = new ModelRenderer(this, 64, 38);
        Water.addBox(-7.5F, -7.5F, -7.5F, 15, 11, 15);
        Water.setRotationPoint(0F, 20F, 0F);
        //Water.setTextureSize(128, 64);
        //Water.mirror = true;
        //setRotation(BowlTap, 0F, 0F, 0F);

        int yText = 0;
        for (int i = 0; i < numberFish; i++)
        {
            int xText = (i * 20);
            if (i > 4)
            {
                xText = xText - 100;
                yText = 10;
            }
            Body[i] = new ModelRenderer(this, xText, yText);
            Body[i].addBox(3F, -4F, -4F, 1, 5, 5);
            Body[i].setRotationPoint(0F, 19F, 0F);
            //Body[i].setTextureSize(128, 64);
            setRotation(Body[i], 0.7853982F, 0F, 0F);

            Tail[i] = new ModelRenderer(this, xText + 12, yText);
            Tail[i].addBox(2.9F, 0F, 0F, 1, 3, 3);
            Tail[i].setRotationPoint(0F, 19F, 0F);
            //Tail[i].setTextureSize(128, 64);
            setRotation(Tail[i], 0.7853982F, 0F, 0F);
        }

    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        MoCEntityFishBowl entityfishbowl = (MoCEntityFishBowl) entity;
        int rotationDegree = entityfishbowl.getRotation();
        int typeI = entityfishbowl.getType();
        //if (typeI < 0) typeI = 0;
        if (typeI < 11 && (typeI - 1 >= 0))
        {
            setRotationAngles(f, f1, f2, f3, f4, f5, typeI - 1, rotationDegree);
            Body[typeI - 1].render(f5);
            Tail[typeI - 1].render(f5);
        }
        //Water.render(f5);
        boolean flag = false;
        GL11.glPushMatrix();
        GL11.glEnable(3042 /*GL_BLEND*/);
        if (!flag)
        {
            float transparency = 0.6F;
            GL11.glBlendFunc(770, 771);
            GL11.glColor4f(0.8F, 0.8F, 0.8F, transparency);
            BowlTap.render(f5);
            Bowl.render(f5);
            if (typeI > 0)
            {
                Water.render(f5);
                //GL11.glColor4f(0.8F, 0.8F, 0.8F, transparency+0.5F);
            }

        }
        else
        {
            GL11.glBlendFunc(770, 1);

        }
        //super.doRender(entityLiving, d, d1, d2, f, f1);
        GL11.glDisable(3042/*GL_BLEND*/);
        GL11.glPopMatrix();

        /*GL11.glPushMatrix();
        GL11.glEnable(3042 GL_BLEND );
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(0.8F, 0.8F, 0.8F, 0.6F);
        Water.render(f5);
        GL11.glDisable(3042GL_BLEND);
        GL11.glPopMatrix();*/
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, int type, int rot)
    {
        //super.setRotationAngles(f, f1, f2, f3, f4, f5);
        Body[type].rotateAngleY = rot / 57.29578F;
        Tail[type].rotateAngleY = rot / 57.29578F;
    }

}

/*
// Date: 4/28/2012 10:51:55 PM
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX






package net.minecraft.src;

public class ModelFishbowl extends ModelBase
{
  //fields
    ModelRenderer Jar;
    ModelRenderer Bowl;
    ModelRenderer Water;
    ModelRenderer Body;
    ModelRenderer Tail;
  
  public ModelFishbowl()
  {
    textureWidth = 64;
    textureHeight = 32;
    
      Jar = new ModelRenderer(this, 32, 14);
      Jar.addBox(-7.5F, -7.5F, -7.5F, 6, 1, 6);
      Jar.setRotationPoint(2F, 15F, 2F);
      Jar.setTextureSize(64, 32);
      Jar.mirror = true;
      setRotation(Jar, 0F, 0F, 0F);
      Bowl = new ModelRenderer(this, 0, 10);
      Bowl.addBox(-4F, -18F, -3F, 8, 8, 8);
      Bowl.setRotationPoint(0F, 17F, -2F);
      Bowl.setTextureSize(64, 32);
      Bowl.mirror = true;
      setRotation(Bowl, 0F, 0F, 0F);
      Water = new ModelRenderer(this, 25, 1);
      Water.addBox(-7.5F, -11.5F, -7.5F, 7, 5, 7);
      Water.setRotationPoint(0F, 20F, 0F);
      Water.setTextureSize(64, 32);
      Water.mirror = true;
      setRotation(Water, 0F, 0F, 0F);
      Body = new ModelRenderer(this, 0, 0);
      Body.addBox(3F, -4F, -4F, 1, 5, 5);
      Body.setRotationPoint(0F, 19F, 0F);
      Body.setTextureSize(64, 32);
      Body.mirror = true;
      setRotation(Body, 0.7853982F, 3.054326F, 0F);
      Tail = new ModelRenderer(this, 12, 0);
      Tail.addBox(2.9F, 0F, 0F, 1, 3, 3);
      Tail.setRotationPoint(0F, 19F, 0F);
      Tail.setTextureSize(64, 32);
      Tail.mirror = true;
      setRotation(Tail, 0.7853982F, 3.054326F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5);
    Jar.render(f5);
    Bowl.render(f5);
    Water.render(f5);
    Body.render(f5);
    Tail.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5);
  }

}


*/