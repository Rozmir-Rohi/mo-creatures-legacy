package com.emoniph.witchery.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;


public class ModelWolfAltar extends ModelBase {
	private final ModelRenderer Base;
	private final ModelRenderer Head;
	private final ModelRenderer MouthB;
	private final ModelRenderer Nose2;
	private final ModelRenderer Neck;
	private final ModelRenderer Neck_r1;
	private final ModelRenderer Neck2;
	private final ModelRenderer LSide;
	private final ModelRenderer RSide;
	private final ModelRenderer Nose;
	private final ModelRenderer Mouth;
	private final ModelRenderer MouthOpen;
	private final ModelRenderer REar;
	private final ModelRenderer LEar;
	private final ModelRenderer Body;
	private final ModelRenderer Body_r1;
	private final ModelRenderer Tail;
	private final ModelRenderer TailD_r1;
	private final ModelRenderer TailC_r1;
	private final ModelRenderer TailB_r1;
	private final ModelRenderer TailA_r1;
	private final ModelRenderer Leg4A;
	private final ModelRenderer Leg4D;
	private final ModelRenderer Leg4B;
	private final ModelRenderer Leg4C;
	private final ModelRenderer Leg3B;
	private final ModelRenderer Leg3B_r1;
	private final ModelRenderer Leg2A;
	private final ModelRenderer Leg2B;
	private final ModelRenderer Leg2C;
	private final ModelRenderer Leg3D;
	private final ModelRenderer Leg3C;
	private final ModelRenderer Leg3A;
	private final ModelRenderer Leg1A;
	private final ModelRenderer Leg1A_r1;
	private final ModelRenderer Leg1B;
	private final ModelRenderer Leg1C;

