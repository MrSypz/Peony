package sypztep.peony.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import sypztep.peony.client.payload.ClientStatsDebugPayload;
import sypztep.peony.common.attachment.LivingEntityStatsAttachment;
import sypztep.peony.common.init.ModAttachments;

public class StatsDebugCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("statsdebug")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("info")
                        .executes(StatsDebugCommand::showDebugInfo)));
    }

    private static int showDebugInfo(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
            context.getSource().sendFailure(Component.literal("Only players can use this command"));
            return 0;
        }

        LivingEntityStatsAttachment serverStats = player.getData(ModAttachments.LIVINGSTATS.get());

        ClientStatsDebugPayload debugPayload = new ClientStatsDebugPayload(
                player.getId(),
                serverStats.getLevel(),
                serverStats.getXp(),
                serverStats.getXpToNextLevel(),
                serverStats.getStatPoints(),
                serverStats.getLevelSystem().isAtMaxLevel()
        );

        PacketDistributor.sendToPlayer(player, debugPayload);

        return 1;
    }
}