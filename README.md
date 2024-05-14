<p align="center">  
  <img src="https://github.com/Frighetto/MyWine/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="MyWine"><br/>  
</p>

## Technical Specifications for developers

java -version

openjdk version "1.8.0_412"

OpenJDK Runtime Environment (build 1.8.0_412-b08)

OpenJDK 64-Bit Server VM (build 25.412-b08, mixed mode)

On Android Studio Settings make sure that java 1.8 openjdk is selected:
Settings >>> (Build, Execution, Deployment) >>> Build Tools >>> Gradle
Select the Gradle Project "MyWine" and set Gradle JDK: java-1.8.0-openjdk, then synchronise project with gradle files.

Build the project to generate the .apk file which is the installer of the app, before build, set build variant to "debug", the build variant "release" is not working.
The .apk are generated in the folder "MyWine/app/build/outputs/apk/debug/".

The default name of the .apk is "app-debug.apk", it is recommended to change the name of "app-debug.apk" to "MyWine.apk".


## Installation and Execution

[The MyWine Installer](https://github.com/Frighetto/MyWine/raw/master/app/build/outputs/apk/debug/MyWine.apk) can be downloaded by the link: 

[https://github.com/Frighetto/MyWine/raw/master/app/build/outputs/apk/debug/MyWine.apk](https://github.com/Frighetto/MyWine/raw/master/app/build/outputs/apk/debug/MyWine.apk)

On installation, seek for the hidden options to allow the installation.

After installed, must allow camera and files, to allow, on Android, go to Settings >>> Applications, select MyWine then allow camera and files.These permission are needed to use barcode reader (camera), and save or load data (files).
> **Note:** The options to allow camera and files may not appear during the installation or during the use of the app.

## Configuration
The app does not store the pictures, only the filepatch to the pictures, by default the pictures are set to the directory "/Pictures/MyWine/", all the other data of the app may be saved in the same directory "/Pictures/MyWine/", that data may have the name "backup.txt". The save or load of backup is manual.

The leds are set by (column, line, led number), there is no user interface for set the leds, so you have to write by hand the text block that set the leds. In a future release there will be a user interface and support for multiple ports of leds and documentation about arduino.
