# Cassette Beasts Explorer

Currently, this project is a searchable index of Cassette Beast species and moves. I may or may not create a more
feature rich GUI for it in the future.

All of the data used in this project was sourced from the excellent [Cassette Beasts wiki](https://wiki.cassettebeasts.com).

## Install

Java 17 or later is required.

### Prebuilt

Download the latest prebuilt `cbe-cli.jar` from the [releases page](https://github.com/pwinckles/cassette-beasts-explorer/releases).

### Build from source

Checkout this project and execute the following within the project directory:

```shell
./mvnw -pl cli -am clean package
```

This will produce an executable jar at `cli/target/cbe-cli.jar`

## Usage

Run the CLI by executing:

```shell
java -jar cbe-cli.jar
```

From there, you can execute queries against the index. Use `exit` or `quit` to terminate the CLI, and `help` to
display useful info about the indices.

Note: I **strongly** recommend using [rlwrap](https://github.com/hanslub42/rlwrap), which will allow you to use the
up arrow key to edit previous queries. To do this, start the CLI like: `rlwrap java -jar cbe-cli.jar`

## Query Examples

The data is indexed using Lucene, and queries must be written using Lucene's query syntax. Refer to the
[Lucene documentation](https://lucene.apache.org/core/9_7_0/queryparser/org/apache/lucene/queryparser/flexible/standard/StandardQueryParser.html) for details.

### Species

#### Filter species by attributes

```
Query: matk:[161 TO *] AND spd:[161 TO *] AND bootleg:false

Matches: 5

================================================================================
 #003 Ripterra [Beast]
 https://wiki.cassettebeasts.com/wiki/Ripterra
  HP | M. Atk | M. Def | R. Atk | R. Def | Speed | Total
 120 |    200 |    100 |     90 |    120 |   170 |   800
================================================================================
 #033 Tokusect [Air]
 https://wiki.cassettebeasts.com/wiki/Tokusect
  HP | M. Atk | M. Def | R. Atk | R. Def | Speed | Total
 120 |    170 |    120 |    100 |    120 |   170 |   800
================================================================================
 #051 Thwackalope [Air]
 https://wiki.cassettebeasts.com/wiki/Thwackalope
  HP | M. Atk | M. Def | R. Atk | R. Def | Speed | Total
 110 |    180 |    110 |    110 |    110 |   180 |   800
================================================================================
 #090 Beanstalker [Plant]
 https://wiki.cassettebeasts.com/wiki/Beanstalker
  HP | M. Atk | M. Def | R. Atk | R. Def | Speed | Total
 140 |    185 |    110 |    100 |    100 |   165 |   800
================================================================================
 #107 Cryoshear [Ice]
 https://wiki.cassettebeasts.com/wiki/Cryoshear
  HP | M. Atk | M. Def | R. Atk | R. Def | Speed | Total
 150 |    180 |     90 |     90 |     90 |   200 |   800
================================================================================
```

#### Filter species by compatible moves

```
Query: compatible_move:(+bite +"trap jaw" +"stab in the dark" +("deja vu" "two heads") +(broadcast echolocation)) AND remaster_to:none AND bootleg:false

Matches: 1

================================================================================
 #111 Shining Kuneko [Astral]
 https://wiki.cassettebeasts.com/wiki/Shining_Kuneko
  HP | M. Atk | M. Def | R. Atk | R. Def | Speed | Total
 140 |    140 |    140 |    140 |    140 |   140 |   840
================================================================================
```

### Moves

#### Filter moves by species

```
Query: compatible_species:beanstalker AND move_cat:melee AND cost:2

Matches: 4

================================================================================
 Silicon Slash [Melee Attack]
 https://wiki.cassettebeasts.com/wiki/Silicon_Slash
 Hits one target.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
     Glass |    2 |    60 |     1 |         100 |        0 | Single
================================================================================
 Toy Hammer [Melee Attack]
 https://wiki.cassettebeasts.com/wiki/Toy_Hammer
 Hits one target.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
   Plastic |    2 |    60 |     1 |         100 |        0 | Single
================================================================================
 Double Smack [Melee Attack]
 https://wiki.cassettebeasts.com/wiki/Double_Smack
 Hits one target multiple times.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
  Typeless |    2 |    30 |     2 |         100 |        0 | Single
================================================================================
 Wallop [Melee Attack]
 https://wiki.cassettebeasts.com/wiki/Wallop
 Hits one target.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
  Typeless |    2 |    60 |     1 |         100 |        0 | Single
================================================================================
```

#### Filter for moves that are unique to a bootleg

```
Query: compatible_bootleg:"beanstalker lightning" AND NOT compatible_species:beanstalker

Matches: 10

================================================================================
 Lightning Bolt [Ranged Attack]
 https://wiki.cassettebeasts.com/wiki/Lightning_Bolt
 Hits one target.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
 Lightning |    3 |    90 |     1 |         100 |        0 | Single
================================================================================
 Thunder Blast [Ranged Attack]
 https://wiki.cassettebeasts.com/wiki/Thunder_Blast
 Hits one target. The target is hit by an additional 30 Air-type damage.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
 Lightning |    3 |    60 |     1 |         100 |        0 | Single
================================================================================
 Lightning Wall [Status Effect]
 https://wiki.cassettebeasts.com/wiki/Lightning_Wall_(move)
 Sacrifice 20% of HP to create a wall that will absorb up to 3 hits for up to 3
 turns.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
 Lightning |    2 |       |       |         100 |        0 | Single
 Status Effects: Lightning Wall [Buff]
================================================================================
 Charge [Melee Attack]
 https://wiki.cassettebeasts.com/wiki/Charge
 Hits one target.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
 Lightning |    2 |    60 |     1 |         100 |        0 | Single
================================================================================
 Unicast [Status Effect]
 https://wiki.cassettebeasts.com/wiki/Unicast
 Gives the target Unitarget status for 3 turn(s).
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
 Lightning |    2 |       |       |         100 |        0 | Single
 Status Effects: Unitarget [Debuff]
================================================================================
 Lightning Coating [Status Effect]
 https://wiki.cassettebeasts.com/wiki/Lightning_Coating_(move)
 Changes the type of the user or an ally.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
 Lightning |    2 |       |       |         100 |        0 | Single Ally
 Status Effects: Lightning Coating [Transmutation]
================================================================================
 Broadcast [Status Effect]
 https://wiki.cassettebeasts.com/wiki/Broadcast
 Gives the user Multitarget status for 3 turn(s).
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
       Air |    2 |       |       |         100 |        0 | Self
 Status Effects: Multitarget [Buff]
================================================================================
 Magnet [Automated, Status Effect]
 https://wiki.cassettebeasts.com/wiki/Magnet
 Used automatically at the start of battle for 0 AP. Prevents opponents
 targeting user’s allies for 3 turn(s).
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
     Metal |    1 |       |       |         100 |        0 | Self
 Status Effects: Intercepting [Buff]
================================================================================
 Battery [Melee Attack]
 https://wiki.cassettebeasts.com/wiki/Battery
 Hits one target twice. Lands critical hits if used after Charge.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
 Lightning |    3 |    40 |     2 |         100 |        0 | Single
================================================================================
 Grounded [Passive, Misc]
 https://wiki.cassettebeasts.com/wiki/Grounded
 Passively reduces incoming Lightning-type damage by 50%, and negates any
 reactions that would occur. Does not affect critical hits.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
 Lightning |    0 |       |       |         100 |        0 | Self
================================================================================
```

## Index

### Species

| Index name      | Description                                                             |
|-----------------|-------------------------------------------------------------------------|
| species_num     | The species' number, formatted as an int                                |
| species_name    | The name of the species                                                 |
| species_type    | The type of the species                                                 |
| bootleg         | Indicates if the species is a bootleg: `true` or `false`                |
| hp              | The base HP attribute value                                             |
| matk            | The base melee attack attribute value                                   |
| mdef            | The base melee defense attribute value                                  |
| ratk            | The base ranged attack attribute value                                  |
| rdef            | The base ranged defense attribute value                                 |
| spd             | The base speed attribute value                                          |
| attr_sum        | The sum of all of the base attribute values                             |
| ap              | The number of available AP                                              |
| slots           | The number of available move slots                                      |
| compatible_move | The name of a move that is compatible with the species                  |
| remaster_from   | The name of the prior form of the species, or `none`.                   |
| remaster_to     | The name of the species this species may be remastered into, or `none`. |

### Moves

| Index name         | Description                                                                                                                                                 |
|--------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| move_name          | The name of the move                                                                                                                                        |
| move_desc          | The description of the move                                                                                                                                 |
| move_type          | The elemental type of the move                                                                                                                              |
| move_cat           | The category of the move: `melee attack`, `ranged attack`, `status effect`, `misc`, `active`, `passive`, `automated`                                        |
| power              | The moves base damage                                                                                                                                       |
| min_hits           | The minimum number of hits the move can make                                                                                                                |
| max_hits           | The maximum number of hits the move can make                                                                                                                |
| avoidable          | Whether the move is avoidable: `true` or `false`                                                                                                            |
| accuracy           | The percentage chance to hit, only if the move is avoidable                                                                                                 |
| cost               | The number of AP the move costs                                                                                                                             |
| target             | Who the move affects: `single`, `team`, `self`, `single ally`, `all`, `all except self`                                                                     |
| copyable           | Whether the move is copyable: `true` or `false`                                                                                                             |
| priority           | The numeric move priority value. 0 is normal. Greater than 0 is higher priority, and less is lower.                                                         |
| status_effect      | The name of the status effect the move causes. See https://wiki.cassettebeasts.com/wiki/Status_Effects                                                      |
| status_effect_kind | The kind of the status effect: `buff`, `debuff`, `transmutation`, `misc`                                                                                    |
| compatible_species | The name of a species that is compatible with the move                                                                                                      |
| compatible_bootleg | The name of a bootleg species that is compatible with the move. Values are the species name followed by the bootleg type. For example: `Thwackalope Astral` |
