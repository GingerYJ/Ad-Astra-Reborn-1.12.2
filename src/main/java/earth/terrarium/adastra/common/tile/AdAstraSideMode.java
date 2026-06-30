package earth.terrarium.adastra.common.tile;

public enum AdAstraSideMode {
    NONE,
    PUSH,
    PULL,
    PUSH_PULL;

    public boolean canPush() {
        return this == PUSH || this == PUSH_PULL;
    }

    public boolean canPull() {
        return this == PULL || this == PUSH_PULL;
    }

    public AdAstraSideMode next() {
        AdAstraSideMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public AdAstraSideMode previous() {
        AdAstraSideMode[] values = values();
        return values[(ordinal() - 1 + values.length) % values.length];
    }

    public static AdAstraSideMode byOrdinal(int ordinal) {
        AdAstraSideMode[] values = values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : NONE;
    }
}
