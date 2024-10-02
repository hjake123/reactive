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

The resulting power will be located at `kubejs:custom_power`. In this scenario, the power is bright purple in color, uses the "Magic" water render, and is represented by a custom item in JEI.

There are a few other possible method calls:
- `.bottle(Item)` chooses an item to be this power's "Bottle". This allows the item to be made by clicking the Crucible with a Quartz Bottle if there's enough of this Power, and causes the item to release this Power and revert to a Quartz Bottle if put inside.
- `.setNormalWater()` causes the power to use the normal water texture in the Crucible.
- `.setNoiseWater()` causes the power to use a noisy water texture in the Crucible.
- `.setFastWater()` causes the power to use a more quickly moving water texture in the Crucible.
- `.setSlowWater()` causes the power to use a less quickly moving water texture in the Crucible.
- `.setCustomWater(Block)` causes the power to use any given block as its water texture. If the block is not animated, this will look bad!

Once a Power is made, it may be used as a valid entry for recipes (which can of course be made using KubeJS or a data pack). You can also add a language entry for it similarly to KubeJS custom items or blocks.

Sources for the Power will automatically be searched for in the item tag `reactive:(power_name)_sources`; `reactive:custom_power_sources` for the above. Remember to put especially strong items into `reactive:high_potency` and consider adding Dissolve recipes if they should leave some byproduct.

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
- `hasPower(ResourceLocation)` checks if the given Power is in the Crucible at all (there is more than 0 of it)
- `getPowerLevel(ResourceLocation)` provides an integer from 0 to 1600, which is how much of the given Power is inside the Crucible
- `getLevel()` returns the Level that the reaction is being performed in
- `getBlockPos()` returns the BlockPos of the crucible performing the reaction
- `getCrucible()` returns the CrucibleBlockEntity itself. If you want to use this, you should check the source code

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
To add a reaction, you will need to set up a few different event handlers and an advancement file.

## REACTION CRITERION TRIGGER
First, you'll need to, in startup, set up a criteria trigger for the reaction. All reactions cause advancement criteria to complete when running, so this is mandatory.
```js
StartupEvents.init(event => {
    ReactionMan.CRITERIA_BUILDER.add("example_reaction")
})
```

This automatically creates advancement criteria `reactive:reaction/(alias)_criterion` and `reactive:reaction/(alias)_perfect_criterion`. 

## REACTION REGISTRATION
Once you've registered the alias of the reaction with the Criteria Builder, you're ready to add the Reaction itself. Reactions are made in the server scripts file, and use the `ReactiveEvents` event group. Take the following example:
```js
ReactiveEvents.constructReactions(event => {
    event.builder("example_reaction", Component.literal("Example Reaction"),"reactive:light", "kubejs:custom_power").needsGoldSymbol().setCost(2).build()
})
```

This defines a new reaction called `example_reaction`. The in-game name of the reaction will be "Example Reaction". This reaction requires Light and our Custom Power from before to occur, and additionally requires the presence of a Gold Symbol as its stimulus. Every server-side reaction tick, it consumes 2 units of power.

This reaction is relatively simple, but the builder has a few more methods you can use:
- `.needsGoldSymbol()` makes the reaction require a Gold Symbol as its stimulus
- `.needsElectric()` makes the reaction require Electric Charge (for example from a Volt Cell) as its stimulus
- `.needsNoElectric()` makes the reaction require a lack of Electric Charge as its stimulus
- `.needsEndCrystal()` makes the reaction require a nearby End Crystal as its stimulus
- `.needsNoEndCrystal()` makes the reaction require there not to be a nearby End Crystal as its stimulus
- `.setCost(int)` adds a per-reaction-tick cost to the reaction. One tick occurs every half second or so (config dependant).
- `.setYield(ResourceLocation, int)` adds a yield to the reaction; each tick, the specified amount of the specified Power is added. Use this to make Synthesis or Conversion reactions.

