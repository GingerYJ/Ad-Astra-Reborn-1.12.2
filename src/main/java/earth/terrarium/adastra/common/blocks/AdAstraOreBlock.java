package earth.terrarium.adastra.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

public class AdAstraOreBlock extends AdAstraBlock {

    private final Item droppedItem;
    private final int droppedMeta;
    private final int minDrops;
    private final int maxDrops;
    private final int minExperience;
    private final int maxExperience;

    public AdAstraOreBlock(Item droppedItem, int minDrops, int maxDrops, int minExperience, int maxExperience) {
        this(droppedItem, 0, minDrops, maxDrops, minExperience, maxExperience);
    }

    public AdAstraOreBlock(Item droppedItem, int droppedMeta, int minDrops, int maxDrops, int minExperience, int maxExperience) {
        super(Material.ROCK, 3.0f, 5.0f);
        this.droppedItem = droppedItem;
        this.droppedMeta = droppedMeta;
        this.minDrops = minDrops;
        this.maxDrops = maxDrops;
        this.minExperience = minExperience;
        this.maxExperience = maxExperience;
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random random, int fortune) {
        return droppedItem == null ? Item.getItemFromBlock(this) : droppedItem;
    }

    @Override
    public int quantityDropped(Random random) {
        return MathHelper.getInt(random, minDrops, maxDrops);
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        int count = quantityDropped(random);
        if (fortune > 0 && droppedItem != null) {
            int multiplier = random.nextInt(fortune + 2) - 1;
            if (multiplier < 0) {
                multiplier = 0;
            }
            count *= multiplier + 1;
        }
        return count;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return droppedMeta;
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        if (droppedItem == null) {
            return 0;
        }
        Random random = world instanceof net.minecraft.world.World
            ? ((net.minecraft.world.World) world).rand
            : new Random();
        return MathHelper.getInt(random, minExperience, maxExperience);
    }
}