	public ModelWolfAltar() {
		textureWidth = 128;
		textureHeight = 128;

		Base = new ModelRenderer(this);
		Base.setRotationPoint(0.0F, 7.0F, -10.0F);
		Base.cubeList.add(new ModelBox(Base, 43, 103, -9.0F, 12.5F, -1.0F, 18, 2, 22, 0.0F, false));
		Base.cubeList.add(new ModelBox(Base, 28, 98, -11.0F, 14.0F, -3.0F, 22, 3, 26, 0.0F, false));
		Base.cubeList.add(new ModelBox(Base, 28, 98, -11.0F, 10.5F, -3.0F, 22, 2, 26, 0.0F, false));

		Head = new ModelRenderer(this);
		Head.setRotationPoint(0.0F, 7.0F, -10.0F);
		Head.cubeList.add(new ModelBox(Head, 0, 0, -4.0F, -16.0F, -1.0F, 8, 8, 6, 0.0F, false));

		MouthB = new ModelRenderer(this);
		MouthB.setRotationPoint(0.0F, 7.0F, -10.0F);
		MouthB.cubeList.add(new ModelBox(MouthB, 16, 33, -2.0F, -10.0F, -2.0F, 4, 1, 2, 0.0F, false));

		Nose2 = new ModelRenderer(this);
		Nose2.setRotationPoint(0.0F, 7.0F, -10.0F);
		Nose2.cubeList.add(new ModelBox(Nose2, 0, 25, -2.0F, -11.0F, -7.0F, 4, 2, 6, 0.0F, false));

		Neck = new ModelRenderer(this);
		Neck.setRotationPoint(0.0F, 10.0F, -6.0F);
		setRotationAngle(Neck, -0.4538F, 0.0F, 0.0F);
		

		Neck_r1 = new ModelRenderer(this);
		Neck_r1.setRotationPoint(0.0F, 14.0F, 6.0F);
		Neck.addChild(Neck_r1);
		setRotationAngle(Neck_r1, -0.1309F, 0.0F, 0.0F);
		Neck_r1.cubeList.add(new ModelBox(Neck_r1, 28, 0, -3.5F, -28.289F, -16.4153F, 7, 8, 7, 0.0F, false));

		Neck2 = new ModelRenderer(this);
		Neck2.setRotationPoint(0.0F, 14.0F, -10.0F);
		setRotationAngle(Neck2, -0.4538F, 0.0F, 0.0F);
		Neck2.cubeList.add(new ModelBox(Neck2, 0, 14, -1.5F, -14.9774F, -5.7665F, 3, 4, 7, 0.0F, false));

		LSide = new ModelRenderer(this);
		LSide.setRotationPoint(0.0F, 7.0F, -10.0F);
		setRotationAngle(LSide, -0.2094F, 0.4189F, -0.0873F);
		LSide.cubeList.add(new ModelBox(LSide, 28, 33, 1.9218F, -13.2312F, 0.3986F, 2, 6, 6, 0.0F, false));

		RSide = new ModelRenderer(this);
		RSide.setRotationPoint(0.0F, 7.0F, -10.0F);
		setRotationAngle(RSide, -0.2094F, -0.4189F, 0.0873F);
		RSide.cubeList.add(new ModelBox(RSide, 28, 45, -3.9218F, -13.2312F, 0.3986F, 2, 6, 6, 0.0F, false));

		Nose = new ModelRenderer(this);
		Nose.setRotationPoint(0.0F, 7.0F, -10.0F);
		setRotationAngle(Nose, 0.2793F, 0.0F, 0.0F);
		Nose.cubeList.add(new ModelBox(Nose, 44, 33, -1.5F, -12.9182F, -4.0104F, 3, 2, 7, 0.0F, false));

		Mouth = new ModelRenderer(this);
		Mouth.setRotationPoint(0.0F, 7.0F, -10.0F);
		Mouth.cubeList.add(new ModelBox(Mouth, 1, 34, -2.0F, -9.0F, -6.5F, 4, 1, 5, 0.0F, false));

		MouthOpen = new ModelRenderer(this);
		MouthOpen.setRotationPoint(0.0F, 7.0F, -10.0F);
		setRotationAngle(MouthOpen, 0.6109F, 0.0F, 0.0F);
		MouthOpen.cubeList.add(new ModelBox(MouthOpen, 1, 34, -2.0F, -7.7811F, -0.9477F, 4, 1, 5, 0.0F, false));

		REar = new ModelRenderer(this);
		REar.setRotationPoint(0.0F, 7.0F, -10.0F);
		setRotationAngle(REar, 0.0F, 0.0F, -0.1745F);
		REar.cubeList.add(new ModelBox(REar, 22, 0, -0.8534F, -20.681F, 2.5F, 3, 5, 1, 0.0F, false));

		LEar = new ModelRenderer(this);
		LEar.setRotationPoint(0.0F, 7.0F, -10.0F);
		setRotationAngle(LEar, 0.0F, 0.0F, 0.1745F);
		LEar.cubeList.add(new ModelBox(LEar, 13, 14, -2.1466F, -20.681F, 2.5F, 3, 5, 1, 0.0F, false));

		Body = new ModelRenderer(this);
		Body.setRotationPoint(0.0F, 6.5F, 2.0F);
		setRotationAngle(Body, 1.5708F, 0.0F, 0.0F);
		Body.cubeList.add(new ModelBox(Body, 20, 15, -4.0F, -7.5F, 0.0F, 8, 8, 10, 0.0F, false));

		Body_r1 = new ModelRenderer(this);
		Body_r1.setRotationPoint(0.0F, 17.5F, -2.0F);
		Body.addChild(Body_r1);
		setRotationAngle(Body_r1, -1.0822F, 0.0F, 0.0053F);
		Body_r1.cubeList.add(new ModelBox(Body_r1, 0, 40, -3.4407F, -17.0465F, -19.21F, 6, 16, 8, 0.0F, false));

		Tail = new ModelRenderer(this);
		Tail.setRotationPoint(0.0F, 38.5559F, 12.7247F);
		

		TailD_r1 = new ModelRenderer(this);
		TailD_r1.setRotationPoint(-0.5F, -12.25F, 3.5F);
		Tail.addChild(TailD_r1);
		setRotationAngle(TailD_r1, -0.6339F, -0.9257F, 1.9413F);
		TailD_r1.cubeList.add(new ModelBox(TailD_r1, 52, 69, -10.0F, 4.3F, 5.9F, 3, 5, 3, 0.0F, false));

		TailC_r1 = new ModelRenderer(this);
		TailC_r1.setRotationPoint(-0.5F, -12.25F, 3.5F);
		Tail.addChild(TailC_r1);
		setRotationAngle(TailC_r1, -0.3922F, -0.9257F, 1.7109F);
		TailC_r1.cubeList.add(new ModelBox(TailC_r1, 48, 59, -12.0F, -0.7F, 4.15F, 4, 6, 4, 0.0F, false));

		TailB_r1 = new ModelRenderer(this);
		TailB_r1.setRotationPoint(-0.5F, -9.8319F, -2.2359F);
		Tail.addChild(TailB_r1);
		setRotationAngle(TailB_r1, 0.6523F, -0.4019F, 0.4733F);
		TailB_r1.cubeList.add(new ModelBox(TailB_r1, 48, 49, -8.0F, -11.25F, 8.0F, 4, 6, 4, 0.0F, false));

		TailA_r1 = new ModelRenderer(this);
		TailA_r1.setRotationPoint(0.0F, -11.6698F, 3.4686F);
		Tail.addChild(TailA_r1);
		setRotationAngle(TailA_r1, 1.0647F, 0.0F, 0.0F);
		TailA_r1.cubeList.add(new ModelBox(TailA_r1, 52, 42, -1.5F, -15.5F, 7.5F, 3, 4, 3, 0.0F, false));

		Leg4A = new ModelRenderer(this);
		Leg4A.setRotationPoint(-3.0F, 9.5F, 7.0F);
		setRotationAngle(Leg4A, -0.3665F, 0.0F, 0.0F);
		

		Leg4D = new ModelRenderer(this);
		Leg4D.setRotationPoint(-3.0F, 9.5F, 7.0F);
		

		Leg4B = new ModelRenderer(this);
		Leg4B.setRotationPoint(-3.0F, 9.5F, 7.0F);
		setRotationAngle(Leg4B, -0.733F, 0.0F, 0.0F);
		

		Leg4C = new ModelRenderer(this);
		Leg4C.setRotationPoint(-3.0F, 9.5F, 7.0F);
		setRotationAngle(Leg4C, -0.1745F, 0.0F, 0.0F);
		

		Leg3B = new ModelRenderer(this);
		Leg3B.setRotationPoint(3.0F, 9.5F, 7.0F);
		setRotationAngle(Leg3B, -0.733F, 0.0F, 0.0F);
		

		Leg3B_r1 = new ModelRenderer(this);
		Leg3B_r1.setRotationPoint(-3.0F, 14.5F, -7.0F);
		Leg3B.addChild(Leg3B_r1);
		setRotationAngle(Leg3B_r1, 1.4732F, 0.0228F, -0.0289F);
		Leg3B_r1.cubeList.add(new ModelBox(Leg3B_r1, 0, 76, -5.1619F, 6.3817F, 4.863F, 2, 2, 5, 0.0F, false));
		Leg3B_r1.cubeList.add(new ModelBox(Leg3B_r1, 0, 76, 2.5883F, 6.3787F, 4.8717F, 2, 2, 5, 0.0F, false));

		Leg2A = new ModelRenderer(this);
		Leg2A.setRotationPoint(-4.0F, 9.5F, -5.5F);
		setRotationAngle(Leg2A, 0.2618F, 0.0F, 0.0F);
		

		Leg2B = new ModelRenderer(this);
		Leg2B.setRotationPoint(-4.0F, 9.5F, -5.5F);
		setRotationAngle(Leg2B, -0.1745F, 0.0F, 0.0F);
		

		Leg2C = new ModelRenderer(this);
		Leg2C.setRotationPoint(-4.0F, 9.5F, -5.5F);
		

		Leg3D = new ModelRenderer(this);
		Leg3D.setRotationPoint(3.0F, 9.5F, 7.0F);
		

		Leg3C = new ModelRenderer(this);
		Leg3C.setRotationPoint(3.0F, 9.5F, 7.0F);
		setRotationAngle(Leg3C, -0.1745F, 0.0F, 0.0F);
		

		Leg3A = new ModelRenderer(this);
		Leg3A.setRotationPoint(3.0F, 12.5F, 5.0F);
		setRotationAngle(Leg3A, 0.5813F, -0.0544F, -0.0299F);
		Leg3A.cubeList.add(new ModelBox(Leg3A, 0, 64, -8.25F, -3.4197F, -2.9333F, 2, 7, 5, 0.0F, false));
		Leg3A.cubeList.add(new ModelBox(Leg3A, 0, 64, -0.5F, -3.4197F, -2.9333F, 2, 7, 5, 0.0F, false));

		Leg1A = new ModelRenderer(this);
		Leg1A.setRotationPoint(4.0F, 9.5F, -5.5F);
		setRotationAngle(Leg1A, 0.2618F, 0.0F, 0.0F);
		

		Leg1A_r1 = new ModelRenderer(this);
		Leg1A_r1.setRotationPoint(-14.0F, 14.5F, 5.5F);
		Leg1A.addChild(Leg1A_r1);
		setRotationAngle(Leg1A_r1, -0.5672F, 0.0F, 0.0F);
		Leg1A_r1.cubeList.add(new ModelBox(Leg1A_r1, 28, 57, 4.0F, -18.6474F, -13.2936F, 2, 8, 4, 0.0F, false));
		Leg1A_r1.cubeList.add(new ModelBox(Leg1A_r1, 28, 57, 14.0F, -18.6474F, -13.2936F, 2, 8, 4, 0.0F, false));

		Leg1B = new ModelRenderer(this);
		Leg1B.setRotationPoint(4.0F, 9.5F, -5.5F);
		setRotationAngle(Leg1B, -0.3054F, 0.0F, 0.0F);
		Leg1B.cubeList.add(new ModelBox(Leg1B, 28, 69, 0.0F, -1.7103F, -0.3106F, 2, 8, 2, 0.0F, false));
		Leg1B.cubeList.add(new ModelBox(Leg1B, 28, 69, -10.0F, -1.7103F, -0.3106F, 2, 8, 2, 0.0F, false));

		Leg1C = new ModelRenderer(this);
		Leg1C.setRotationPoint(4.0F, 9.5F, -5.5F);
		Leg1C.cubeList.add(new ModelBox(Leg1C, 28, 79, -0.5067F, 6.0F, -2.5F, 3, 2, 3, 0.0F, false));
		Leg1C.cubeList.add(new ModelBox(Leg1C, 28, 79, -1.5067F, 6.0F, 4.0F, 3, 2, 3, 0.0F, false));
		Leg1C.cubeList.add(new ModelBox(Leg1C, 28, 79, -9.2567F, 6.0F, 4.0F, 3, 2, 3, 0.0F, false));
		Leg1C.cubeList.add(new ModelBox(Leg1C, 28, 79, -10.5067F, 6.0F, -2.5F, 3, 2, 3, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		Base.render(f5);
		Head.render(f5);
		MouthB.render(f5);
		Nose2.render(f5);
		Neck.render(f5);
		Neck2.render(f5);
		LSide.render(f5);
		RSide.render(f5);
		Nose.render(f5);
		Mouth.render(f5);
		MouthOpen.render(f5);
		REar.render(f5);
		LEar.render(f5);
		Body.render(f5);
		Tail.render(f5);
		Leg4A.render(f5);
		Leg4D.render(f5);
		Leg4B.render(f5);
		Leg4C.render(f5);
		Leg3B.render(f5);
		Leg2A.render(f5);
		Leg2B.render(f5);
		Leg2C.render(f5);
		Leg3D.render(f5);
		Leg3C.render(f5);
		Leg3A.render(f5);
		Leg1A.render(f5);
		Leg1B.render(f5);
		Leg1C.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}