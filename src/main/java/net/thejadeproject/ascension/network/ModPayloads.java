package net.thejadeproject.ascension.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.events.karma.KarmaSyncPayload;

import net.thejadeproject.ascension.network.clientBound.*;

import net.thejadeproject.ascension.network.serverBound.*;
import net.thejadeproject.ascension.network.serverBound.input.ChangePlayerInputState;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.SyncEntityForm;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.skills.casting.SyncCastingInstance;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.skills.casting.SyncSlot;
import net.thejadeproject.ascension.refactor_packages.network.server_bound.skills.ClearSlot;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.skills.SyncHeldSkills;
import net.thejadeproject.ascension.refactor_packages.network.server_bound.cultivation.TriggerBreakthrough;
import net.thejadeproject.ascension.refactor_packages.network.server_bound.skills.SetActiveSlot;
import net.thejadeproject.ascension.refactor_packages.network.server_bound.skills.UpdateSkillSlot;


public class ModPayloads {
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(AscensionCraft.MOD_ID).versioned("1.0");

        //=================================== CLIENT======================================
        registrar.playToClient(
                SyncHeldSkills.TYPE,
                SyncHeldSkills.STREAM_CODEC,
                SyncHeldSkills::handlePayload
        );
        registrar.playToClient(
                SyncEntityForm.TYPE,
                SyncEntityForm.STREAM_CODEC,
                SyncEntityForm::handlePayload
        );
        registrar.playToClient(
                OpenKarmicLedgerScreen.TYPE,
                OpenKarmicLedgerScreen.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        if (context.flow().isClientbound()) {
                            // Only handle on client
                            net.thejadeproject.ascension.clients.ClientPacketHandler.handleOpenKarmicLedgerScreen(payload);
                        }
                    });
                }
        );

        registrar.playToClient(
                KarmaSyncPayload.TYPE,
                KarmaSyncPayload.STREAM_CODEC,
                KarmaSyncPayload::handle
        );
        registrar.playToClient(
                SyncSlot.TYPE,
                SyncSlot.STREAM_CODEC,
                SyncSlot::handlePayload
        );
        registrar.playToClient(
                SyncCastingInstance.TYPE,
                SyncCastingInstance.STREAM_CODEC,
                SyncCastingInstance::handlePayload
        );

        //===================================== SERVER ==================================



        registrar.playToServer(
                ToggleTabletDropModePayload.TYPE,
                ToggleTabletDropModePayload.STREAM_CODEC,
                ToggleTabletDropModePayload::handlePayload
        );

        registrar.playToServer(
                UpdateSkillSlot.TYPE,
                UpdateSkillSlot.STREAM_CODEC,
                UpdateSkillSlot::handlePayload
        );
        registrar.playToServer(
                ClearSlot.TYPE,
                ClearSlot.STREAM_CODEC,
                ClearSlot::handlePayload
        );


        registrar.playToServer(
                ChangePlayerInputState.TYPE,
                ChangePlayerInputState.STREAM_CODEC,
                ChangePlayerInputState::handlePayload

        );
        registrar.playToServer(
                SetActiveSlot.TYPE,
                SetActiveSlot.STREAM_CODEC,
                SetActiveSlot::handlePayload

        );
        registrar.playToServer(
                TriggerBreakthrough.TYPE,
                TriggerBreakthrough.STREAM_CODEC,
                TriggerBreakthrough::handlePayload
        );



    }
}
