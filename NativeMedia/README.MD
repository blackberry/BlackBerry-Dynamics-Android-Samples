# Good Dynamics SDK Native-Media Sample  

This sample application is based on a version of the Native-Media sample app from [Google NDK samples](https://github.com/googlesamples/android-ndk/tree/master/native-media) 

This sample app requires an MPEG-2 Transport Stream file to be placed in /sdcard/NativeMedia.ts and encoded as:

  video: H.264 baseline profile
  audio: AAC LC stereo

For demonstration purposes a Google provided .ts file is provided. Any actual stream must be created according to the MPEG-2 specification.

The modifications to the generic sample application comprise -

- Integration of the Application Java code with the Good Dynamics lifecycle. Ensuring application can be activated and authorized
- Code which will copy provided video from SDcard into GD Secure Container to be played
- Integration of the Application C code with the Good Dynamics Secure Container to read data direct from the Secure Container 


## Applies to / Runtime

Android Studio 2.1.2
Android 6.0.1


## Author(s)

John White


## How to Setup & Build

1) Copy sample media file to SDCard 'adb push NativeMedia.ts /sdcard/NativeMedia.ts' 

2) Copy Good Dynamics SDK into ./GD_SDK folder (as per instructions found in folder)

3) Build native code by executing command 'ndk-build' from project root directory. 

Note: This requires Android NDK downloaded and location added to PATH (Tested with NDK r11b)

4) Set GD Application ID in assets/settings.json . This value is the GD entitlement ID which should be added into Good Control Server

5) Build java code by executing command './gradlew clean assemble' from the project root directory, 
or alternatively loading the build.gradle into Android Studio IDE to build/debug

6) Install APK and GD Activate the application 

7) Select "Start/Pause Native MediaPlayer" to start playing media file from GD Secure Container 

Note: Mediafile will be copied from SDcard to GD SecureContainer when app is authorized. In actual application this Media File would
typically have been received over secure connection

See below for more information about how to install the GD SDK

[Developers for Enterprise Apps](https://community.good.com/community/gdn/get-started)


## License

Apache License 2.0


## Disclaimer

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
