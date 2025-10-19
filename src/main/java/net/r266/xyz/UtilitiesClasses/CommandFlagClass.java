package net.r266.xyz.UtilitiesClasses;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;

public class CommandFlagClass {
  private int CurrentCharPos = 0;
  final private StringBuilder CurrentRenderingFlag = new StringBuilder();
  final private ArrayList<String> CorrectFlags = new ArrayList<>(), WrongFlags = new ArrayList<>();
  final private Map<String, Function<Double, Double>> CommandFlags = new HashMap<>() {{
    put("ru", Math::ceil);
    put("rd", Math::floor);
    put("rti", Math::rint);
    put("ctn", (n) -> n/8);
  }};
  
  private void NextChar() {
    this.CurrentCharPos++;
  }
  
  public Function<Double, Double> FlagFunction(String Flag) {
    return this.CommandFlags.getOrDefault(Flag.toLowerCase(), null);
  }
  
  private void CompleteCheck() {
    if (!this.CurrentRenderingFlag.isEmpty()) {
      if (this.CommandFlags.containsKey(this.CurrentRenderingFlag.toString())) {
        this.CorrectFlags.add(this.CurrentRenderingFlag.toString());
      } else {
        this.WrongFlags.add(this.CurrentRenderingFlag.toString());
      }
      this.CurrentRenderingFlag.setLength(0);
    } else {
      NextChar();
    }
  }
  
  public void ParseFlags(String UnfilteredFlags) {
    UnfilteredFlags = UnfilteredFlags.toLowerCase();
    
    while (UnfilteredFlags.length() > this.CurrentCharPos) {
      if (UnfilteredFlags.charAt(this.CurrentCharPos) == '-') {
        CompleteCheck();
      } else if (UnfilteredFlags.charAt(this.CurrentCharPos) == ' ') {
        NextChar();
      } else {
        this.CurrentRenderingFlag.append(UnfilteredFlags.charAt(this.CurrentCharPos));
        NextChar();
      }
    }
    
    CompleteCheck();
  }
  
  public ArrayList<String> CorrectFlags(){
    return this.CorrectFlags;
  }
  
  public ArrayList<String> WrongFlags(){
    return this.WrongFlags;
  }
}