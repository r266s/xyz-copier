package net.r266.xyz.Commands;

import java.util.Map;
import java.util.HashMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import static net.minecraft.text.Text.literal;
import static net.r266.xyz.ModChecks.Utilities.IsFlagVaild;

public class CopyCoordinate {
  public static String Arg1 = "Axis";
  public static String Arg2 = "WithAxisHeaders";
  public static String Arg3 = "flags";

  public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
    dispatcher.register(ClientCommandManager.literal("CopyCoordinate")
            .then(ClientCommandManager.argument(Arg1, StringArgumentType.string()).executes(CopyCoordinate::Run)
                    .then(ClientCommandManager.argument(Arg2, BoolArgumentType.bool()).executes(CopyCoordinate::Run)
                            .then(ClientCommandManager.argument(Arg3, StringArgumentType.string()).executes(CopyCoordinate::Run)))));

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

    String CommandFlags = "";
    boolean AddAxisHeaders;

    try {
      AddAxisHeaders = BoolArgumentType.getBool(ClientCommandSourceCommandContext, Arg2);
    } catch (IllegalArgumentException e) {
      AddAxisHeaders = false;
    }

    try {
      CommandFlags = StringArgumentType.getString(ClientCommandSourceCommandContext, Arg3).toLowerCase();
    } catch (IllegalArgumentException ignored) {}

    FabricClientCommandSource ClientCommandSource = ClientCommandSourceCommandContext.getSource();
    Entity Player = ClientCommandSource.getEntity();

    if (SelectedAxis.length() <= 3 && !SelectedAxis.isEmpty()) {
      HashMap<Object, Object> Coords = new HashMap<>();

      for (char SelectedAxi : SelectedAxis.toCharArray()) {
        if (IsCorrectAxis(SelectedAxi, AllowedAxis)) {
          double CorrectAxis = 0.00;
          int order = 0;

          if (SelectedAxi == 'X') {
            CorrectAxis = Player.getX();
          } else if (SelectedAxi == 'Z') {
            CorrectAxis = Player.getZ();
            order = 2;
          } else if (SelectedAxi == 'Y'){
            CorrectAxis = Player.getY();
            order = 1;
          }

          if (!CommandFlags.isEmpty()) {
            var result = IsFlagVaild(CommandFlags);
            if (result.getA() && result.getB() != null) {
              CorrectAxis = result.getB().apply(CorrectAxis);
            } else {
              ClientCommandSource.sendError(literal("'%s' is not a flag!".formatted(CommandFlags)));
              return -1;
            }
          }

          String AddAxis = (AddAxisHeaders) ? "%s: %.3f".formatted(SelectedAxi, CorrectAxis) : "%.3f".formatted(CorrectAxis);
          Coords.put(order, AddAxis);
        } else {
          ClientCommandSource.sendError(literal("'%s' is not axis!".formatted(SelectedAxi)));
          return -1;
        }
      }

      MinecraftClient WindowClient = MinecraftClient.getInstance();
      StringBuilder finalCoords = new StringBuilder();
      int temp_int = 1;

      for (Map.Entry<Object, Object> entry: Coords.entrySet()) {
        String temp = (temp_int < Coords.size()) ? " " : "";
        finalCoords.append(entry.getValue());
        finalCoords.append(temp);
        temp_int++;
      }

      if (WindowClient != null) {
        WindowClient.keyboard.setClipboard(finalCoords.toString());
        ClientCommandSource.sendFeedback(literal("Successfully copied to clipboard!")
                .formatted(Formatting.GREEN, Formatting.BOLD));
      } else {
        ClientCommandSource.sendFeedback(
                literal("Cannot Access clipboard, Click me to copy the " + SelectedAxis + ".")
                        .setStyle(Style.EMPTY.withClickEvent(new ClickEvent.CopyToClipboard(finalCoords.toString()))));
      }

      return 1;
    } else {
      if (SelectedAxis.length() > 3) {
        ClientCommandSource.sendError(literal("Trying to access the 4th dimension?"));
      } else {
        ClientCommandSource.sendError(literal("Select a Axis!"));
      }
      return -1;
    }
  }
}
