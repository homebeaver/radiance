## Timeline pulse source
By default, Radiance timelines are driven by a dedicated thread. This thread is synced with the display refresh rate.

Applications that require custom pulse behavior - higher, lower or variable frame rate - should use the `RadianceAnimationCortex.setPulseSource(PulseSource)` API. The `PulseSource` interface is:

```java
public interface PulseSource {
	public void waitUntilNextPulse();
}
```

The implementation of `waitUntilNextPulse()` is expected to be a blocking call that returns on the next target pulse.

### Sample usage

The following class installs a custom pulse source that fires timeline pulses every 100 milliseconds:

```java
public class CustomPulseSource {
   private float value;

   public void setValue(float newValue) {
      SimpleDateFormat sdf = new SimpleDateFormat("mm:SSS");
      System.out.println(sdf.format(new Date()) + " : " + this.value + " -> "
            + newValue);
      this.value = newValue;
   }

   public static void main(String[] args) {
       RadianceAnimationCortex.setPulseSource(() -> {
          try {
              Thread.sleep(100);
          } catch (InterruptedException ie) {
              ie.printStackTrace();
          }
      });
      CustomPulseSource helloWorld = new CustomPulseSource();

      Timeline.builder(helloWorld)
          .addPropertyToInterpolate("value", 0.0f, 1.0f)
          .play();

      try {
         Thread.sleep(3000);
      } catch (Exception exc) {
      }
   }
}
```

This example uses the blocking `Thread.sleep()` API. Sample output of running this class is:

```
40:362 : 0.0 -> 0.0
40:362 : 0.0 -> 0.0
40:456 : 0.0 -> 0.2
40:550 : 0.2 -> 0.746
40:659 : 0.746 -> 0.946
40:753 : 0.946 -> 1.0
40:753 : 1.0 -> 1.0
```

Discarding the first and last lines - that correspond to the start and the end of the timeline - the custom pulse source is indeed firing on average every 100 ms as expected. Applications that have a higher-precision timer implementation - perhaps using native APIs - can provide the matching `PulseSource` implementation to achieve a higher pulse rate.
