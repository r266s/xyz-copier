package net.r266.xyz.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

public class CopyCoordinate {
  public static String Arg1 = "Axis";
  public static String Arg2 = "WithAxisHeaders";

  public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
    dispatcher.register(ClientCommandManager.literal("CopyCoordinate")
            .then(ClientCommandManager.argument(Arg1, StringArgumentType.string())
                    .then(ClientCommandManager.argument(Arg2, BoolArgumentType.bool()).executes(CopyCoordinate::Run))));
  }

  private static boolean IsCorrectAxis(char Axi, String AllowedAxis) {
    for (char AllowedAxi : AllowedAxis.toCharArray()) {
      if (Axi == AllowedAxi) {
        return true;
      }
    }
    return false;
  }

  private static int Run(CommandContext<FabricClientCommandSource> ClientCommandSourceCommandContext) throws CommandSyntaxException {
    String AllowedAxis = "XYZ".toUpperCase();
    String SelectedAxis = StringArgumentType.getString(ClientCommandSourceCommandContext, Arg1).toUpperCase();
    boolean AddAxisHeaders = BoolArgumentType.getBool(ClientCommandSourceCommandContext, Arg2);

    FabricClientCommandSource ClientCommandSource = ClientCommandSourceCommandContext.getSource();
    Entity Player = ClientCommandSource.getEntity();

    if (Player == null) {
      ClientCommandSource.sendFeedback(Text.literal("Player not exist"));
      return -1;
    }

    if (SelectedAxis.length() <= 3 && !SelectedAxis.isEmpty()) {
      String Coords = "";

      for (char SelectedAxi : SelectedAxis.toCharArray()) {
        if (IsCorrectAxis(SelectedAxi, AllowedAxis)) {
          double CorrectAxis = 0.00;

          if (SelectedAxi == 'X') {
            CorrectAxis = Player.getX();
          } else if (SelectedAxi == 'Z') {
            CorrectAxis = Player.getZ();
          } else if (SelectedAxi == 'Y'){
            CorrectAxis = Player.getY();
          }

          Coords += (AddAxisHeaders) ? "%s: %.2f ".formatted(SelectedAxi, CorrectAxis) : "%.2f ".formatted(CorrectAxis);
        } else {
          ClientCommandSource.sendFeedback(Text.literal("'%s' is not axis!".formatted(SelectedAxi)));
          return -1;
        }
      }

      String finalCoords = Coords;
      ClientCommandSource.sendFeedback(Text.literal(finalCoords));

      return 1;
    } else {
      if (SelectedAxis.length() > 3) {
        ClientCommandSource.sendFeedback(Text.literal("Trying to access the 4th dimension?"));
      } else {
        ClientCommandSource.sendFeedback(Text.literal("Select a Axis!"));
      }
      return -1;
    }
  }
}
