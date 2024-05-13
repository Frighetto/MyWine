### Technical Specifications for developers

java -version
openjdk version "1.8.0_412"
OpenJDK Runtime Environment (build 1.8.0_412-b08)
OpenJDK 64-Bit Server VM (build 25.412-b08, mixed mode)

On Android Studio Settings make sure that java 1.8 openjdk is selected:
  -(Build, Execution, Deployment) >>> Build Tools >>> Gradle
    Select the Gradle Project "MyWine" and set Gradle JDK: java-1.8.0-openjdk, then syncronise project with gradle files

Build the project to generate the .apk file, before build, set build variant to release, that file is the installer of the app.
The .apk are generated in the folder MyWine/app/build/outputs/apk/release/
The default name of the .apk is app-release-unsigned.apk
It is recommended to change the name of app-release-unsigned.apk to MyWine.apk


### Installation and Execution

[The MyWine Installer](https://github.com/Frighetto/MyWine/raw/master/app/build/outputs/apk/release/MyWine.apk) can be downloaded by the link: [https://github.com/Frighetto/MyWine/raw/master/app/build/outputs/apk/release/MyWine.apk](https://github.com/Frighetto/MyWine/raw/master/app/build/outputs/apk/release/MyWine.apk)
On installation, seek for the hidden options to allow the installation.
After installed, must allow camera and files, to allow, on Android, go to Settings >>> Applications, select MyWine then allow camera and files.
These permission are needed to use barcode reader (camera), and save or load data (files).
The options to allow camera and files may not appear during the installation or during the use of the app.
