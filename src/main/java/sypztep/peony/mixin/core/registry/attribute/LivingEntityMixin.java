package sypztep.peony.mixin.core.registry.attribute;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sypztep.peony.common.init.ModAttributes;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void registryExtraStats(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        for (Holder<Attribute> entry : ModAttributes.ENTRIES) cir.getReturnValue().add(entry);
    }
}
