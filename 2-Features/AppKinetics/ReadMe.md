# AppKinetics

This sample demonstrates secure inter-application communication between BlackBerry Dynamics applications, using AppKinetics.

## Description

The sample uses an Application-Based Service to exchange data with other BlackBerry Dynamics applications. The service for this is Transfer File, which has the com.good.gdservice.transfer-file identifier.

In this application:

-   Files in the assets folder whose names start with "Sample" are listed in the
	user interface and can be sent to another application.
-   Files whose names have the extension ".txt" can be viewed, if in the assets
	folder, or if received from another application.

When the application is registered as a provider of the Transfer File service. It can receive files from consumers of that service. This application is also a consumer of the same service, and can send files to other app[lications that provide it. Custom in-house BlackBerry Dynamics applications can be registered as service providers in the BlackBerry UEM console. See [Manage BlackBerry Dynamics app services](https://docs.blackberry.com/en/endpoint-management/blackberry-uem/current/managing-apps/managing-blackberry-dynamics-apps/bd-app-services) in BlackBerry UEM documentation.

## Requirements

See [Software Requirements](https://docs.blackberry.com/en/development-tools/blackberry-dynamics-sdk-android/current/blackberry-dynamics-sdk-android-devguide/gwj1489687014271) of the BlackBerry Dynamics SDK (Android) 

## How To Build and Deploy

1. Set up BlackBerry Dynamics Development Environment.
2. Clone the repo. 
3. Launch Android Studio and open the project.
4. Edit the *GDApplicationID* to your own or use the default '*com.blackberry.dynamics.sample*' in the *settings.json* file in the assets directory .
5. Assign the app entitlement to a user in your UEM server. This may also require adding the BlackBerry Dynamics App entitlement to UEM if you are using your own. See [Add an internal BlackBerry Dynamics app entitlement](https://docs.blackberry.com/en/endpoint-management/blackberry-uem/current/managing-apps/managing-blackberry-dynamics-apps).
6. Build and run on emulator or a device.

For more information on how to develop BlackBerry Dynamics apps, refer to [Get Started with BlackBerry Dynamics](https://developers.blackberry.com/us/en/resources/get-started/blackberry-dynamics-getting-started) 

## License

Apache 2.0 License

## Disclaimer

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.