package earth.terrarium.adastra.common.entities.mob.lunarians;

import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.Collections;
import java.util.Random;

public final class LunarianMerchantOffers {

    private LunarianMerchantOffers() {
    }

    public static MerchantRecipeList createLunarianTrades(Random random) {
        MerchantRecipeList recipes = new MerchantRecipeList();

        buyForEmerald(recipes, ModItems.CHEESE, 20);
        buyForEmerald(recipes, ModItems.ICE_SHARD, 12);
        buyForEmerald(recipes, ModItems.RAW_DESH, 4);
        buyForEmerald(recipes, ModBlocks.MOON_SAND, 32);
        buyForEmerald(recipes, ModBlocks.MOON_STONE, 20);
        buyForEmerald(recipes, ModBlocks.CONGLOMERATE, 10);

        sellForEmeralds(recipes, ModItems.CHEESE, 3, 1);
        sellForEmeralds(recipes, ModItems.ICE_SHARD, 2, 3);
        sellForEmeralds(recipes, ModItems.DESH_INGOT, 1, 5);
        sellForEmeralds(recipes, ModItems.WRENCH, 1, 14);
        sellForEmeralds(recipes, ModItems.SPACE_HELMET, 1, 10);
        sellForEmeralds(recipes, ModItems.SPACE_SUIT, 1, 36);
        sellForEmeralds(recipes, ModItems.SPACE_PANTS, 1, 14);
        sellForEmeralds(recipes, ModItems.SPACE_BOOTS, 1, 8);
        sellForEmeralds(recipes, ModItems.OXYGEN_GEAR, 1, 10);
        sellForEmeralds(recipes, ModItems.GAS_TANK, 1, 10);
        sellForEmeralds(recipes, ModItems.OXYGEN_BUCKET, 1, 32);
        sellForEmeralds(recipes, ModBlocks.OXYGEN_LOADER, 1, 48);
        sellForEmeralds(recipes, ModBlocks.COAL_GENERATOR, 1, 32);
        sellForEmeralds(recipes, ModBlocks.MOON_STONE_BRICKS, 10, 1);
        sellForEmeralds(recipes, ModBlocks.CHISELED_MOON_STONE_BRICKS, 4, 1);
        sellForEmeralds(recipes, ModBlocks.WHITE_FLAG, 1, 3);
        sellForEmeralds(recipes, ModItems.SPACE_PAINTING, 1, 48);

        addRandomSpecialtyTrade(recipes, random);
        return recipes;
    }

    public static MerchantRecipeList createWanderingTraderTrades(Random random) {
        MerchantRecipeList recipes = new MerchantRecipeList();
        MerchantRecipeList commonRecipes = new MerchantRecipeList();

        sellForEmeralds(commonRecipes, ModItems.CHEESE, 3, 1);
        sellForEmeralds(commonRecipes, ModItems.SPACE_PAINTING, 1, 48);
        sellForEmeralds(commonRecipes, ModItems.GAS_TANK, 1, 10);
        sellForEmeralds(commonRecipes, ModItems.WRENCH, 1, 12);
        sellForEmeralds(commonRecipes, ModItems.OXYGEN_BUCKET, 1, 10);
        sellForEmeralds(commonRecipes, ModItems.OXYGEN_GEAR, 1, 10);
        sellForEmeralds(commonRecipes, ModItems.SPACE_HELMET, 1, 10);
        sellForEmeralds(commonRecipes, ModItems.SPACE_SUIT, 1, 16);
        sellForEmeralds(commonRecipes, ModItems.SPACE_PANTS, 1, 14);
        sellForEmeralds(commonRecipes, ModItems.SPACE_BOOTS, 1, 8);
        sellForEmeralds(commonRecipes, ModBlocks.LAUNCH_PAD, 1, 3);
        sellForEmeralds(commonRecipes, ModBlocks.GLACIAN_LOG, 16, 5);
        sellForEmeralds(commonRecipes, ModBlocks.GLACIAN_LEAVES, 16, 5);
        sellForEmeralds(commonRecipes, ModBlocks.AERONOS_STEM, 16, 5);
        sellForEmeralds(commonRecipes, ModBlocks.STROPHAR_STEM, 16, 5);
        sellForEmeralds(commonRecipes, ModItems.FUEL_BUCKET, 1, 4);

        buyForEmerald(commonRecipes, ModItems.DESH_INGOT, 4);
        buyForEmerald(commonRecipes, ModItems.OSTRUM_INGOT, 4);
        buyForEmerald(commonRecipes, ModItems.CALORITE_INGOT, 4);

        Collections.shuffle(commonRecipes, random);
        for (int i = 0; i < Math.min(5, commonRecipes.size()); i++) {
            recipes.add(commonRecipes.get(i));
        }

        addRareWanderingTrade(recipes, random);
        return recipes;
    }

    private static void addRandomSpecialtyTrade(MerchantRecipeList recipes, Random random) {
        switch (random.nextInt(5)) {
            case 0:
                sellForEmeralds(recipes, ModItems.TIER_1_ROVER, 1, 48);
                break;
            case 1:
                sellForEmeralds(recipes, ModItems.TIER_1_ROCKET, 1, 64);
                break;
            case 2:
                sellForEmeralds(recipes, ModItems.ZIP_GUN, 1, 18);
                break;
            case 3:
                sellForEmeralds(recipes, ModBlocks.RADIO, 1, 12);
                break;
            default:
                sellForEmeralds(recipes, ModBlocks.MOON_GLOBE, 1, 16);
                break;
        }
    }

    private static void addRareWanderingTrade(MerchantRecipeList recipes, Random random) {
        switch (random.nextInt(6)) {
            case 0:
                sellForEmeralds(recipes, ModItems.ETRIUM_INGOT, 1, 8);
                break;
            case 1:
                sellForEmeralds(recipes, ModItems.OSTRUM_INGOT, 1, 6);
                break;
            case 2:
                sellForEmeralds(recipes, ModItems.STEEL_INGOT, 2, 4);
                break;
            case 3:
                sellForEmeralds(recipes, ModItems.LARGE_GAS_TANK, 1, 24);
                break;
            case 4:
                sellForEmeralds(recipes, ModItems.TI_69, 1, 12);
                break;
            default:
                sellForEmeralds(recipes, ModItems.CRYO_FUEL_BUCKET, 1, 8);
                break;
        }
    }

    private static void buyForEmerald(MerchantRecipeList recipes, Item item, int count) {
        recipes.add(new MerchantRecipe(new ItemStack(item, count), new ItemStack(Items.EMERALD)));
    }

    private static void buyForEmerald(MerchantRecipeList recipes, Block block, int count) {
        buyForEmerald(recipes, Item.getItemFromBlock(block), count);
    }

    private static void sellForEmeralds(MerchantRecipeList recipes, Item item, int count, int emeralds) {
        recipes.add(new MerchantRecipe(new ItemStack(Items.EMERALD, emeralds), new ItemStack(item, count)));
    }

    private static void sellForEmeralds(MerchantRecipeList recipes, Block block, int count, int emeralds) {
        Item item = Item.getItemFromBlock(block);
        if (item != Items.AIR) {
            sellForEmeralds(recipes, item, count, emeralds);
        }
    }
}
