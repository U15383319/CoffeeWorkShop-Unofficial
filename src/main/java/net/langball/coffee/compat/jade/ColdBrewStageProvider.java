package net.langball.coffee.compat.jade;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.ColdBrewPotBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

enum ColdBrewStageProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final int READY_STAGE = 7;
    private static final ResourceLocation UID = new ResourceLocation(CoffeeWork.MODID, "cold_brew_stage");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!accessor.getBlockState().hasProperty(ColdBrewPotBlock.FERM)) {
            return;
        }

        int stage = accessor.getBlockState().getValue(ColdBrewPotBlock.FERM);
        Component text;
        if (stage < READY_STAGE) {
            text = Component.translatable("jade.coffeework.cold_brew.stage", stage, READY_STAGE);
        } else if (stage == READY_STAGE) {
            text = Component.translatable("jade.coffeework.cold_brew.ready", stage, READY_STAGE);
        } else {
            text = Component.translatable("jade.coffeework.cold_brew.drained");
        }
        tooltip.add(text, UID);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
