# Sepals

An extremely radical and experimental optimization mod for Minecraft server performances.

We recommend using Sepals with [Lithium](https://modrinth.com/mod/lithium) and [Async](https://modrinth.com/mod/async)
for optimal performance.

![](https://count.getloli.com/@@cao-awa.sepals?name=%40cao-awa.sepals&theme=rule34&padding=7&offset=0&align=top&scale=1&pixelated=1&darkmode=auto)

## Foreword

First,
the Sepals is a collection of highly experimental server-side optimizations that target very specific performance
bottlenecks in Minecraft’s AI, entity processing, and task systems. Instead of providing broad “general” optimizations
like Lithium, Sepals focuses on deep rewrites of a few extremely expensive vanilla mechanisms — especially villager
brains, frog behavior, entity cramming, target filtering, and nearby-entity sensing.

### So what does it do in practice?

If you run large farms, villager halls, dense mob enclosures,
or any scenario with hundreds/thousands of entities in a small area,
Sepals drastically lowers tick time by avoiding Vanilla’s most expensive AI operations.

If you’re running a normal SMP with small farms, you probably won’t notice much of a difference.

It is experimental, and not every optimization is guaranteed to have Vanilla parity,
but in performance-stress tests, the gains can be huge.

## Compatibility

Currently, Sepals is compatible with nearly all mods.

Here is the verified mod list with the latest Sepals version:

|                                           Target mod | Required target version | 
|-----------------------------------------------------:|:-----------------------:|
|            [Sodium](https://modrinth.com/mod/sodium) |           all           |  
|                [Iris](https://modrinth.com/mod/iris) |           all           |   
| [FerriteCore](https://modrinth.com/mod/ferrite-core) |           all           |    
|          [Krypton](https://modrinth.com/mod/krypton) |           all           | 
|          [Lithium](https://modrinth.com/mod/lithium) |        >=0.21.2         |   
|         [C2ME](https://modrinth.com/mod/c2me-fabric) |      \>=0.3.6.0.0       |        

If you don't know which version to choose, please use the latest version of optimization mods, as Sepals will
ensure compatibility with them.

### Important

Sepals won't support any old versions and snapshot versions of Minecraft; all bug fixes and features are only available
in the newest version.

When using it with [Async](https://modrinth.com/mod/async),
please manually compare performance with Sepals and Async, and edit their configurations, to find which optimizations
are worthwhile,
as each machine has different results.

Sepals feature related: ```enableSepalsEntitiesCramming```.

## Config

|                 Config name                 | Allowed value | Default value |
|:-------------------------------------------:|:-------------:|:-------------:|
|            forceEnableSepalsPoi             |  bool value   |     false     |    
|            enableSepalsVillager             |  bool value   |     true      |    
|           enableSepalsFrogLookAt            |  bool value   |     true      |    
|      enableSepalsFrogAttackableSensor       |  bool value   |     true      |    
|        enableSepalsLivingTargetCache        |  bool value   |     true      |    
|   nearestLivingEntitiesSensorUseQuickSort   |  bool value   |     true      |    
|       enableSepalsBiasedLongJumpTask        |  bool value   |     true      |    
|        enableSepalsEntitiesCramming         |  bool value   |     true      |    
|            enableSepalsItemMerge            |  bool value   |     true      |
| enableSepalsQuickCanBePushByEntityPredicate |  bool value   |     true      |

## Performance

A test was done in server of Mars provided by feimia, CPU: Intel i7-14700K; Game memory: 4G; OS: Ubuntu 24.04.1 LTS;
Minecraft: 1.21.

### Entities cramming

```
Use box cache to prevent too much 'getOtherEntities' calls

-- Notice --
This feature ignores scoreboard/team predicates for every single entity
and may cause unexpected behavior.

This problem will not affect anything in Vanilla, except when using
command blocks.

-- Status --
Enabled by default
```

1390 villagers cramming in a 7x7 space:

|      Environment | tickCramming | Percent (Avg.) |
|-----------------:|:------------:|:--------------:|
|          Vanilla |   53.6 ms    |     100 %      |
|          Lithium |   54.4 ms    |     101 %      |
|           Sepals |   10.2 ms    |      19 %      |
| Sepals + Lithium |    8.5 ms    |      15 %      |

### Weighted random

```
Use binary search to replace vanilla weight random

-- Warning --
This feature may not be worthwhile,
as Spark proved that it is slower than Vanilla when constructing range tables,
even if the binary search is close to 0 ms.

-- Status --
Disabled by default

-- Warning -- 
Not tested.
```

### Biased long jump task

```
Use Sepals long jump task implementation to replace Vanilla

As mentioned above, the binary search is almost no costs
sepals impletation will construct the range table at the same time as generating targets
and used Catheter to replaced java stream

The same time, rearrange should jump conditions in frog brain, make a good performance

-- Notice --
This feature is unable to change in game runtime
required restart the server to apply changes

-- Status --
Enabled by default
```

800 frogs cramming in a 7x7 space:

|                 Environment                  | keepRunning | Percent(Avg.) |
|:--------------------------------------------:|:-----------:|:-------------:|
|        Vanilla <br /> (LongJumpTask)         |   43.1 ms   |     100 %     |
|        Lithium <br /> (LongJumpTask)         |   7.5 ms    |     17 %      |
|      Sepals <br /> (SepalsLongJumpTask)      |   0.2 ms    |     0.4 %     |
| Sepals + Lithium <br /> (SepalsLongJumpTask) |   0.05 ms   |     0.1 %     |

|                 Environment                  | getTarget | Percent(Avg.) | Percent(in ```keepRunning```) |
|:--------------------------------------------:|:---------:|:-------------:|:-----------------------------:|
|        Vanilla <br /> (LongJumpTask)         |  43.1 ms  |     100 %     |             100 %             |
|        Lithium <br /> (LongJumpTask)         |  3.6 ms   |      9 %      |             48 %              |
|      Sepals <br /> (SepalsLongJumpTask)      |  N/A ms   |      0 %      |              0 %              |
| Sepals + Lithium <br /> (SepalsLongJumpTask) |  N/A ms   |      0 %      |              0 %              |

### Quick sort in NearestLivingEntitiesSensor

```
Use quick sorting from FastUtil to replace Java TIM's sorting

-- Status --
Enabled by default
```

800 frogs cramming in a 7x7 space:

|      Environment | sort (NearestLivingEntitiesSensor#sense) | Percent(Avg.) |
|-----------------:|:----------------------------------------:|:-------------:|
|          Vanilla |                  3.8 ms                  |     100 %     |
|          Lithium |                  3.6 ms                  |     94 %      |
|           Sepals |                  2.2 ms                  |     57 %      |
| Sepals + Lithium |                  2.2 ms                  |     57 %      |

### Frog attackable target filter

```
Rearrange the attackable conditions
to have the least costly conditions first,
and therefore reduce the probability of high-costs calculating

-- The complaints --
Mojang's attackable predicate is:

!entity.getBrain().hasMemoryModule(MemoryModuleType.HAS_HUNTING_COOLDOWN)
 && Sensor.testAttackableTargetPredicate(entity, target)
 && FrogEntity.isValidFrogFood(target)
 && !this.isTargetUnreachable(entity, target)
 && target.isInRange(entity, 10.0)

in this case, 'Sensor#testAttackableTargetPredicate' calls 'TargetPredicate#test'
which causes lots of raycast calculating when there are too many entities in an area,
making it even worse, considering Minecraft's raycast is painfully slow

in this case, 'TargetPredicate#test' (800 frogs) costed 9.8ms per game tick,
and 'BlockView.raycast' contributed 7.3ms

therefore, I changed it to:

FrogEntity.isValidFrogFood(target) &&
 entity.getBrain().hasMemoryModule(MemoryModuleType.HAS_HUNTING_COOLDOWN) && 
 target.isInRange(entity, 10.0) && 
 Sensor.testAttackableTargetPredicate(entity, target) && 
 isTargetUnreachable(entity, target);
 
the 'isValidFrogFood' is a simple condition, it checks the entity's 'frog_food' tag
and an extra check when entity is slime then skip it when it size not 1

'isInRange' and 'hasMemoryModule' are also fine, as they only calculate some simple things

-- Status --
Enabled by default
```

800 frogs cramming in a 7x7 space:

|                                   Environment |  time  | Percent(Avg.) |
|----------------------------------------------:|:------:|:-------------:|
|       Vanilla (FrogAttackablesSensor#matches) | 10 ms  |     100 %     |
|       Lithium (FrogAttackablesSensor#matches) | 5.7 ms |     57 %      |
|           Sepals (SepalsFrogBrain#attackable) | 0.1 ms |      1 %      |
| Sepals + Lithium (SepalsFrogBrain#attackable) | 0.1 ms |      1 %      |

### Frog look-at target filter

```
Use 'SepalsLivingTargetCache' to improves target search performance
You must have that option enabled, otherwise this is identical to Vanilla

-- Notice --
The raycast is in TargetPredicate test
at the 'findFirst' in LivingTargetCache when input predicate is success

but if subsequent conditions are failures, it is useless to calculate this further
because even if the findFirst has found (raycast success)
but we don't used this result in subsequent contexts  

-- Status --
Enabled by default
```

800 frogs cramming in a 7x7 space:

|                                                                       Environment | findFirst | Percent |
|----------------------------------------------------------------------------------:|:---------:|:-------:|
|                      Vanilla <br /> (LookAtMobWithIntervalTask$$Lambda#findFirst) |  2.7 ms   |  100 %  |
|                      Lithium <br /> (LookAtMobWithIntervalTask$$Lambda#findFirst) |  2.5 ms   |  92 %   |
|           Sepals <br /> (SepalsLookAtMobWithIntervalTask$$Lambda#findFirstPlayer) |  0.1 ms   |   3 %   |
| Sepals + Lithium <br /> (SepalsLookAtMobWithIntervalTask$$Lambda#findFirstPlayer) |  0.1 ms   |   3 %   |

### Villager miscellaneous optimizations

```
This is what Sepals does:

1. Use Catheter to replaced Java's Stream, since it has much better performance and scalability compared to Stream
2. Cache tasks, activities, running tasks and memory to improve starting and updating task times
3. Use sepals composite task to replaced vanilla composite task
4. Whenever possible, find opportunities to skip more raycasts and useless predicates
5. Use 'SepalsLivingTargetCache' to replaced vanilla cache, At the cost in sensors tick make less cost in finding interaction target or look at mob task
6. Rearranged predicates and extra lower cost predicate, The purpose of this is do higher cost predicate later or best don't do that, skip the remaining high cost predicates in advance
7. Copied and modified 'SerializingRegionBasedStorage' optimizations from lithium
8. With more targeted task, don't use the generics to reduce useless operations
9. Use binary search list to replace hashset search

-- Notice --
It is recommended to use this with Lithium and C2ME for optimal performance

-- Warning --
Not long-term stability tested, only a month running shown it's ok currently
this feature has not been proved to be identical to Vanilla,
but also hasn't appeared to be statistically different

-- Status --
Enabled by default
```

800 villagers cramming in a 7x7 space at noon:

|   Environment    | Brain#tick (Total) | Percent | Brain#startTasks | Percent(startTasks) | Brain#tickSensors | Percent(tickSensors) | Brain#updateTasks | Percent(updateTasks) | Brain#tickMemories | Percent(tickMemories) |
|:----------------:|:------------------:|:-------:|:----------------:|:-------------------:|:-----------------:|:--------------------:|:-----------------:|:--------------------:|:------------------:|:---------------------:|
|     Vanilla      |       18 ms        |  100 %  |      9.3 ms      |        100 %        |      5.2 ms       |        100 %         |       3 ms        |        100 %         |       0.5 ms       |         100 %         |
|     Lithium      |      12.4 ms       |  68 %   |      4.8 ms      |        51 %         |      5.9 ms       |        113 %         |      1.2 ms       |         40 %         |       0.5 ms       |         100 %         |
|      Sepals      |       9.7 ms       |  53 %   |      3.6 ms      |        38 %         |      3.7 ms       |         71 %         |       2 ms        |         66 %         |       0.4 ms       |         80 %          |
| Sepals + Lithium |       10 ms        |  55 %   |      3.4 ms      |        36 %         |      3.7 ms       |         71 %         |      2.5 ms       |         83 %         |       0.4 ms       |         80 %          |

800 villagers cramming in a 7x7 space at night:

|   Environment    | Brain#tick (Total) | Percent | Brain#startTasks | Percent(startTasks) | Brain#tickSensors | Percent(tickSensors) | Brain#updateTasks | Percent(updateTasks) | Brain#tickMemories | Percent(tickMemories) |
|:----------------:|:------------------:|:-------:|:----------------:|:-------------------:|:-----------------:|:--------------------:|:-----------------:|:--------------------:|:------------------:|:---------------------:|
|     Vanilla      |      16.7 ms       |  100 %  |      8.2 ms      |        100 %        |       6 ms        |        100 %         |       2 ms        |        100 %         |       0.5 ms       |         100 %         |
|     Lithium      |      10.2 ms       |  61 %   |      3.2 ms      |        24 %         |       6 ms        |        113 %         |      0.5 ms       |         25 %         |       0.5 ms       |         100 %         |
|      Sepals      |        9 ms        |  53 %   |      3.3 ms      |        16 %         |      4.7 ms       |         78 %         |      0.7 ms       |         35 %         |       0.3 ms       |         60 %          |
| Sepals + lithium |       8.7 ms       |  52 %   |      2.9 ms      |        11 %         |      4.6 ms       |         76 %         |      0.7 ms       |         35 %         |       0.5 ms       |         100 %         |

### Predicate optimization

1172 frogs cramming in a 3x3 space:

|                                                                                                  Environment |   time   | Percent(Avg.) |
|-------------------------------------------------------------------------------------------------------------:|:--------:|:-------------:|
|                                                      Vanilla (java.util.function.Predicate.lambda\$and\$0()) | 49.01 ms |     100 %     |
| Sepals (com.github.cao.awa.sepals.entity.predicate.SepalsEntityPredicates$$Lambda/0x000002d8f116e000.test()) | 22.6 ms  |     46 %      |
