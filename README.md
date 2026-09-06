# Instant Multi Fishing

Server-side Fabric mod for Minecraft 26.2.

## Requirements

- Minecraft 26.2
- Fabric Loader 0.19.5+
- Fabric API 0.159.0+26.2
- Java 25
- Install the mod on the dedicated server only.

## Instant Catch

`instant_multi_fishing:instant_catch` is a level-1 fishing rod enchantment.

With Instant Catch on a fishing rod:

- One cast launches five fishing hooks in a fan spread: -12°, -6°, 0°, +6°, +12°.
- Reeling immediately resolves every surviving managed hook, without waiting for a natural bite.
- Every hook calls Minecraft's normal `FishingHook.retrieve` flow, so fishing loot remains vanilla and Luck of the Sea is applied through the rod's normal fishing-luck value.
- Five hooks can therefore produce up to five catches from one cast/reel.
- The rod is damaged once per reel rather than five independent durability hits.

The enchantment is added to the normal non-treasure enchanting pool and supports fishing rods.

For testing while holding a fishing rod:

```mcfunction
/enchant @s instant_multi_fishing:instant_catch 1
```

Luck of the Sea, Mending and Unbreaking can remain on the same rod.
