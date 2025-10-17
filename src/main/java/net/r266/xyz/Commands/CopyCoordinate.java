package net.r266.xyz.Commands;

import java.util.*;
import net.minecraft.text.*;
import net.minecraft.entity.Entity;
import java.util.function.Consumer;
import net.minecraft.util.Formatting;
import static net.r266.xyz.Utilities.*;
import static net.minecraft.text.Text.*;
import com.mojang.brigadier.arguments.*;
import net.minecraft.client.MinecraftClient;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.*;
import com.mojang.brigadier.context.CommandContext;

public class CopyCoordinate {
  final private static String Arg1 = "axis", Arg2 = "includeHeaders", Arg3 = "flags";
  public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
    dispatcher.register(ClientCommandManager.literal("CopyCoordinate")
            .then(ClientCommandManager.argument(Arg1, StringArgumentType.string()).executes(CopyCoordinate::Run)
                    .then(ClientCommandManager.argument(Arg2, BoolArgumentType.bool()).executes(CopyCoordinate::Run)
                            .then(ClientCommandManager.argument(Arg3, StringArgumentType.greedyString()).executes(CopyCoordinate::Run)))));

  }

  private static int OnCommandSendFeedback(int FeedBackType, MutableText msg, FabricClientCommandSource Source) {
    int FormatType = (FeedBackType < 1) ? -1 : 1;
    
    Map<Integer, Consumer<Text>> ListOfFeedBackType = new HashMap<>();
    ListOfFeedBackType.put(1, Source::sendFeedback);
    ListOfFeedBackType.put(-1, Source::sendError);
    
    Consumer<Text> Result = ListOfFeedBackType.get(FormatType);
    
    Result.accept(msg);
    return FormatType;
  }
  
  private static int Run(CommandContext<FabricClientCommandSource> ClientCommandContext) {
    FabricClientCommandSource ClientCommandSource = ClientCommandContext.getSource();
    MinecraftClient WindowClient = MinecraftClient.getInstance();
    Entity PlayerClient = ClientCommandSource.getEntity();
    
    String InputAxis = StringArgumentType.getString(ClientCommandContext, Arg1).toUpperCase();
    StringBuilder FormattedCoordinates = new StringBuilder();
    String[] CommandEachFlag = null; String CommandFlags;
    boolean IncludeHeaders = false;
    
    try {
      IncludeHeaders = BoolArgumentType.getBool(ClientCommandContext, Arg2);
      CommandFlags = StringArgumentType.getString(ClientCommandContext, Arg3).toLowerCase();
      CommandEachFlag = (!CommandFlags.isEmpty()) ? CommandFlags.split(" ") : null;
    } catch (Exception e) {
      if (!(e instanceof IllegalArgumentException)) {
        return OnCommandSendFeedback(-1, literal(e.getMessage()), ClientCommandSource);
      }
    }
    
    if (InputAxis.length() <= 3 && !InputAxis.isEmpty()) {
      HashMap<Object, Object> CoordinateMap = new HashMap<>();
      double CurrentAxisValue; int AxisOrder = 0;
      
      for (char AxisChar : InputAxis.toCharArray()) {
        switch (AxisChar) {
          case 'X': CurrentAxisValue = PlayerClient.getX(); break;
          case 'Y': CurrentAxisValue = PlayerClient.getY(); AxisOrder = 1; break;
          case 'Z': CurrentAxisValue = PlayerClient.getZ(); AxisOrder = 2; break;
          default: return OnCommandSendFeedback(-1, literal("Unknown axis: '%s'".formatted(AxisChar)), ClientCommandSource);
        }
        
        if (CommandEachFlag != null) {
          for (String AssignedFlag : CommandEachFlag) {
            var result = IsFlagValid(AssignedFlag); var IsValid = result.getA(); var FlagFunc = result.getB();
            
            if (IsValid && (FlagFunc != null)) {
              CurrentAxisValue = FlagFunc.apply(CurrentAxisValue);
            } else {
              return OnCommandSendFeedback(-1, literal("'%s' is not a flag!".formatted(AssignedFlag)), ClientCommandSource);
            }
          }
        }
        
        String FormattedAxis = "%.3f".formatted(CurrentAxisValue);
        FormattedAxis = (IncludeHeaders) ? "%s: ".formatted(AxisChar) + FormattedAxis : FormattedAxis;
        CoordinateMap.put(AxisOrder, FormattedAxis); AxisOrder = 0;
      }
      
      var CoordinateEntries = new ArrayList<>(CoordinateMap.entrySet());
      
      for (int i = 0; i < CoordinateEntries.size(); i++) {
        String doSpace = ((i+1) < CoordinateMap.size()) ? " " : "";
        FormattedCoordinates.append(CoordinateEntries.get(i).getValue()).append(doSpace);
      }
      
      MutableText SuccessfulMessage;
      
      if (WindowClient != null) {
        WindowClient.keyboard.setClipboard(FormattedCoordinates.toString());
        SuccessfulMessage = literal("Successfully copied to clipboard!").formatted(Formatting.GREEN, Formatting.BOLD);
      } else {
        SuccessfulMessage = literal("Cannot Access clipboard, Click this message to copy the axis.")
                .setStyle(Style.EMPTY.withClickEvent(new ClickEvent.CopyToClipboard(FormattedCoordinates.toString())));
      }

      return OnCommandSendFeedback(1, SuccessfulMessage, ClientCommandSource);
    } else {
      String MG = (InputAxis.length() > 3) ? "Trying to access the 4th dimension?" : "Select a Axis!";
      return OnCommandSendFeedback(-1, literal(MG), ClientCommandSource);
    }
  }
}