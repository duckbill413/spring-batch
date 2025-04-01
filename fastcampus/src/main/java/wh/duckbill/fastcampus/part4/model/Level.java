package wh.duckbill.fastcampus.part4.model;

import java.util.Objects;

/**
 * author        : duckbill413
 * date          : 2023-02-07
 * description   :
 **/

public enum Level {
    VIP(500_000, null),
    GOLD(500_000, VIP),
    SILVER(300_000, GOLD),
    NORMAL(200_000, SILVER);

    private final int nextAmount;
    private final Level nextLevel;

    Level(int nextAmount, Level nextLevel){
        this.nextLevel = nextLevel;
        this.nextAmount = nextAmount;
    }

    public static boolean availableLevelUp(Level level, Integer orderedPrice) {
        if (Objects.isNull(level))
            return false;

        if (Objects.isNull(level.nextLevel))
            return false;

        return orderedPrice >= level.nextAmount;
    }

    public static Level getNextLevel(Integer orderedPrice) {
        if (orderedPrice >= Level.VIP.nextAmount)
            return VIP;
        if (orderedPrice >= Level.GOLD.nextAmount)
            return GOLD.nextLevel;
        if (orderedPrice >= Level.SILVER.nextAmount)
            return SILVER.nextLevel;
        if (orderedPrice >= Level.NORMAL.nextAmount)
            return NORMAL.nextLevel;
        return NORMAL;
    }
}
