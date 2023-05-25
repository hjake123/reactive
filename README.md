# reactive
 
Reactive is an alchemy-themed magic mod based on in-world experimentation. 
If you're here, you're either curious about the source code, or want to know the format for recipes.
Either way, this is a passion project but I tried to put in work to make it understandable at least. 

# Config Options
There are two config files; one in the config folder, and one in each world's serverconfigs. For reference, these are the options each contain:
### COMMON CONFIG
```
#Options:
[config]
	#The crucible performs a stage of its calculations once every X game ticks. Lower numbers are more responsive, but laggier. [Default: 5]
	#Range: 1 ~ 900
	crucibleTickDelay = 5
 (If the crucible is causing server tick lag, this is the first thing you can change.)
 
	#The crucible may check an area this many blocks in radius for some effects. Do not set this too high. [Default: 12]
	#Range: 2 ~ 64
	crucibleRange = 12
 (This is the range for symbol effects, reaction effects, and some other things, so it also affects performance.)
 
	#Certain effects might teleport entities if they are not in this blacklist. [Default: "minecraft:ender_dragon", "minecraft:wither"]
	doNotTeleport = ["minecraft:ender_dragon", "minecraft:wither"]
 (Add any LivingEntity entities that you need to never be teleportable.)
 
 	#Whether acid should dissolve entity blocks. This would delete the contents of said blocks. [Default: false]
	acidMeltBlockEntities = false
 (Self explanatory.)
 
 	#The maximum number of blocks that can be displaced at once by a certain effect. [Default: 128]
	#Range: 4 ~ 4096
	maxDisplaceCount = 128
(How many blocks can be displaced when a structure of adjacent Framed Motion Salt Block is electrified. Note that this many blocks will be operated on in one tick by the initial displacement, which might cause a lag spike if you set it too high.)
 
```
### SERVER CONFIG
```
#World Specific Value Options:
[config]
	#The seed value used to generate world-specific values. By default, it is set to your world seed on world load. If you change this, alchemy rules might change!
	#Range: -9223372036854775808 ~ 9223372036854775807
	seed = 0

 #Whether to reset the seed to your world seed when loading.
	resetSeed = true 
 (is true by default and set to false after the world seed is written to this file)
 (if you want deterministic behavior, set this to false in a defaultconfigs copy of the file and choose your own seed)

```
### TAGS
The ```reactive:acid_immune``` block tag decides which blocks are immune to being dissolved by blocks of acid.

# Adding Power Sources
Any item can be added as a power source by adding it to one of these following tags:
```
reactive:body_sources
reactive:caustic_sources
reactive:curse_sources
reactive:light_sources
reactive:mind_sources
reactive:soul_sources
reactive:verdant_sources
reactive:vital_sources
reactive:warp_sources
```
Note that blaze sources cannot be added.
To specify that an item is "high potency", i.e. that it should return substantially more power than normal, assign it to the tag ```reactive:high_potency```.
Please take care not to replace my tag definition unless you want to overwrite my assignments of items to powers.

# Adding Recipes
### Specifying Powers
Whenever you need to write a Power into one of the following recipes 
(in the 'reagent' sections), you may use any of the following values:
```
reactive:body
reactive:blaze
reactive:caustic
reactive:curse
reactive:light
reactive:mind
reactive:soul
reactive:verdant
reactive:vital
reactive:warp
reactive:esoteric_x
reactive:esoteric_y
reactive:esoteric_z
```

### Specifying Reactants
Reactants are the input items for these recipes. They can be specified either as an item or a tag, like so:
```
"reactant": {
    "item": "namespace:input_item"
  }
```
```
"reactant": {
    "tag": "namespace:tag"
 }
```
Note that you don't need to use the '#' character when specifying a tag.

### DISSOLVE (Power Release) RECIPES
```
{
  "type": "reactive:dissolve",
  "reactant": {
    "item": "namespace:input_item"
  },
  "product": {
    "item": "namespace:output_item"
  }
  "needs_electricity": false
}
```
These recipes do not require that an item is a power source; they simply happen when any quantity of the reactant is added to the Crucible.

"needs_electricity" causes the recipe to require that electrical charge is present in the Crucible for the recipe to work. If it is omitted, false will be assumed by default.

Note that if "needs_electricity" is true and an item is a power source, non-electrified Crucibles will **still break down the item**! It would simply not return any byproduct.
### TRANSMUTE RECIPES
```
{
  "type": "reactive:transmutation",
  "reactant": {
    "item": "namespace:input_item"
  },
  "product": {
    "item": "namespace:output_item"
  },
  "reagents": ["reactive:power1", "reactive:power2", ...],
  "min": 1300,
  "cost": 1000
  "needs_electricity: false
}
```
These recipes are like Dissolve recipes, but they have a Power requirement.

"min" refers to the minumum total power balance that must be present for the transmutation to occur.

"cost" refers to the amount of power used (split between the reagents) by each operation.

"needs_electricity", as above, makes the recipe require that the Crucible is electrified.

### PRECIPITATION RECIPES
Though the mod doesn't describe them in JEI, there are also Precipitation Recipes, which cause items to be generated without a catalyst item. Their format is:
```
{
  "type": "reactive:precipitation",
  "product": {
    "item": "namespace:output_item"
  },
  "reagents": ["reactive:power1", "reactive:power2", ...],
  "min": 750,
  "cost": 250,
  "reagent_count": 2
  "needs_electricity: false
}
```
"min" and "cost" are the same as for transmutation.

"reagent_count" is the number of reagents from the list that are chosen. Which ones are required can therefore be made world-specific. If this value is equal or higher than the number of reagents given, all will be required. Note that adding too many of these recipes would be bad for performance and might make some reactions hard or impossible to cause.

"needs_electricity" is as above.

All recipes support item stack definitions with "count" greater than 1.
# Further Customization
At this time, there is no more you can do to alter the behavior of the mod. I'll likely add an API or similar sooner or later, especially if it's asked for. You can get in touch with me through the issues section or my discord (HyperLynx#7548) to talk about this.
