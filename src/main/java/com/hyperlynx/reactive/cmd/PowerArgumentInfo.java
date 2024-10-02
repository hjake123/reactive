package com.hyperlynx.reactive.cmd;

import com.google.gson.JsonObject;
import com.hyperlynx.reactive.Registration;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class PowerArgumentInfo implements ArgumentTypeInfo<PowerArgumentType, PowerArgumentInfo.Template> {
    @Override
    public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
        // No need.
    }

    @Override
    public Template deserializeFromNetwork(FriendlyByteBuf buffer) {
        return new Template();
    }

    @Override
    public void serializeToJson(Template template, JsonObject json) {
        // No need.
    }

    @Override
    public Template unpack(PowerArgumentType power_argument) {
        return new Template();
    }

    public final class Template implements ArgumentTypeInfo.Template<PowerArgumentType> {
        @Override
        public PowerArgumentType instantiate(CommandBuildContext context) {
            return new PowerArgumentType();
        }

        @Override
        public ArgumentTypeInfo<PowerArgumentType, ?> type() {
            return Registration.POWER_ARGUMENT.value();
        }
    }
}
