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
Query: matk:[161 TO *] AND spd:[161 TO *]

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
Query: compatible_move:(+bite +"trap jaw" +"stab in the dark" +("deja vu" OR "two heads") +(broadcast OR echolocation))

================================================================================
 #110 Kuneko [Air]
 https://wiki.cassettebeasts.com/wiki/Kuneko
  HP | M. Atk | M. Def | R. Atk | R. Def | Speed | Total
 115 |    115 |    115 |    115 |    115 |   115 |   690
================================================================================
 #111 Shining Kuneko [Astral]
 https://wiki.cassettebeasts.com/wiki/Shining_Kuneko
  HP | M. Atk | M. Def | R. Atk | R. Def | Speed | Total
 140 |    140 |    140 |    140 |    140 |   140 |   840
================================================================================
```

### Moves

#### Filter moves by power

```
Query: power:[100 TO *] AND target:team

================================================================================
 Cosmic Kunai [Ranged Attack]
 https://wiki.cassettebeasts.com/wiki/Cosmic_Kunai
 Hits a whole team. Destroys walls.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
    Astral |   10 |   150 |     1 |         100 |        0 | Team
================================================================================
```

#### Filter moves by species

```
Query: compatible_species:beanstalker AND status_effect:defence AND status_effect_kind:buff

================================================================================
 Defend [Status Effect]
 https://wiki.cassettebeasts.com/wiki/Defend
 Raises the user’s Melee Defence.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
  Typeless |    1 |       |       |         100 |        0 | Self
 Status Effects: Melee Defence Up [Buff]
================================================================================
 Raise Shields [Status Effect]
 https://wiki.cassettebeasts.com/wiki/Raise_Shields
 Raises the user’s Ranged Defence.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
  Typeless |    1 |       |       |         100 |        0 | Self
 Status Effects: Ranged Defence Up [Buff]
================================================================================
 Treat [Status Effect]
 https://wiki.cassettebeasts.com/wiki/Treat
 Gives the target a random buff.
      Type | Cost | Power |  Hits |    Accuracy | Priority | Target
  Typeless |    1 |       |       | Unavoidable |        0 | Single
 Status Effects: Accuracy Up [Buff]
                 AP Boost [Buff]
                 Cottoned On [Buff]
                 Evasion Up [Buff]
                 Glitter Coating [Transmutation]
                 Healing Leaf [Buff]
                 Healing Steam [Buff]
                 Locked On [Buff]
                 Melee Attack Up [Buff]
                 Melee Defence Up [Buff]
                 Mind-Meld [Buff]
                 Multistrike [Buff]
                 Multitarget [Buff]
                 Parry Stance [Buff]
                 Ranged Attack Up [Buff]
                 Ranged Defence Up [Buff]
                 Speed Up [Buff]
================================================================================
```

## Index

### Species

| Index name      | Description                                                             |
|-----------------|-------------------------------------------------------------------------|
| species_num     | The species' number, formatted as an int                                |
| species_name    | The name of the species                                                 |
| species_type    | The type of the species                                                 |
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

| Index name         | Description                                                                                                          |
|--------------------|----------------------------------------------------------------------------------------------------------------------|
| move_name          | The name of the move                                                                                                 |
| move_desc          | The description of the move                                                                                          |
| move_type          | The elemental type of the move                                                                                       |
| move_cat           | The category of the move: `melee attack`, `ranged attack`, `status effect`, `misc`, `active`, `passive`, `automated` |
| power              | The moves base damage                                                                                                |
| min_hits           | The minimum number of hits the move can make                                                                         |
| max_hits           | The maximum number of hits the move can make                                                                         |
| avoidable          | Whether the move is avoidable: `true` or `false`                                                                     |
| accuracy           | The percentage chance to hit, only if the move is avoidable                                                          |
| cost               | The number of AP the move costs                                                                                      |
| target             | Who the move affects: `single`, `team`, `self`, `single ally`, `all`, `all except self`                              |
| copyable           | Whether the move is copyable: `true` or `false`                                                                      |
| priority           | The numeric move priority value. 0 is normal. Greater than 0 is higher priority, and less is lower.                  |
| status_effect      | The name of the status effect the move causes. See https://wiki.cassettebeasts.com/wiki/Status_Effects               |
| status_effect_kind | The kind of the status effect: `buff`, `debuff`, `transmutation`, `misc`                                             |
| compatible_species | The name of a species that is compatible with the move                                                               |
