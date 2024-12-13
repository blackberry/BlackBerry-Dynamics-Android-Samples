# Bypass Unlock

This sample shows how part of the application user interface can remain accessible after the BlackBerry Dynamics idle time out has expired. This is done
by using the Bypass Unlock feature.

## Description

Bypass Unlock can be allowed or disallowed by enterprise policy. This is implemented using the BlackBerry Dynamics Application Policies feature. To switch on Bypass Unlock, update the policy setting in the management console to allow parts of the user interface to be displayed when the idle lock is in place.

The part of the user interface that bypasses the unlock screen can be opened by sending an Android Debug Bridge (adb) command to open a particular Activity:

	$ adb shell am broadcast -n com.good.example.sdk.bypassunlock/.EventReceiver

The BlackBerry Dynamics unlock screen can be bypassed only when the user is already authorized, and after the idle time out has expired. If the user opens a Bypass Activity from cold start, i.e. before authorizing, they will have to enter their password.

## Requirements

See [Software Requirements](https://docs.blackberry.com/en/development-tools/blackberry-dynamics-sdk-android/current/blackberry-dynamics-sdk-android-devguide/gwj1489687014271) of the BlackBerry Dynamics SDK (Android) 

## How To Build and Deploy

1. Set up BlackBerry Dynamics Development Environment.
2. Clone the repo. 
3. Launch Android Studio and open the project.
4. Only use the *GDApplicationID* '*com.good.example.sdk.bypassunlock*' in the *settings.json* file in the assets directory. This ID has specific entitlements to use this feature.
5. Assign the app entitlement to a user in your UEM server. This may also require adding the BlackBerry Dynamics App entitlement to UEM if you are using your own. See [Add an internal BlackBerry Dynamics app entitlement](https://docs.blackberry.com/en/endpoint-management/blackberry-uem/current/managing-apps/managing-blackberry-dynamics-apps).
6. Build and run on emulator or a device.

For more information on how to develop BlackBerry Dynamics apps, refer to [Get Started with BlackBerry Dynamics](https://developers.blackberry.com/us/en/resources/get-started/blackberry-dynamics-getting-started) 

## License

Apache 2.0 License

## Disclaimer

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.