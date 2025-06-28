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
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> STAT_POINT_RANGES = BUILDER .comment("Level ranges for stat points (start levels: 1, 5, 10, 15, ...)")
            .defineList("stat_point_ranges",
                    List.of(1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95),
                    o -> o instanceof Integer);
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> STAT_POINT_VALUES = BUILDER .comment("Stat points awarded for each range")
            .defineList("stat_point_values",
                    List.of(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22),
                    o -> o instanceof Integer);

    public static final ModConfigSpec.ConfigValue<Double> DEATH_XP_PENALTY_PERCENTAGE = BUILDER.comment("Percentage of XP lost on death (0.05 = 5%)").
            defineInRange("death_xp_penalty_percentage", 0.05, 0.0, 1.0);

    public static final ModConfigSpec.ConfigValue<Integer> DEATH_PENALTY_MIN_LEVEL = BUILDER.comment("Minimum level before death penalty applies")
            .defineInRange("death_penalty_min_level", 1, 1, 98);

    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_DEATH_PENALTY = BUILDER.comment("Enable XP loss on death")
            .define("enable_death_penalty", true);

    public static final ModConfigSpec.ConfigValue<Boolean> PVP_DEATH_PENALTY_ENABLED = BUILDER.comment("Apply death penalty when killed by another player")
            .define("pvp_death_penalty_enabled", false);

    public static final ModConfigSpec.BooleanValue SHOW_ATTACK_NOTIFY_PARTICLES = BUILDER
            .comment("Show attack notification particles (crit, back attack, down attack, etc). Disabling may improve performance when there are many attacks per second.")
            .define("showAttackNotifyParticles", true);

    public static final ModConfigSpec.BooleanValue SHOW_MISS_PARTICLES = BUILDER
            .comment("Show miss/missing particles. Disabling may improve performance when there are many attacks per second.")
            .define("showMissParticles", true);

    // อันนี้ควรอยู่อันท้าย
    public static final ModConfigSpec SPEC = BUILDER.build();
}

//
//@EventBusSubscriber(modid = Peony.MODID, bus = EventBusSubscriber.Bus.MOD)
//public class Config {
//    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
//
//    private static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER.comment("Whether to log the dirt block on common setup").define("logDirtBlock", true);
//
//    private static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER.comment("A magic number").defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);
//
//    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER.comment("What you want the introduction message to be for the magic number").define("magicNumberIntroduction", "The magic number is... ");
//
//    // a list of strings that are treated as resource locations for items
//    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER.comment("A list of items to log on common setup.").defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);
//
//    static final ModConfigSpec SPEC = BUILDER.build();
//
//    public static boolean logDirtBlock;
//    public static int magicNumber;
//    public static String magicNumberIntroduction;
//    public static Set<Item> items;
//
//    private static boolean validateItemName(final Object obj) {
//        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
//    }
//
//    @SubscribeEvent
//    static void onLoad(final ModConfigEvent event) {
//        logDirtBlock = LOG_DIRT_BLOCK.get();
//        magicNumber = MAGIC_NUMBER.get();
//        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();
//
//        // convert the list of strings into a set of items
//        items = ITEM_STRINGS.get().stream().map(itemName -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName))).collect(Collectors.toSet());
//    }
//}

