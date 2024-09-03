# reactive
 
Reactive is an alchemy-themed magic mod based on in-world experimentation. 
If you're here, you're either curious about the source code, or want to know the format for recipes.
Either way, this is a passion project but I tried to put in work to make it understandable at least. 

See the [Wiki](https://github.com/hjake123/reactive/wiki) for more information.

# Config Options
There are three config files; two in the config folder, and one in each world's serverconfigs. For reference, these are the options each contain:
### COMMON CONFIG
```
#Options:
[config]
	#The crucible performs a stage of its calculations once every X game ticks. Lower numbers are more responsive, but laggier. [Default: 5]
	#Range: 1 ~ 900
	crucibleTickDelay = 5
 (If the crucible is causing server tick lag, this is the first thing you can change.)
 
	#The crucible affect entities with an area of this radius. [Default: 12]
	#Range: 2 ~ 64
	crucibleRange = 12
 (This is the range for reaction effects, and some other things.)

	#The crucible checks an area this many blocks in radius up to a few times a second. Do not set this too high. [Default: 6]
	#Range: 2 ~ 64
	areaMemoryRange = 6
 (This is the range for the area memory system. The time of each area memory update increases in an n-cubed fashion with this value.)

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

	#The maximum distance that a block like Copper can convey a displacement pulse [Default: 8]
	#Range: 1 ~ 4096
	copperDisplaceConductRange = 8
(If a displacement-conductive block is placed next to a Framed Motion Salt Block, the displacement effect scans up to this many blocks in a direction to find another Framed Motion Salt Block to chain with.)

	#Whether the Radiant Staff of Power produces permanent light sources. When false, its lights will gradually vanish. [Default: true]
	lightStaffLightsPermanent = true
(If this is turned off, invisible lights left by the Radiant Staff of Power will decay on random ticks.)

	#Blocks with a base break time beyond this cannot be displaced or made to fall. For finer control, use the relevant block tags. [Default: 35.0]
	#Range: 0.0 ~ 10000.0
	maxMoveBlockBreakTime = 35.0
(This controls how tough blocks can be before they are not able to be displaced or made to fall by Motion Salts. For reference, an Iron Block passes the default threshold, but Deepslate does not. Higher values are more permissive. Blocks with negative hardness (e.g. Bedrock) are always unmovable by this system, and an additional deny list exists in the block tags.)
```
### SERVER CONFIG
```
#World Specific Value Options:
[wsv]
	#The seed value used to generate world-specific values. By default, it is set to your world seed on world load. If you change this, some alchemy rules will change!
	#Range: -9223372036854775808 ~ 9223372036854775807
	seed = 42
	#Whether to use the above customized alchemy seed instead of the automatically generated one.
	useCustomSeed = false
(You can set a customized seed for your instance/modpack if you prefer the rules not to change in each new world.)

#Balance Options:
[balance]
	#The scale that the Reduction reaction sets nearby creatures to. [Default: 0.6]
	#Range: 0.05 ~ 0.95
	shrinkSmallSize = 0.6
	#The scale that the Enlargement reaction sets nearby creatures to. [Default: 1.5]
	#Range: 1.05 ~ 10.0
	growLargeSize = 1.5
	#The step height that the Reduction reaction sets nearby creatures to. Normal is 0.6. [Default: 0.45]
	#Range: 0.01 ~ 0.6
	shrinkSmallStep = 0.45
	#The step height that the Enlargement reaction sets nearby creatures to. Normal is 0.6. [Default: 1.0]
	#Range: 0.6 ~ 5.0
	growLargeStep = 1.05
(As each entry says, this controls the balancing of a few things in the mod.)

```
### CLIENT CONFIG
```
#Client Side Options:
[config]
	#Whether to show the sources of each Power in JEI. Use this if your pack adds a lot of unintuitive Power sources, or you become frustrated.
	showPowerSources = false
(This shows the players every item that is a power source. It doesn't match my original design philosophy, but if your pack adds unintuitive Power sources,
	this will make things a lot easier. Also, players could circumvent the secrecy using JEI tag searches anyway so it was time for this feature to exist.)
	
	#Whether to render all Powers using vanilla Water's icon. Use if Rubidium or other rendering mods make the custom water textures break.
	doNotChangeWaterTexture = false
(This is a Rubidium compat feature, since with Rubidium installed Crucible water will not render correctly.)

	#Whether to allow Litmus Paper to use multicolored text. Disable if the colored text is hard to read.
	colorizeLitmusOutput = true
(As of version 8, Litmus Paper will attempt to color the lines of its measurements to match the powers they're discussing. This setting disables that system, in case it causes eyestrain or readability issues.)

    #Whether to hide the icons of Powers in JEI. This is on by default because otherwise the menu looks redundant due to how Power icons work.
    hidePowersFromJEI = true
(Powers are JEI ingredients, but they share the images of items from the mod, so I recommend not having them visible unless you're debugging custom Powers.)
```
### TAGS
The ```reactive:acid_immune``` block tag decides which blocks are immune to being dissolved by blocks of acid.

The ```reactive:can_be_generated``` block tag decides which blocks are created spontaneously by a certain reaction. The reaction is hard to start, but can be made maintained with renewable resources.

The ```reactive:do_not_displace``` block tag is a deny list for blocks that don't behave when displaced. This includes multi-part blocks like doors and piston heads, since displacement can cut them in half. Blocks above the configured hardness are already immune.

The ```reactive:do_not_make_fall``` block tag is a deny list for blocks that do not fall when adjacent to a Motion Salt Block. Blocks above the configured hardness are already immune.

The ```reactive:displacement_conductive``` block tag defines which blocks are allowed to act as a bridge between Framed Motion Salt Blocks during displacement. These blocks are immune to displacement as a side effect.

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

Keep in mind as well that by default players will not be able to look up which items source which powers. That's part of the fun of experimenting in the mod! However, there is a config option in the client config to show this information to the player though a JEI interaction. Decide if you want to use that option carefully.

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
reactive:astral
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
```json
{
  "type": "reactive:dissolve",
  "reactant": {
    "item": "namespace:input_item"
  },
  "product": {
    "item": "namespace:output_item"
  },
  "needs_electricity": false
}
```
These recipes do not require that an item is a power source; they simply happen when any quantity of the reactant is added to the Crucible.

"needs_electricity" causes the recipe to require that electrical charge is present in the Crucible for the recipe to work. If it is omitted, false will be assumed by default.

Note that if "needs_electricity" is true and an item is a power source, non-electrified Crucibles will **still break down the item**! It would simply not return any byproduct.
### TRANSMUTE RECIPES
```json
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
  "cost": 1000,
  "needs_electricity": false
}
```
These recipes are like Dissolve recipes, but they have a Power requirement.

"min" refers to the minumum total power balance that must be present for the transmutation to occur.

"cost" refers to the amount of power used (split between the reagents) by each operation.

"needs_electricity", as above, makes the recipe require that the Crucible is electrified.

### PRECIPITATION RECIPES
Though the mod doesn't describe them in JEI, there are also Precipitation Recipes, which cause items to be generated without a catalyst item. Their format is:
```json
{
  "type": "reactive:precipitation",
  "product": {
    "item": "namespace:output_item"
  },
  "reagents": ["reactive:power1", "reactive:power2", ...],
  "min": 750,
  "cost": 250,
  "reagent_count": 2,
  "needs_electricity": false
}
```
"min" and "cost" are the same as for transmutation.

"reagent_count" is the number of reagents from the list that are chosen. Which ones are required can therefore be made world-specific. If this value is equal or higher than the number of reagents given, all will be required. Note that adding too many of these recipes would be bad for performance and might make some reactions hard or impossible to cause.

"needs_electricity" is as above.

All recipes support item stack definitions with "count" greater than 1.
# KubeJS Integration
This mod has integration with KubeJS, allowing you to add new kinds of Power and new Reactions through KubeJS scripting.

For a full rundown of these features, please see KUBEJS.md.

I think that's everything! If you have questions or suggestions you can contact me at `hyperlynx` on Discord, or use the Issues section here.