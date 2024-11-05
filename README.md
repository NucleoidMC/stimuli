# Stimuli
Stimuli is a library that allows working with server-side events while being able to filter events by their source.
This makes Stimuli useful for general additional server-side event use, but also a very powerful tool in setting up
behavior local to a specific part of the world.

## Using

### Adding to Gradle
To add Stimuli to your Gradle project, add the Nucleoid Maven repository and Stimuli dependency.
`STIMULI_VERSION` should be replaced with the latest version from [Maven](https://maven.nucleoid.xyz/xyz/nucleoid/stimuli).
```gradle
repositories {
  maven { url = 'https://maven.nucleoid.xyz/' }
  maven { url = "https://jitpack.io" }
}

dependencies {
  // ...
  modImplementation 'xyz.nucleoid:stimuli:STIMULI_VERSION'
}
```

### Registering events globally
The simplest usage of the mod involves registering global events without a filter. Global event listeners can be
registered through `Stimuli.global()`. 

```java
Stimuli.global().listen(PlayerChatEvent.EVENT, (sender, message, messageType) -> {
    sender.sendMessage(Text.literal("You shall not speak!"), false);
    return EventResult.DENY;
});
```

This example registers a listener to the `PlayerChatEvent` event and returns `EventResult.DENY` in order to cancel
further processing of the chat event.

If an event you need is not currently implemented, please consider [submitting a Pull Request](https://github.com/NucleoidMC/stimuli/compare)!

### Registering events with a filtered source
Capturing events with a filtered source is a little more complicated. The basis of receiving events with a filter is an
`EventListenerSelector`: a listener selector returns an iterator of event listeners given an event source and event type.

The simplest case is returning events based on a single filter, and Stimuli provides an implementation for this case.

For example:
```java
// create a filter that only accepts positions within the overworld between (0; 0; 0) and (16; 256; 16)
EventFilter filter = EventFilter.box(World.OVERWORLD, new BlockPos(0, 0, 0), new BlockPos(16, 256, 16));

// create a map of event type to listener & deny block breaking
EventListenerMap listeners = new EventListenerMap();
listeners.listen(BlockBreakEvent.EVENT, (player, world, pos) -> EventResult.DENY);

// register our event listener selector so that our events get called
Stimuli.registerSelector(new SimpleListenerSelector(filter, listeners));
```
