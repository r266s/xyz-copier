package net.r266.xyz.Commands;

import java.util.Map;
import java.util.HashMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import static net.minecraft.text.Text.literal;
import static net.r266.xyz.ModChecks.Utilities.IsFlagValid;

public class CopyCoordinate {
  public static String Arg1 = "Axis";
  public static String Arg2 = "WithAxisHeaders";
  public static String Arg3 = "flags";

  public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
    dispatcher.register(ClientCommandManager.literal("CopyCoordinate")
            .then(ClientCommandManager.argument(Arg1, StringArgumentType.string()).executes(CopyCoordinate::Run)
                    .then(ClientCommandManager.argument(Arg2, BoolArgumentType.bool()).executes(CopyCoordinate::Run)
                            .then(ClientCommandManager.argument(Arg3, StringArgumentType.greedyString()).executes(CopyCoordinate::Run)))));

  }

  private static int OnCommandSendError(String msg, FabricClientCommandSource Source) {
    Source.sendError(literal(msg));
    return -1;
  }

  private static int Run(CommandContext<FabricClientCommandSource> ClientCommandSourceCommandContext) {
    String AllowedAxis = "XYZ".toUpperCase();
    String SelectedAxis = StringArgumentType.getString(ClientCommandSourceCommandContext, Arg1).toUpperCase();

    FabricClientCommandSource ClientCommandSource = ClientCommandSourceCommandContext.getSource();
    Entity Player = ClientCommandSource.getEntity();

    MinecraftClient WindowClient = MinecraftClient.getInstance();
    StringBuilder finalAxis = new StringBuilder();

    int temp_int = 1;
    String CommandFlags = "";
    boolean AddAxisHeaders = false;

    try {
      AddAxisHeaders = BoolArgumentType.getBool(ClientCommandSourceCommandContext, Arg2);
    } catch (Exception e) {
      if (!(e instanceof IllegalArgumentException)) {
        return OnCommandSendError(e.getMessage(), ClientCommandSource);
      }
    }

    try {
      CommandFlags = StringArgumentType.getString(ClientCommandSourceCommandContext, Arg3).toLowerCase();
    } catch (Exception e) {
      if (!(e instanceof IllegalArgumentException)) {
        return OnCommandSendError(e.getMessage(), ClientCommandSource);
      }
    }

    if (SelectedAxis.length() <= 3 && !SelectedAxis.isEmpty()) {
      HashMap<Object, Object> ListOfAxis = new HashMap<>();
      for (char SelectedAxi : SelectedAxis.toCharArray()) {
        if (AllowedAxis.contains(String.valueOf(SelectedAxi))) {
          double CorrectAxis = 0.00;
          int order = 0;

          switch (SelectedAxi) {
            case 'X':
              CorrectAxis = Player.getX();
              break;
            case 'Z':
              CorrectAxis = Player.getZ();
              order = 2;
              break;
            case 'Y':
              CorrectAxis = Player.getY();
              order = 1;
          }

          if (!CommandFlags.isEmpty()) {
            for (String f : CommandFlags.split(" ")) {
              var result = IsFlagValid(f);
              if (result.getA() && result.getB() != null) {
                CorrectAxis = result.getB().apply(CorrectAxis);
              } else {
                return OnCommandSendError("'%s' is not a flag!".formatted(f), ClientCommandSource);
              }
            }
          }

          String AddAxis = (AddAxisHeaders) ? "%s: %.3f".formatted(SelectedAxi, CorrectAxis) : "%.3f".formatted(CorrectAxis);
          ListOfAxis.put(order, AddAxis);
        } else {
          return OnCommandSendError("'%s' is not axis!".formatted(SelectedAxi), ClientCommandSource);
        }
      }

      for (Map.Entry<Object, Object> entry: ListOfAxis.entrySet()) {
        String temp = (temp_int < ListOfAxis.size()) ? " " : "";
        finalAxis.append(entry.getValue());
        finalAxis.append(temp);
        temp_int++;
      }

      if (WindowClient != null) {
        WindowClient.keyboard.setClipboard(finalAxis.toString());
        ClientCommandSource.sendFeedback(literal("Successfully copied to clipboard!")
                .formatted(Formatting.GREEN, Formatting.BOLD));
      } else {
        ClientCommandSource.sendFeedback(
                literal("Cannot Access clipboard, Click me to copy the " + SelectedAxis + ".")
                        .setStyle(Style.EMPTY.withClickEvent(new ClickEvent.CopyToClipboard(finalAxis.toString()))));
      }

      return 1;
    } else {
      String MG = (SelectedAxis.length() > 3) ? "Trying to access the 4th dimension?" : "Select a Axis!";
      return OnCommandSendError(MG, ClientCommandSource);
    }
  }
}