## REACTION EFFECTS
To make the reaction do anything, you'll need to handle another event. `ReactiveEvents.runReaction` is a KubeJS event that will fire every time that a custom reaction performs its server tick, which should happen a few times a second. Since the same event is fired for all custom reactions, you'll need to check for the alias before you implement it:
```js
ReactiveEvents.runReaction(event => {
    if(event.getAlias() == "example_reaction"){
        console.log("Do something!")
    }
})
```
The event includes these methods:
- `getAlias()` returns the reaction's unique alias as a string
- `getLevel()` returns the Level that the reaction is being performed in.
- `getBlockPos()` returns the BlockPos of the crucible performing the reaction.
- `getCrucible()` returns the CrucibleBlockEntity itself. If you want to use this, you should check the source code
- `expendPower(int)` spends the given amount of energy from the Crucible. This allows you to make reactions that do not run forever
- `hasPower(ResourceLocation)` checks if the given Power is in the Crucible at all (there is more than 0 of it)
- `getPowerLevel(ResourceLocation)` provides an integer from 0 to 1600, which is how much of the given Power is inside the Crucible

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

## REACTION CHECKS
You can also add a custom check to determine if a reaction should occur. Due to limitations, this check must be listened for and handled on **both the client and server**. If you only listen on one side, the reaction will not render when it should, or will render without taking action.

Here's an example of a custom check:
```js
ReactiveEvents.checkReaction(event => {
    if(event.getAlias() == "test_reaction"){
        if(event.getLevel().isRaining()){
            event.cancel()
        }
    }
})
```

This check uses the getLevel() method to determine if it is raining. If so, the event is cancelled, which prevents the reaction from running or rendering.
Note that this check occurs after the system checks for the reaction's power balance and stimulus, so if you do not cancel the event, the reaction is guaranteed to occur.

This event has all the same fields as the `runReaction` event does.

## REACTION ADVANCEMENTS
As it stands, your reaction will appear as "Unknown Reaction" when measured by Litmus Paper. That is because Litmus Paper checks if the player has achieved a specific advancement for each reaction before presenting its name. Since yours doesn't yet have an associated advancement, it will never appear. Let's fix that!

The advancement should be located within `data/reactive/advancement/reactions`, and its name must match the reaction alias. For example, the prior reaction will look for the advancement `reactive:reactions/example_reaction`.

The advancement can be in any valid format, but something like this is sufficient:
```json
{
  "criteria": {
    "criterion": {
      "trigger": "reactive:reaction/example_reaction_criterion"
    }
  },
  "requirements": [
    [
      "criterion"
    ]
  ]
}
```
Here, the alias is again `example_reaction`. Make sure that the proper auto-generated reaction criterion is being used, or the advancement will not unlock when you observe the reaction.

### Reaction Perfection Advancement
If you want to add a Journal of Alchemy entry for your reaction, we'll need another advancement, this one for "perfectly" performing the reaction -- that is, performing the reaction without any extra Powers. This prevents players from learning the formula of reactions they didn't really discover. It follows a format like this:
```json
{
  "criteria": {
    "criterion": {
      "trigger": "reactive:reaction/example_reaction_perfect_criterion"
    }
  },
  "requirements": [
    [
      "criterion"
    ]
  ],
  "sends_telemetry_event": true
}
```
The only difference from the normal advancement is the criterion checked. This advancement must have a name of the form `(reaction alias)+perfect`, and needs to be located in the same directory as the normal reaction advancement.

## REACTION PATCHOULI PAGE
Please refer to the [Patchouli docs](https://vazkiimods.github.io/Patchouli/docs/reference/book-json#extension-keys) about creating an addon book and adding a page to it. All built-in reactions use a page similar to this one:
```json
{
  "name": "Luminous Ring",
  "icon": "minecraft:paper",
  "category": "reactive:reactions",
  "advancement": "reactive:reactions/sunlight",
  "pages": [
    {
      "type": "patchouli:text",
      "text": "$(bold)Visual:$(br)$()A ring of light appears with a 12 block radius around the Crucible.$(p)$(bold)Effect:$(br)$()Undead within the ring catch on fire as if burning in daylight."
    },
    {
      "type": "reactive:reaction",
      "reaction": "sunlight"
    }
  ]
}
```
The important bits here are the `advancement` field, which prevents this entry from unlocking if the player hasn't ever seen the reaction, and the `reactive:reaction` page template, which shows the formula if the player has the "perfect" advancement for the reaction.
