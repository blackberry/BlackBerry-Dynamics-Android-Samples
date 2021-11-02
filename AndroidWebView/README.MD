# BlackBerry Dynamics WebView client sample
**Note:** This sample has been transformed into the [BBWebView](https://developer.blackberry.com/devzone/files/blackberry-dynamics/android/classcom_1_1blackberry_1_1bbwebview_1_1_b_b_web_view.html) class available in the BlackBerry Dynamics SDK. It is recommended to use BBWebView over this sample.

This application is a simple web content browser supporting access to private/corporate network resources through BlackBerry Dynamics SDK.

App's functionality includes:                                                      
 * Secure HTTP request interception (URL loading, xml and fetch http requests)
 * Cookies saved in BlackBerry Dynamics secure storage
 * Page resource and content loading
 * Data Leakage Protection For Cut, Copy & Paste
 * URL input bar
 * Page history navigation
 * HTTP cookies listing
 * JavaScript evaluate console
 * Web Settings screen

<img src="./gitlab.gif" width="270" height="540" />



The following features are **not** implemented in this sample.

- HTTP authentication
- Kerberos authentication
- File downloading / uploading
- URL schemes ( ‘mailto:’, ‘geo:’ etc. )
- Web worker (Service worker)
- Web permissions (i.e. to enable GPS)
- Safe browsing (Google APIs)
- Paste from a BlackBerry Dynamics app to WebView using drag-n-drop

## Requirements
This sample was tested using the following:

 * Android Studio 2020.3.1
 * BlackBerry Dynamics SDK 10.0


## Runtime
 * Android 9.0 or later
 * Android system WebView update 80+


## Author(s)

* Mykhailo Mykytyn
* Serhii Butryk


## How To Build and Deploy

1. Set up BlackBerry Dynamics environment
2. Clone the repo to your computer.
3. Launch Android Studio and import the project's root folder
4. Edit app's package name to your own (the default is com.good.gd.example.apachehttp)
6. Edit GDApplicationID to your own in the './application/src/main/assets/settings.json'(the default is com.good.gd.example.apachehttp)
7. Build, deploy and run on a testing device using IDE or CLI
   1. Android Studio
      1. Run the "Application" configuration
   1. Command Line
      1. execute './gradlew clean build installRelease'


## Additional app dependencies
* ['org.brotli:dec:0.1.2'](https://mvnrepository.com/artifact/org.brotli/dec/0.1.2)

## Project structure
 * `android.webkit` custom components:
    * `BBWebViewClient` - custom WebView client, it's an entry point class for content loading
    * `BBDServiceWorkerClient` - custom ServiceWorker implementation intercepting service worker requests
    * `BBResponseInputStream` - custom InputStream, provides the response data for a WebView to consume
    * `BBWebResourceResponse` - custom WebResourceResponse class. Manages response codes, headers and data streams.
    * `RequestInterceptor` - interprets WebView requests and schedules appropriate http requests.
 * package `com.good.gd.webview_V2.bbwebview.tasks.http`
   

## License

Apache 2.0 License


## Disclaimer

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Before you use this sample application which integrates the BlackBerry Dynamics SDK (the “SDK”) you must read the BlackBerry Dynamics SDK Development License Agreement (“the Agreement”). By building this sample which integrates the SDK, you agree to the Agreement terms and conditions. If you do not accept them, do not download,  build or run this sample application.