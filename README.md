# aprs-android-messaging

## Build

You must have the Android SDK and [NDK](http://developer.android.com/sdk/ndk/index.html) set up.

```bash
./gradlew assembleDebug
```

## Message Format

|        1111111      | 1231234E  |  121234N | Message |
| ------------------- | --------- | -------- | ------- |
| Truncated unix time | Longitude | Latitude | Message |

### Truncated unix time

* Characters 0 - 6
* The two most significant and the one least significant value is truncated. This provides 10 second time resolution and ~38 months before it overlaps.
* Example:

  Unix time: 1504011673
  Truncated unix time: 0401167

### Longitude

* Characters 7 - 14
* 4 decimal precision. This provides ~11 meters resolution.
* Degrees are always 3 digits with leading 0s when less than 100 or less than 10.
* Decimal is dropped from the value.
* E appended for East, W appended for West.

### Latitude

* Characters 15 - 21
* 4 decimal precision. This provides ~11 meters resolution.
* Degrees are always 2 digits with a leading 0 when less than 10.
* Decimal is dropped from the value.
* N appended for North, S appended for South.

### Message

* Characters 22 - 256
* Message in ASCII characters

## Acknowledgements

Based on code from [PacketDroid](https://github.com/ge0rg/PacketDroid) and [APRSdroid](https://github.com/ge0rg/aprsdroid).
