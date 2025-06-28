package sypztep.peony.common.init;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import sypztep.peony.client.payload.AddTextParticlesPayloadS2C;

public final class ModPayload {
    public static void initPayload(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0.0").executesOn(HandlerThread.NETWORK);

        registrar.playToClient(AddTextParticlesPayloadS2C.TYPE, AddTextParticlesPayloadS2C.STREAM_CODEC, AddTextParticlesPayloadS2C::handle);
    }
}
