package net.r266.xyz;

import net.r266.xyz.Commands.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.*;

public class XYZcopierClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
      ClientCommandRegistrationCallback[] ListOfCommands = {
              (dispatcher, commandRegistryAccess) -> CopyCoordinate.register(dispatcher),
      };
      for (ClientCommandRegistrationCallback command : ListOfCommands) {
        ClientCommandRegistrationCallback.EVENT.register(command);
      }
    }
}
