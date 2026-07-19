package earth.terrarium.adastra.common.entities.misc;

import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Locale;

public class SpacePaintingEntity extends EntityHanging {

    private Variant variant = Variant.EARTH;

    public SpacePaintingEntity(World world) {
        super(world);
    }

    public SpacePaintingEntity(World world, BlockPos pos, EnumFacing facing, Variant variant) {
        super(world, pos);
        this.variant = variant;
        updateFacingWithBoundingBox(facing);
    }

    @Override
    public int getWidthPixels() {
        return variant.width;
    }

    @Override
    public int getHeightPixels() {
        return variant.height;
    }

    @Override
    public void onBroken(Entity brokenEntity) {
        world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_PAINTING_BREAK, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        if (world.getGameRules().getBoolean("doEntityDrops")) {
            if (brokenEntity instanceof EntityPlayer && ((EntityPlayer) brokenEntity).capabilities.isCreativeMode) {
                return;
            }
            entityDropItem(new ItemStack(ModItems.SPACE_PAINTING), 0.0f);
        }
    }

    @Override
    public void playPlaceSound() {
        world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_PAINTING_PLACE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setString("Variant", variant.id);
        super.writeEntityToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        variant = Variant.byId(compound.getString("Variant"));
        super.readEntityFromNBT(compound);
    }

    public Variant getVariant() {
        return variant;
    }

    public enum Variant {
        MERCURY("mercury", 16, 16),
        MOON("moon", 16, 16),
        PLUTO("pluto", 16, 16),
        EARTH("earth", 32, 32),
        GLACIO("glacio", 32, 32),
        MARS("mars", 32, 32),
        VENUS("venus", 32, 32),
        JUPITER("jupiter", 48, 48),
        NEPTUNE("neptune", 48, 48),
        URANUS("uranus", 48, 48),
        SATURN("saturn", 64, 48),
        THE_MILKY_WAY("the_milky_way", 64, 48),
        ALPHA_CENTAURI("alpha_centauri", 64, 64),
        SUN("sun", 80, 80),
        CERES("ceres", 16, 16),
        ORCUS("orcus", 16, 16),
        HAUMEA("haumea", 16, 16),
        QUAOAR("quaoar", 16, 16),
        MAKEMAKE("makemake", 16, 16),
        GONGGONG("gonggong", 16, 16),
        ERIS("eris", 16, 16),
        SEDNA("sedna", 16, 16),
        PROXIMA_CENTAURI_B("proxima_centauri_b", 32, 32),
        VICINUS("vicinus", 32, 32);

        private final String id;
        private final int width;
        private final int height;

        Variant(String id, int width, int height) {
            this.id = id;
            this.width = width;
            this.height = height;
        }

        public String getId() {
            return id;
        }

        public int getArea() {
            return width * height;
        }

        private static Variant byId(String id) {
            String normalized = id == null ? "" : id.toLowerCase(Locale.ROOT);
            for (Variant variant : values()) {
                if (variant.id.equals(normalized)) {
                    return variant;
                }
            }
            return EARTH;
        }
    }
}
