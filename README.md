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
	#The seed value used to generate world-specific values. By default, it is set to your world seed on world load. If you change this, alchemy rules might change!
	#Range: -9223372036854775808 ~ 9223372036854775807
	seed = 0

 #Whether to reset the seed to your world seed when loading.
	resetSeed = true 
 (is true by default and set to false after the world seed is written to this file)
 (if you want deterministic behavior, set this to false in a defaultconfigs copy of the file and choose your own seed)

#Mod Integration Options:
[integration]
	#:Requires Pehkui: The scale that the Reduction reaction sets nearby creatures to. [Default: 0.65]
	#Range: 0.05 ~ 0.95
	pehkuiSmallSize = 0.65
	#:Requires Pehkui: The scale that the Enlargement reaction sets nearby creatures to. [Default: 1.33]
	#Range: 1.05 ~ 10.0
	pehkuiLargeSize = 1.33
(Defines the sizes that the Pehkui interaction reactions make things. Keep in mind that there is research to be done to undo these effects, so the player needs to be able to operate at either altered size.)

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

# Adding Powers
Custom Powers are added at startup using `StartupEvents.registry`, like so:
```js
StartupEvents.registry('reactive:powers', event => {
    event.create('custom_power')
        .color(0xFF00FF)
        .icon('kubejs:custom_power_bottle')
        .setMagicWater()
})
```

The resulting power will be `kubejs:custom_power`. In this scenario, the power is bright purple in color, uses the "Magic" water render, and is represented by a custom item in JEI.

There are a few other possible method calls:
- `.bottle(Item)` chooses an item to be this power's "Bottle". This allows the item to be made by clicking the Crucible with a Quartz Bottle if there's enough of this Power, and causes the item to release this Power and revert to a Quartz Bottle if put inside.
- `.setNormalWater()` causes the power to use the normal water texture in the Crucible.
- `.setNoiseWater()` causes the power to use a noisy water texture in the Crucible.
- `.setFastWater()` causes the power to use a more quickly moving water texture in the Crucible.
- `.setSlowWater()` causes the power to use a less quickly moving water texture in the Crucible.
- `.setCustomWater(Block)` causes the power to use any given block as its water texture. If the block is not animated, this will look bad!

Once a Power is made, it may be used as a valid entry for recipes (which can of course be made using KubeJS or a data pack). You can also add a language entry for it similarly to KubeJS custom items or blocks.

Sources for the Power will automatically be searched for in the item tag `reactive:(power_name)_sources`; `reactive:custom_power_sources` for the above. This follows the normal logic for the power source tags.

# Adding Special Cases
Some of the effects in the mod are represented as 'Special Cases' that occur when you do certain actions involving the Crucible. You can implement two kinds of these in KubeJS by handling different events in your server script file.

## EMPTYING EVENTS
When you empty the Crucible, sometimes an effect occurs depending on the Powers inside the Crucible. This also fires the `ReactiveEvents.emptyCrucible` event, so you can add new effects!
```js
ReactiveEvents.emptyCrucible(event => {
    if(event.hasPower("kubejs:custom_power")){
        if(event.getPowerLevel("kubejs:custom_power") > WorldSpecificValue.get("test_empty_threshold", 400, 600)){
            console.log("Do something dramatic!")
        }else{
            console.log("Do something subtle!")
        }
    }
})
```
The above handler checks to see if the Custom Power was present when the Crucible was emptied, and if so, tests the amount of that power against a world-specific threshold. 

Here's a summary of the methods available through the event handler:
- `hasPower(ResourceLocation)` checks if the given Power is in the Crucible at all (there is more than 0 of it).
- `getPowerLevel(ResourceLocation)` provides an integer from 0 to 1600, which is how much of the given Power is inside the Crucible.
- `getCruciblePos()` returns the `BlockPos` that the Crucible occupies
- `getCrucible()` returns the `CrucibleBlockEntity` itself. Check the source code if you want to use this.

As an aside, the `WorldSpecificValue` class generates a random value between two numbers that is the same every time it is called in the same world, and different in other worlds. Here, it randomizes the threshold beyond which the effect changes.
## DISSOLVE EVENT
When an item is dissolved in the Crucible, it fires a `ReactiveEvents.dissolveItem` event in KubeJS. You can handle it like this:
```js
ReactiveEvents.dissolveItem(event => {
    if(event.getItem().is("kubejs:simple_item")){
        console.log("We're dissolving a custom item. Do something!")
    }
})
```
The event object all the same methods as the previous one, and these two as well:
- `.getItem()` returns the item being dissolved. Since this event fires after ever dissolution, you need to test that this is the item you want.
- `.getItemEntity()` returns the item entity that is being dissolved. If you want to prevent it from being processed further, you could kill this entity.

# Adding Reactions
To add a reaction, you will need to set up a few different event handlers.

## REACTION CRITERIA
First, you'll need to, in startup, set up a criteria trigger for the reaction. All reactions cause advancement criteria to complete when running, so this is mandatory.
```js
StartupEvents.init(event => {
    ReactionMan.CRITERIA_BUILDER.add("example_reaction")
})
```

This automatically creates advancement criteria `reactive:reaction/(alias)_criterion` and `reactive:reaction/(alias)_perfect_criterion`. You can make advancements using these reactions and even add them to the journal if you'd like. I'd recommend checking the journal files if you want to try this.

## REACTION REGISTRATION
Once you've registered the alias of the reaction with the Criteria Builder, you're ready to add the Reaction itself. Reactions are made in the server scripts file, and use the `ReactiveEvents` event group. Take the following example:
```js
ReactiveEvents.constructReactions(event => {
    event.builder("example_reaction", "reactive:light", "kubejs:custom_power").setStimulus("ELECTRIC").build()
})
```

This defines a new reaction called `example_reaction`. This reaction requires Light and our Custom Power from before to occur, and additionally requires the 'ELECTRIC' stimulus. Setting a stimulus is not mandatory, but if you do you'll need to choose from these terms:
```
GOLD_SYMBOL
ELECTRIC
NO_ELECTRIC
SACRIFICE
END_CRYSTAL
NO_END_CRYSTAL
```

## REACTION EFFECTS
To make the reaction do anything, you'll need to handle another event. `ReactiveEvents.runReaction` is a KubeJS event that will fire every time that a custom reaction performs its server tick, which should happen a few times a second. Since the same event is fired for all custom reactions, you'll need to check for the alias before you implement it:
```js
ReactiveEvents.runReaction(event => {
    if(event.getAlias() == "example_reaction"){
        console.log("Do something!")
    }
})
```
To aid you in making a useful handler, the event also includes these methods:
- `getLevel()` returns the Level that the reaction is being performed in.
- `getBlockPos()` returns the BlockPos of the crucible performing the reaction.
- `getCrucible()` returns the CrucibleBlockEntity itself. If you want to use this, you should check the source code.
- `expendPower(int)` spends the given amount of energy from the Crucible. This allows you to make reactions that do not run forever.

## REACTION RENDERS
Reactions can also run on the client side -- in fact, they do this every frame. Custom reactions use this time to send out another KubeJS event, which you can handle to add visual effects to your reaction. These handlers must be in a client-side script.
```js 
ReactiveEvents.renderReaction(event => {
    if(event.getAlias() == "example_reaction"){
        ParticleScribe.drawParticleRing(event.getLevel(), "minecraft:electric_spark", event.getBlockPos(), 2, 3, 10)
    }
})
```

This event has all the same fields as the `runReaction` event does. Also, as you can see, you have access to my `ParticleScribe` class if you want to use it. Check the source code  in `reactive.client.particle`.