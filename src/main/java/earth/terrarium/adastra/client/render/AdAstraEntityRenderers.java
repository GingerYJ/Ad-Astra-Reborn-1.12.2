package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.entities.misc.AirVortexEntity;
import earth.terrarium.adastra.common.entities.mob.CorruptedLunarianEntity;
import earth.terrarium.adastra.common.entities.mob.GlacianRamEntity;
import earth.terrarium.adastra.common.entities.mob.LunarianEntity;
import earth.terrarium.adastra.common.entities.mob.LunarianWanderingTraderEntity;
import earth.terrarium.adastra.common.entities.mob.MartianRaptorEntity;
import earth.terrarium.adastra.common.entities.mob.MoglerEntity;
import earth.terrarium.adastra.common.entities.mob.PygroBruteEntity;
import earth.terrarium.adastra.common.entities.mob.PygroEntity;
import earth.terrarium.adastra.common.entities.mob.StarCrawlerEntity;
import earth.terrarium.adastra.common.entities.mob.SulfurCreeperEntity;
import earth.terrarium.adastra.common.entities.mob.ZombifiedMoglerEntity;
import earth.terrarium.adastra.common.entities.mob.ZombifiedPygroEntity;
import earth.terrarium.adastra.common.entities.projectile.IceSpitEntity;
import earth.terrarium.adastra.common.entities.vehicles.LanderEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier1RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier1RoverEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier2RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier3RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.Tier4RocketEntity;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class AdAstraEntityRenderers {

    private static boolean registered;

    private AdAstraEntityRenderers() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        RenderingRegistry.registerEntityRenderingHandler(AirVortexEntity.class, RenderAirVortex::new);
        RenderingRegistry.registerEntityRenderingHandler(Tier1RoverEntity.class,
            manager -> new RenderTexturedEntity<Tier1RoverEntity>(manager, texture("rover/tier_1_rover"), 2.2f, 0.75f, 1.4f, 0.4f));
        RenderingRegistry.registerEntityRenderingHandler(Tier1RocketEntity.class,
            manager -> new RenderTexturedEntity<Tier1RocketEntity>(manager, texture("rocket/tier_1_rocket"), 1.1f, 4.6f, 1.1f, 0.7f));
        RenderingRegistry.registerEntityRenderingHandler(Tier2RocketEntity.class,
            manager -> new RenderTexturedEntity<Tier2RocketEntity>(manager, texture("rocket/tier_2_rocket"), 1.1f, 4.8f, 1.1f, 0.7f));
        RenderingRegistry.registerEntityRenderingHandler(Tier3RocketEntity.class,
            manager -> new RenderTexturedEntity<Tier3RocketEntity>(manager, texture("rocket/tier_3_rocket"), 1.1f, 5.5f, 1.1f, 0.7f));
        RenderingRegistry.registerEntityRenderingHandler(Tier4RocketEntity.class,
            manager -> new RenderTexturedEntity<Tier4RocketEntity>(manager, texture("rocket/tier_4_rocket"), 1.1f, 7.0f, 1.1f, 0.7f));
        RenderingRegistry.registerEntityRenderingHandler(LanderEntity.class,
            manager -> new RenderTexturedEntity<LanderEntity>(manager, texture("lander/lander"), 1.4f, 2.0f, 1.4f, 0.6f));

        RenderingRegistry.registerEntityRenderingHandler(LunarianEntity.class,
            manager -> new RenderTexturedMob<LunarianEntity>(manager, new ModelLunarian(), texture("mob/lunarian/lunarian"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(CorruptedLunarianEntity.class,
            manager -> new RenderTexturedMob<CorruptedLunarianEntity>(manager, new ModelCorruptedLunarian(), texture("mob/lunarian/corrupted_lunarian"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(LunarianWanderingTraderEntity.class,
            manager -> new RenderTexturedMob<LunarianWanderingTraderEntity>(manager, new ModelLunarian(), texture("mob/lunarian/lunarian_wandering_trader"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(StarCrawlerEntity.class,
            manager -> new RenderTexturedMob<StarCrawlerEntity>(manager, new ModelStarCrawler(), texture("mob/star_crawler"), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(MartianRaptorEntity.class,
            manager -> new RenderTexturedMob<MartianRaptorEntity>(manager, new ModelMartianRaptor(), texture("mob/martian_raptor"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(PygroEntity.class,
            manager -> new RenderTexturedMob<PygroEntity>(manager, new ModelPygro(), texture("mob/pygro"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(ZombifiedPygroEntity.class,
            manager -> new RenderTexturedMob<ZombifiedPygroEntity>(manager, new ModelZombifiedPygro(), texture("mob/zombified_pygro"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(PygroBruteEntity.class,
            manager -> new RenderTexturedMob<PygroBruteEntity>(manager, new ModelPygroBrute(), texture("mob/pygro_brute"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(MoglerEntity.class,
            manager -> new RenderTexturedMob<MoglerEntity>(manager, new ModelMogler(), texture("mob/mogler"), 0.7f));
        RenderingRegistry.registerEntityRenderingHandler(ZombifiedMoglerEntity.class,
            manager -> new RenderTexturedMob<ZombifiedMoglerEntity>(manager, new ModelMogler(), texture("mob/zombified_mogler"), 0.7f));
        RenderingRegistry.registerEntityRenderingHandler(SulfurCreeperEntity.class, RenderSulfurCreeper::new);
        RenderingRegistry.registerEntityRenderingHandler(GlacianRamEntity.class,
            manager -> new RenderTexturedMob<GlacianRamEntity>(manager, new ModelGlacianRam(), texture("mob/glacian_ram/glacian_ram"), 0.7f));

        RenderingRegistry.registerEntityRenderingHandler(IceSpitEntity.class,
            manager -> new RenderSnowball<IceSpitEntity>(manager, ModItems.ICE_SHARD, Minecraft.getMinecraft().getRenderItem()));
    }

    private static ResourceLocation texture(String path) {
        return new ResourceLocation(Reference.MOD_ID, "textures/entity/" + path + ".png");
    }
}
