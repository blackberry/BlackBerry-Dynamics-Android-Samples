# BlackBerry Dynamics SDK Native-Media Sample

This sample application is based on a version of the Native-Media sample app from [Google NDK samples](https://github.com/googlesamples/android-ndk/tree/master/native-media) 

## Description

This sample app requires an MPEG-2 Transport Stream file to be placed in /sdcard/NativeMedia.ts and encoded as:

  video: H.264 baseline profile
  audio: AAC LC stereo

For demonstration purposes a Google provided .ts file is provided. Any actual stream must be created according to the MPEG-2 specification.

The modifications to the generic sample application comprise -

- Integration of the Application Java code with the BlackBerry Dynamics lifecycle. Ensuring application can be activated and authorized
- Code which will copy provided video from SDcard into BD Secure Container to be played
- Integration of the Application C code with the BlackBerry Dynamics Secure Container to read data direct from the Secure Container 

## Requirements

See [Software Requirements](https://docs.blackberry.com/en/development-tools/blackberry-dynamics-sdk-android/current/blackberry-dynamics-sdk-android-devguide/gwj1489687014271) of the BlackBerry Dynamics SDK (Android) 

## How To Build and Deploy

1. Set up BlackBerry Dynamics Development Environment.
2. Clone the repo. 
3. Copy sample media file to SDCard 'adb push NativeMedia.ts /sdcard/NativeMedia.ts' 
4. Build native code by executing command 'ndk-build' from project root directory. 
Note: This requires Android NDK downloaded and location added to PATH (Tested with NDK r11b)
5. Edit the *GDApplicationID* to your own or use the default '*com.blackberry.dynamics.sample*' in the *settings.json* file in the assets directory .
6. Assign the app entitlement to a user in your UEM server. This may also require adding the BlackBerry Dynamics App entitlement to UEM if you are using your own. See [Add an internal BlackBerry Dynamics app entitlement](https://docs.blackberry.com/en/endpoint-management/blackberry-uem/current/managing-apps/managing-blackberry-dynamics-apps).
7. Build java code by executing command './gradlew clean assemble' from the project root directory, 
or alternatively loading the build.gradle into Android Studio IDE to build/debug.
8. Install APK and Dynamics activate the application .
9. Select "Start/Pause Native MediaPlayer" to start playing media file from GD Secure Container 

Note: Mediafile will be copied from SDcard to GD SecureContainer when app is authorized. In actual application this Media File would
typically have been received over secure connection

For more information on how to develop BlackBerry Dynamics apps, refer to [Get Started with BlackBerry Dynamics](https://developers.blackberry.com/us/en/resources/get-started/blackberry-dynamics-getting-started) 

## License

Apache 2.0 License

## Disclaimer

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.






