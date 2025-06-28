package sypztep.peony.client.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import sypztep.peony.Peony;
import sypztep.peony.client.util.TextParticleProvider;

public record AddTextParticlesPayloadS2C(int entityId, int selector) implements CustomPacketPayload {

    public static final Type<AddTextParticlesPayloadS2C> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Peony.MODID, "add_text_particle"));

    public static final StreamCodec<ByteBuf, AddTextParticlesPayloadS2C> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, AddTextParticlesPayloadS2C::entityId,
                    ByteBufCodecs.UNSIGNED_SHORT, AddTextParticlesPayloadS2C::selector,
                    AddTextParticlesPayloadS2C::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void send(ServerPlayer player, int entityId, TextParticleProvider selector) {
        PacketDistributor.sendToPlayer(player, new AddTextParticlesPayloadS2C(entityId, selector.getFlag()));
    }

    public static void handle(AddTextParticlesPayloadS2C payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                Entity entity = context.player().level().getEntity(payload.entityId());
                if (entity != null)TextParticleProvider.handleParticle(entity, payload.selector());
            }
        });
    }
}