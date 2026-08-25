package net.langball.coffee.menu;

import net.langball.coffee.block.MachineBlock;

/**
 * Single source of truth for the legacy machine GUI geometry. Keeping menu
 * slots, progress rendering and JEI click targets together prevents the three
 * coordinate systems from drifting apart.
 */
public record MachineGuiLayout(
        int inputX,
        int inputY,
        int fuelX,
        int fuelY,
        int outputX,
        int outputY,
        String textureName,
        int burnX,
        int burnBaseY,
        int progressX,
        int progressY,
        int progressWidth,
        int progressHeight
) {
    private static final int NO_POSITION = -1;

    public boolean hasFuelSlot() {
        return fuelX != NO_POSITION && fuelY != NO_POSITION;
    }

    public boolean hasBurnIndicator() {
        return burnX != NO_POSITION && burnBaseY != NO_POSITION;
    }

    public int machineSlotCount() {
        return hasFuelSlot() ? 3 : 2;
    }

    public int insertionSlotEnd() {
        return hasFuelSlot() ? 2 : 1;
    }

    public static MachineGuiLayout forKind(MachineBlock.Kind kind) {
        return switch (kind) {
            case GRINDER -> standard("grinder");
            case COFFEE_MACHINE -> new MachineGuiLayout(
                    36, 35,
                    NO_POSITION, NO_POSITION,
                    116, 35,
                    "coffee_machine",
                    NO_POSITION, NO_POSITION,
                    79, 35, 24, 16);
            case ICECREAM_MACHINE -> new MachineGuiLayout(
                    56, 17,
                    112, 17,
                    83, 57,
                    "icecream_machine",
                    85, 5,
                    76, 21, 31, 29);
            case ROLLER, OVEN -> standard("roller");
        };
    }

    private static MachineGuiLayout standard(String textureName) {
        return new MachineGuiLayout(
                56, 17,
                56, 53,
                116, 35,
                textureName,
                56, 36,
                79, 35, 24, 16);
    }
}
