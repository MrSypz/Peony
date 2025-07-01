package sypztep.peony;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class PeonyConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> XP_TABLE = BUILDER.comment("XP required per level (index = level - 1)") // https://irowiki.org/wiki/Base_EXP_Chart Rangarok xp table
            .defineList("xpTable",
                    List.of(
                            548, 894, 1486, 2173, 3152, 3732, 4112, 4441, 4866, 5337, // 1-10
                            5804, 5883, 6106, 6424, 7026, 7624, 7981, 8336, 8689, 9134,        // 11-20
                            9670, 10296, 11012, 12095, 12986, 13872, 14753, 15628, 16498, 17362, // 21-30
                            18221, 19074, 19923, 20947, 21604, 23334, 24606, 25871, 26682, 27932, // 31-40
                            29175, 29969, 31636, 32856, 33194, 34836, 36468, 38523, 40565, 42165, // 41-50
                            43754, 45334, 46903, 48463, 50013, 51976, 53084, 54605, 56116, 57618, // 51-60
                            58277, 60593, 63721, 66005, 69097, 72171, 74407, 77445, 89404, 103722, // 61-70
                            113105, 124848, 130898, 136110, 143684, 149620, 154725, 158216, 181403, 204449, // 71-80
                            235195, 273571, 311715, 372936, 426040, 463377, 523585, 575751, 688829, 763069, // 81-90
                            912936, 1137747, 1361200, 1583303, 1804061, 2098423, 2465723, 297978), // 91- 99
                    o -> o instanceof Integer);
    public static final ModConfigSpec.IntValue MAX_LEVEL = BUILDER.comment("Maximum level allowed")
            .defineInRange("maxLevel", 99, 1, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue START_STAT_POINTS = BUILDER.comment("Stat points granted when first join")
            .defineInRange("startStatpoints", 48, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<Double> DEATH_XP_PENALTY_PERCENTAGE = BUILDER.comment("Percentage of XP lost on death (0.05 = 5%)").
            defineInRange("death_xp_penalty_percentage", 0.05, 0.0, 1.0);

    public static final ModConfigSpec.ConfigValue<Integer> DEATH_PENALTY_MIN_LEVEL = BUILDER.comment("Minimum level before death penalty applies")
            .defineInRange("death_penalty_min_level", 1, 1, 98);

    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_DEATH_PENALTY = BUILDER.comment("Enable XP loss on death")
            .define("enable_death_penalty", true);

    public static final ModConfigSpec.BooleanValue SHOW_ATTACK_NOTIFY_PARTICLES = BUILDER
            .comment("Show attack notification particles (crit, back attack, down attack, etc). Disabling may improve performance when there are many attacks per second.")
            .define("showAttackNotifyParticles", true);

    public static final ModConfigSpec.BooleanValue SHOW_MISS_PARTICLES = BUILDER
            .comment("Show miss/missing particles. Disabling may improve performance when there are many attacks per second.")
            .define("showMissParticles", true);

    // อันนี้ควรอยู่อันท้าย
    public static final ModConfigSpec SPEC = BUILDER.build();
}