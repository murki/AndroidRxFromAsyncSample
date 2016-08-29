# Converting callback async calls to RxJava
Companion piece to blog post

This app reports values from the accelerometer sensor reactively using RxJava, then outputs all the values to logcat using different `AsyncEmitter.BackpressureMode` strategies

## App Screenshot
![App Screenshot](/screenshot-2016-08-24_22.00.02.989.png?raw=true)

## Logcat output when MissingBackpressureException is thrown
![logcat output example](/Screen Shot 2016-08-29 at 4.07.56 PM.png?raw=true)
