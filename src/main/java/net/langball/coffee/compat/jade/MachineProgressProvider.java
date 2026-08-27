package net.langball.coffee.compat.jade;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.entity.MachineBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;

enum MachineProgressProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID = new ResourceLocation(CoffeeWork.MODID, "machine_progress");
    private static final String PROGRESS_KEY = "CoffeeWorkProgress";
    private static final String TOTAL_KEY = "CoffeeWorkProgressTotal";

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof MachineBlockEntity machine)) {
            return;
        }

        int progress = machine.processingProgress();
        boolean hasInput = !machine.items().getStackInSlot(MachineBlockEntity.SLOT_INPUT).isEmpty();
        if (progress <= 0 && !hasInput) {
            return;
        }

        data.putInt(PROGRESS_KEY, Math.max(0, progress));
        data.putInt(TOTAL_KEY, Math.max(1, machine.processingTotal()));
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains(PROGRESS_KEY, Tag.TAG_INT) || !data.contains(TOTAL_KEY, Tag.TAG_INT)) {
            return;
        }

        int progress = Math.max(0, data.getInt(PROGRESS_KEY));
        int total = Math.max(1, data.getInt(TOTAL_KEY));
        float ratio = Mth.clamp((float) progress / total, 0.0F, 1.0F);
        int percentage = Mth.clamp(Math.round(ratio * 100.0F), 0, 100);
        IElementHelper helper = tooltip.getElementHelper();
        tooltip.add(helper.progress(
                ratio,
                Component.translatable("jade.coffeework.machine.progress", percentage),
                helper.progressStyle(),
                BoxStyle.DEFAULT,
                true).tag(UID));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
