# _XYZ_ Copier <u>Documentation</u>:

## Command(*s*)
- `CopyCoordinate`:
  - `Usage`: `/CopyCoordinate <axis> [<includeHeaders>] <flags>` 
  - `Exmaple Usage`: `/CopyCoordinate XZ true` -> Expected result: `X: 100.000 Z: 50.000`
  ### Argument(*s*):
    1. `<axis>` **(required)**
    2. `[<includeHeaders>]` **(optional)** 
    3. `<flags>` **(optional)**:
       1. you can use the flags more the once like: `/CopyCoordinate XZ true -rti -ctn` -> `X: 12.000 Z: 1253.000 (rounds to the nearest whole number then divide by 8)`
       - `-ru` |> `Description`: rounds up the selected axis.
       - `-rd` |> `Description`: rounds down the selected axis.
       - `-rti` |> `Description`: rounds to the nearest whole number selected axis.
       - `-ctn` |> `Description`: Converts the overworld axis to nether axis (divided by 8).


### _Made by r266._
