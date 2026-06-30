package earth.terrarium.adastra.common.tile;

import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class FlagTileEntity extends AdAstraSyncedTileEntity {

    private UUID ownerId;
    private String ownerName = "";
    private String flagUrl = "";
    private int baseColor = 0xFFFFFF;
    private String pattern = "";

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwner(UUID ownerId, String ownerName) {
        this.ownerId = ownerId;
        this.ownerName = ownerName == null ? "" : ownerName;
        syncToClients();
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        String value = flagUrl == null ? "" : flagUrl.trim();
        if (!this.flagUrl.equals(value)) {
            this.flagUrl = value;
            syncToClients();
        }
    }

    public int getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(int baseColor) {
        int value = baseColor & 0xFFFFFF;
        if (this.baseColor != value) {
            this.baseColor = value;
            syncToClients();
        }
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        String value = pattern == null ? "" : pattern.trim();
        if (!this.pattern.equals(value)) {
            this.pattern = value;
            syncToClients();
        }
    }

    public boolean hasCustomContent() {
        return !flagUrl.isEmpty() || !pattern.isEmpty() || baseColor != 0xFFFFFF;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        ownerId = compound.hasUniqueId("OwnerId") ? compound.getUniqueId("OwnerId") : null;
        ownerName = compound.getString("OwnerName");
        flagUrl = compound.getString("FlagUrl");
        baseColor = compound.hasKey("BaseColor") ? compound.getInteger("BaseColor") & 0xFFFFFF : 0xFFFFFF;
        pattern = compound.getString("Pattern");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (ownerId != null) {
            compound.setUniqueId("OwnerId", ownerId);
        }
        compound.setString("OwnerName", ownerName);
        compound.setString("FlagUrl", flagUrl);
        compound.setInteger("BaseColor", baseColor);
        compound.setString("Pattern", pattern);
        return compound;
    }
}
