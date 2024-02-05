package drzhark.mocreatures.entity;

import net.minecraft.nbt.NBTTagCompound;

public interface IMoCTameable {

    boolean isRiderDisconnecting();

    void playTameEffect(boolean par1);

    String getOwnerName();

    String getName();

    void setName(String name);

    void setTamed(boolean par1);

    void setDead();

    int getType();

    void writeEntityToNBT(NBTTagCompound nbtTagCompound);

    void readEntityFromNBT(NBTTagCompound nbtTagCompound);

    int getMoCAge();

    int getOwnerPetId();

    boolean getIsAdult();

    void setType(int creatureType);

    void setOwner(String username);

    void setMoCAge(int age);

    public void setAdult(boolean adult);

    public void setOwnerPetId(int petId);

    public boolean getIsTamed();

    public float getPetHealth();
}