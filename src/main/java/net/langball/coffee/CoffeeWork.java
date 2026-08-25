package net.langball.coffee;

import com.mojang.logging.LogUtils;
import net.langball.coffee.registry.ModBlocks;
import net.langball.coffee.registry.ModBlockEntities;
import net.langball.coffee.registry.ModCreativeTabs;
import net.langball.coffee.registry.ModEffects;
import net.langball.coffee.registry.ModItems;
import net.langball.coffee.registry.ModMenus;
import net.langball.coffee.registry.ModRecipes;
import net.langball.coffee.registry.ModSounds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CoffeeWork.MODID)
public final class CoffeeWork {
    public static final String MODID = "coffeework";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CoffeeWork() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modBus);
        ModSounds.register(modBus);
        ModItems.register(modBus);
        ModEffects.register(modBus);
        ModBlockEntities.register(modBus);
        ModMenus.register(modBus);
        ModRecipes.register(modBus);
        ModCreativeTabs.register(modBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CoffeeConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(CommonEvents.class);

        LOGGER.info("Loading Coffee Workshop for Forge 1.20.1 ({} blocks, {} items)",
                ModBlocks.ENTRIES.size(), ModItems.ENTRIES.size());
    }
}
