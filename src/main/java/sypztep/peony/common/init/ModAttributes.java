package sypztep.peony.common.init;

import java.util.ArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import sypztep.peony.Peony;

public final class ModAttributes {
    public static final ArrayList<Holder<Attribute>> ENTRIES = new ArrayList<>();

    public static final Holder<Attribute> ACCURACY = register("accuracy", new RangedAttribute("attribute.name.accuracy", 0, 0.0, 2048.0D).setSyncable(true));
    public static final Holder<Attribute> EVASION = register("evasion", new RangedAttribute("attribute.name.evasion", 0, 0.0, 2048.0D).setSyncable(true));;
    public static final Holder<Attribute> CRIT_DAMAGE = register("crit_damage", new RangedAttribute("attribute.name.crit_damage", 0.5, 0.0, 10.24D).setSyncable(true));
    public static final Holder<Attribute> CRIT_CHANCE = register("crit_chance", new RangedAttribute("attribute.name.crit_chance", 0.05, 0.0, 2.0D).setSyncable(true));
    public static final Holder<Attribute> BACK_ATTACK = register("back_attack", new RangedAttribute("attribute.name.back_attack", 0.5, 0.0, 10.24D).setSyncable(true));
    public static final Holder<Attribute> AIR_ATTACK = register("air_attack", new RangedAttribute("attribute.name.air_attack", 1.0, 0.0, 10.24D).setSyncable(true));
    public static final Holder<Attribute> DOWN_ATTACK = register("down_attack", new RangedAttribute("attribute.name.down_attack", 0.5, 0.0, 10.24D).setSyncable(true));

    private static Holder<Attribute> register(String id, Attribute attribute) {
        Holder<Attribute> entry = Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, Peony.id(id), attribute);
        ENTRIES.add(entry);
        return entry;
    }
}
