package net.langball.coffee.compat.jade;

import net.langball.coffee.block.ColdBrewPotBlock;
import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.block.entity.MachineBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class CoffeeWorkJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(MachineProgressProvider.INSTANCE, MachineBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(MachineProgressProvider.INSTANCE, MachineBlock.class);
        registration.registerBlockComponent(ColdBrewStageProvider.INSTANCE, ColdBrewPotBlock.class);
    }
}
