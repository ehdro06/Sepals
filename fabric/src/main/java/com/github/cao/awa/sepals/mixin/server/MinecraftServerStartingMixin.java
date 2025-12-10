package com.github.cao.awa.sepals.mixin.server;

import com.github.cao.awa.sepals.event.server.ServerStartingEvent;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerStartingMixin {
    @Inject(
            method = "runServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z",
                    shift = At.Shift.AFTER
            )
    )
    public void init(CallbackInfo ci) {
        ServerStartingEvent.trigger((MinecraftServer) (Object) this);
    }
}
