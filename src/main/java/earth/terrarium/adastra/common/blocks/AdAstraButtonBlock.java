package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.BlockButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AdAstraButtonBlock extends BlockButton {

    private final boolean wooden;

    public AdAstraButtonBlock(boolean wooden) {
        super(wooden);
        this.wooden = wooden;
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setHardness(0.5f);
    }

    @Override
    protected void playClickSound(EntityPlayer player, World world, BlockPos pos) {
        world.playSound(player, pos, wooden ? SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3f, 0.6f);
    }

    @Override
    protected void playReleaseSound(World world, BlockPos pos) {
        world.playSound(null, pos, wooden ? SoundEvents.BLOCK_WOOD_BUTTON_CLICK_OFF : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3f, 0.5f);
    }
}
