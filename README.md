# Converting callback async calls to RxJava
Companion piece to blog post

This app reports values from the accelerometer sensor reactively using RxJava, then outputs all the values to logcat using different `AsyncEmitter.BackpressureMode` strategies

## App Screenshot
![App Screenshot](/screenshot-2016-08-24_22.00.02.989.png?raw=true)

## Logcat output when MissingBackpressureException is thrown
![logcat output example](/Screen%20Shot%202016-08-29%20at%204.07.56%20PM.png?raw=true)
