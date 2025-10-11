package net.r266.xyz.ModChecks;

import oshi.util.tuples.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Utilities {
  public static Pair<Boolean, Function<Double, Double>> IsFlagValid(String CheckFlag) {
    Map<String, Function<Double, Double>> FlagsList = new HashMap<>() {{
      put("-ru", Math::ceil);
      put("-rd", Math::floor);
      put("-ctn", (n) -> n/8.0);
    }};
    return new Pair<>(true, FlagsList.getOrDefault(CheckFlag.toLowerCase(), null));
  }
}
