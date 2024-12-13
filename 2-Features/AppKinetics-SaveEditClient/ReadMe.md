# AppKinetics Save-Edit Client

How to use BlackBerry Dynamics AppKinetics to securely share files and content between applications on device.

## Description

This AppKinetrics samples shows how to search for, create, and implement an Application-Based Service, using BlackBerry Dynamics AppKinetics. 

The SaveEditClient and SaveEditService samples split out the consumer and provider into different apps to illustrate transferring the file between apps on the device.

## Requirements

See [Software Requirements](https://docs.blackberry.com/en/development-tools/blackberry-dynamics-sdk-android/current/blackberry-dynamics-sdk-android-devguide/gwj1489687014271) of the BlackBerry Dynamics SDK (Android) 

## How To Build and Deploy

1. Set up BlackBerry Dynamics Development Environment.
2. Clone the repo. 
3. Launch Android Studio and open the project.
4. Edit the *GDApplicationID* to your own or use the default '*com.blackberry.dynamics.sample.consumer*' in the *settings.json* file in the assets directory .
5. Assign the app entitlement to a user in your UEM server. This may also require adding the BlackBerry Dynamics App entitlement to UEM if you are using your own. See [Add an internal BlackBerry Dynamics app entitlement](https://docs.blackberry.com/en/endpoint-management/blackberry-uem/current/managing-apps/managing-blackberry-dynamics-apps).
6. Build and run on emulator or a device.

For more information on how to develop BlackBerry Dynamics apps, refer to [Get Started with BlackBerry Dynamics](https://developers.blackberry.com/us/en/resources/get-started/blackberry-dynamics-getting-started) 

## License

Apache 2.0 License

## Disclaimer

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.