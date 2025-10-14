package net.r266.xyz;

import java.util.function.Function;
import oshi.util.tuples.Pair;
import java.util.HashMap;
import java.util.Map;

public class Utilities {
  public static Pair<Boolean, Function<Double, Double>> IsFlagValid(String CheckFlag) {
    Map<String, Function<Double, Double>> FlagsList = new HashMap<>() {{
      put("-ru", Math::ceil);
      put("-rd", Math::floor);
      put("-rti", Math::rint);
      put("-ctn", (n) -> n/8);
    }};
    return new Pair<>(true, FlagsList.getOrDefault(CheckFlag.toLowerCase(), null));
  }
}