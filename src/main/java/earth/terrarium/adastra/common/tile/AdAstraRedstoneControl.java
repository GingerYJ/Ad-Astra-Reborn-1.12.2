package earth.terrarium.adastra.common.tile;

public enum AdAstraRedstoneControl {
    ALWAYS_ON,
    ACTIVE_WITH_SIGNAL,
    ACTIVE_WITHOUT_SIGNAL,
    NEVER;

    public boolean canRun(boolean powered) {
        switch (this) {
            case ACTIVE_WITH_SIGNAL:
                return powered;
            case ACTIVE_WITHOUT_SIGNAL:
                return !powered;
            case NEVER:
                return false;
            case ALWAYS_ON:
            default:
                return true;
        }
    }

    public static AdAstraRedstoneControl byOrdinal(int ordinal) {
        AdAstraRedstoneControl[] values = values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : ALWAYS_ON;
    }
}
