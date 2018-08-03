AndroidWebView
==============

This sample demonstrates how to create an Android WebView that makes use of BlackBerry Dynamics secure connectivity to load content.  This sample requires the BlackBerry Dynamics SDK for Android to be installed.  Refer to the Getting Started guide for instructions on how to install
and configure it.  https://developers.blackberry.com/us/en/resources/get-started/blackberry-dynamics-getting-started.html?platform=android#step-1

Applies To:
- Android Studio
- BlackBerry Dynamics SDK

Author(s): 
- Jim Hawkins
- Mark Sohm

Contributing:
To contribute code to this repository you must be signed up as an official contributor.

This sample was tested with the following environment:

- BlackBerry Dynamics SDK 4.0.0.5079
- Android Studio 3.1.3
- Android 8.1.0


**How To Open the Project**

Note that you must install the BlackBerry Dynamics SDK for Android in order to build this sample.

1. Clone the repo to your computer.
2. Launch Android Studio.
3. Click on File menu, Open.
4. Navigate to the directory you saved the project and click OK.
5. Open the settings.json file in the assets directory and update the GDApplicationID to be unique.

Components
==========
The components of the Android WebView are as follows.

Mobile Application for Android
==============================
The main component is an Android application. It has the following sub-components.

-   Custom WebView class, StreamWebView, that is a GDWebView subclass. There
    isn't much code in StreamWebView. It is instantiated by the MainActivity
    class.
-   WebViewClient implementation, StreamWebViewClient, that intercepts HTTP
    requests, by implementing the `shouldInterceptRequest` method. See more
    detailed notes under WebViewClient Implementation, below.
-   InputStream subclass, WebInputStream, that is suitable to be used in the
    above context. See more detailed notes under InputStream Implementation,
    below.
-   ResponseBuilder helper class for assembling WebResourceResponse instances.
    Instances of WebResourceResponse cannot be used for assembly in all cases,
    because a WebResourceResponse cannot be constructed for a 3xx redirection
    HTTP response.
-   Request body data bridge, that intercepts and caches HTTP request body data.
    See more detailed notes under Request Body Data Bridge, below.
-   Application subclass, WebViewSpike, that co-ordinates all the above and the
    user interface options. It also has some miscellaneous functions. See more
    details under Application Subclass, below.

WebViewClient Implementation
----------------------------
The WebViewClient implementation handles HTTP requests by doing one of the following.

-   Open a file in the application assets, and build a WebResourceResponse from
    the stream. The application user interface is implemented in this way.

-   Execute the HTTP request via BlackBerry Dynamics secure communication.

    The request is sent using the GDHttpRequest interface. All stages are executed 
	in this class, up to and including the `execute()`. A WebResourceResponse is 
	then built from the returned status code, headers, and data stream.
    
-   Return null so that the WebView native code executes the HTTP processing
    itself. This option is for comparison and can be selected in the user
    interface.

InputStream Implementation
--------------------------
The WebInputStream class is a custom InputStream subclass from which a
WebResourceResponse can be built.

The subclass wraps the data stream of an HTTP request in another "outer" stream.
Multiple "inner" streams can be wrapped and will be serialised into the outer
stream. This supports injection of JavaScript into retrieved content.

The subclass closes all wrapped inner streams, and executes an HTTP connection
shutdown, when the outer stream is closed. The subclass also has a lot of
diagnostic logging in this sample.

Request Body Data Bridge
------------------------
Body data from an HTTP request has to be "bridged" from the WebView to the
application code.

Data bridging is implemented in the sample by the following code.

-   JavaScript (JS) interception code is injected by the InputStream
    implementation, see above.
    
    The JS code replaces a number of methods in the JavaScript built-in class
    prototypes, so that the body data can be intercepted and bridged out.    
    
-   Java code receives and caches the bridged out data from the JS interception,
    above.

    The receiving end of the bridge is in the StreamWebViewClient class, in the
    JavaScriptBridge inner class. The cache is also in StreamWebViewClient, in
    the requestCache property, which is a Java Map.

-   Java code writes the data that was cached, above, into the HTTP request,
    when the corresponding WebResourceRequest is received.
    
    The writing code is in the StreamWebViewClient class, in the executeHTTP
    method, and in the ReaderContentProducer inner class.

The JS interception creates a universally unique identifier (UUID) for each HTTP
request with body data. The UUID is:

-   Sent to the Java end of the bridge, with the body data.
-   Included in the HTTP request URI, either as a query parameter, or in an HTTP
    header.

Application Subclass
--------------------
The Application subclass in the sample implements all the following.

-   Bridge between the JavaScript in the WebView and the native Java code.
-   Local receiver for Intnet broadcasts from the BlackBerry Dynamics runtime,
    and therefore authorisation life cycle.
-   Instantiates everything except the WebView, which is instantiated by the
    main Activity class.
-   ActivityLifecycleCallbacks implementation.

User Interface
==============
The mobile application's user interface is an HTML page that is loaded from its
assets folder. The intercept code handles this by inspecting the URI and
diverting requests for resources on the `localhost:1` server.

The user interface HTML is a combination of the following.

-   Hard-coded content including some formatting and fixed URI links.
-   Links and text retrieved from the management console.
-   User interface controls, for selection of options such as whether
    HTTP is to be intercepted, whether any content is to be injected, and
    whether the Apache code should handle redirection.



THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.