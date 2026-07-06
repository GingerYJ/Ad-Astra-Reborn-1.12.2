package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * ItemBlock for celestial meta-blocks that use PropertyEnum.
 * Overrides getTranslationKey to include the enum variant name,
 * enabling per-variant display names via lang files.
 *
 * Example: ceres_blocks with meta=0 (ceres_grunt)
 *   鈫?translation key: tile.ad_astra.ceres_blocks.ceres_grunt
 */
public class CelestialItemBlock extends ItemBlock {

    private final IProperty<?> variantProperty;

    public CelestialItemBlock(Block block, IProperty<?> variantProperty) {
        super(block);
        this.variantProperty = variantProperty;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        int meta = stack.getItemDamage();
        Comparable<?> value = getVariantValue(meta);
        if (value != null) {
            String variantName = getVariantName(meta);
            return "tile." + Reference.MOD_ID + "." + getBlock().getRegistryName().getPath() + "." + variantName;
        }
        return super.getTranslationKey(stack);
    }

    public int getVariantCount() {
        int count = 0;
        for (Object ignored : variantProperty.getAllowedValues()) {
            count++;
        }
        return count;
    }

    public String getVariantName(int meta) {
        Comparable<?> value = getVariantValue(meta);
        if (value instanceof net.minecraft.util.IStringSerializable) {
            return ((net.minecraft.util.IStringSerializable) value).getName();
        }
        if (value instanceof Enum<?>) {
            return ((Enum<?>) value).name().toLowerCase(java.util.Locale.ROOT);
        }
        return null;
    }

    public String getVariantModelName(int meta) {
        String variantName = getVariantName(meta);
        if (variantName == null || getBlock().getRegistryName() == null) {
            return null;
        }
        return getBlock().getRegistryName().getPath() + "_" + variantName;
    }

    @SuppressWarnings("unchecked")
    private Comparable<?> getVariantValue(int meta) {
        for (Comparable<?> value : (Iterable<Comparable<?>>) (Iterable<?>) variantProperty.getAllowedValues()) {
            // Try to match by ordinal (standard for PropertyEnum)
            if (((Enum<?>) value).ordinal() == meta) {
                return value;
            }
        }
        return null;
    }
}

