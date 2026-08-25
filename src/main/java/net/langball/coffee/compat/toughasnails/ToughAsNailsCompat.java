package net.langball.coffee.compat.toughasnails;

import net.minecraft.world.entity.player.Player;
import toughasnails.api.temperature.ITemperature;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;

public final class ToughAsNailsCompat {
    public static final String MOD_ID = "toughasnails";

    private ToughAsNailsCompat() {
    }

    public static void afterDrink(Player player, String drinkId) {
        if (!TemperatureHelper.isTemperatureEnabled()) {
            return;
        }

        ITemperature temperature = TemperatureHelper.getTemperatureData(player);
        TemperatureLevel current = temperature.getLevel();
        TemperatureLevel adjusted = current;

        if (drinkId.endsWith("_ice")) {
            if (current == TemperatureLevel.WARM || current == TemperatureLevel.HOT) {
                adjusted = current.decrement(1);
            }
        } else if (current == TemperatureLevel.ICY || current == TemperatureLevel.COLD) {
            adjusted = current.increment(1);
        } else if (current == TemperatureLevel.WARM || current == TemperatureLevel.HOT) {
            adjusted = current.decrement(1);
        }

        if (adjusted != current) {
            temperature.setLevel(adjusted);
        }
    }
}
