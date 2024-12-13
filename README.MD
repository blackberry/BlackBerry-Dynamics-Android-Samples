# BlackBerry Dynamics Android Samples

Samples built with BlackBerry Dynamics SDK.

## BlackBerry Dynamics Overview

[BlackBerry Dynamics](https://docs.blackberry.com/en/endpoint-management/blackberry-dynamics) is a mobile application development platform that enables secure communication, secure storage and exchange of data between enterprise apps according to enterprise security policies.

For details on features and a development guide, see [BlackBerry Dynamics SDK for Android](https://docs.blackberry.com/en/development-tools/blackberry-dynamics-sdk-android/)

## Samples

This repository contains sample projects that demonstrate the use of BlackBerry Dynamics APIs on Android.

### Getting Started

This sample demonstrate features commonly used in the BlackBerry Dynamics applications. These include how to use secure file storage, secure database and secure communication.

| App Name                                      | Language        |
| --------------------------------------------- | --------------- |
| [Dynamics-GettingStarted](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/1-GettingStarted//GettingStartedBD) | Java            |
| [Dynamics-GettingStartedKotlin](https://github.com/blackberry/BlackBerry-Dynamics-iOS-Samples/tree/master/1-GettingStarted//GettingStartedKotlinBD) | Kotlin           |

A version of these samples prior to Dynamics integration called 'GettingStarted' and GettingStartedKotlin' can also be found in the Getting Started folder.

### Features

These samples demonstrate specific features available in BlackBerry Dynamics applications. They are updated with best effort as not all are tested and updated against each SDK release.

------
| App Name                                                     | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [AppBasedCertImport](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/2-Features/AppBasedCertImport) | How to use the app-based PKI solution to provide certificates for use by BlackBerry Dynamics. |
| [AppConfigurator](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/2-Features/AppConfigurator)  | Demonstrates how to use a custom App Policy uploaded into the BlackBerry UEM Admin Console, which can be used to remotely configure an application. |
| [AppKinetics](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/2-Features/AppKinetics) <br> [SaveEditClient](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/2-Features/AppKinetics-SaveEditClient) <br>[SaveEditService](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/2-Features/AppKinetics-SaveEditService)  | How to search for, create, and implement an Application-Based Service, using BlackBerry Dynamics AppKinetics. These concepts are demonstrated together in the AppKinetics sample by implementing a consumer and a provider for the Transfer File service. The SaveEditClient and SaveEditService samples split out the consumer and provider into different apps to illustrate transfering the file between apps on the device. |
| [GDInteraction](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/2-Features/GDInteraction)  | Retrieval of remote settings from the enterprise management console and handling the state change and configuration events. |
| [GreetingsClient](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/2-Features/GreetingsClient) <br> [GreetingsServer](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/2-Features/GreetingsServer)  | Demonstrates how to create your own AppKinetics application-based service. This example uses a fictitious Greetings Service to show how to securely share a greeting between two Dynamics applications (client and server). |
| [KeyManager](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/2-Features/KeyManager)  | This app demonstrates how to present a Credential Manager user interface that enables the user to set up, review, and manage user certificates and keys. The user is granted this capability by admin through assignment of User Certificate and User Credential profiles. |
| [OkHttpBD](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/2-Features/OkHttpBD)  | This sample demonstrates the use of com.blackberry.okhttpsupport.interceptor.BBCustomInterceptor, which allows the OkHttp library to be used for network communication using BlackBerry Dynamics enterprise network connectivity. |
| [SecureCopyPaste](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/2-Features/SecureCopyPaste)  | Demonstrates the difference between UI text controls in the BlackBerry Dynamics Platform (GDTextView, GDEditText, GDAutoCompleteTextView, GDSearchView, GDWebView) and the corresponding default UI text controls in Android. |
| [SharedServicesTestApp](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/2-Features/SharedServicesTestApp)  | Demonstrates various BlackBerry Dynamics Shared Services (also known as AppKinetics). |
| [WebView_BDSDK](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/2-Features/WebView_BDSDK)  | This sample demonstrates the use of com.blackberry.bbwebview.BBWebView, which extends android.webkit.WebView and provides BlackBerry Dynamics network connectivity. |

### Archive

These older samples are not maintained and included for reference. 

|                                                              | **Description**                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [AndroidThingsSensorDemo](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/3-Archive/AndroidThingsSensorDemo) | An example of an Android Things application integrated with the BlackBerry Dynamics SDK.|
| [BDVideoPlayback](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/3-Archive/BDVideoPlayback) | Demonstrates how to play back video files that are saved in the BlackBerry Dynamics secure file system using the Android MediaPlayer with a custom MediaPlayerDataSource.
| [BEMSContactServiceSample](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/3-Archive/BEMSContactServiceSample) | This sample demonstrates the use of the BlackBerry Enterprise Mobility Server (BEMS) Contact Service, which can be used to create, read and modify a user’s personal contacts.|
| [BEMSDirectoryLookupSample](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/3-Archive/BEMSDirectoryLookupSample) | Uses BlackBerry Enterprise Mobility Server (BEMS) Directory Lookup Service, which can be used to search a global address book and the user’s personal address book.|
| [BEMSDocsServiceSample](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/3-Archive/BEMSDocsServiceSample) | Demonstrates use of the BlackBerry Enterprise Mobility Server (BEMS) Docs Service, which can be used to list, upload and download files from various file repositories.|
| [BEMSPresenceServiceSample](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/3-Archive/BEMSPresenceServiceSample) | Use of the BlackBerry Enterprise Mobility Server (BEMS) Presence Service, which can be used to poll for and receive updates of contacts presence status within Skype for Business (Microsoft Lync).	Gitlab (existing)|
| [BypassUnlock](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/3-Archive/BypassUnlock) | This sample shows how part of the application user interface can remain accessible after the BlackBerry Dynamics idle time out has expired.|
| [CrossTheBridge](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/3-Archive/CrossTheBridge) | Demonstrates how an application can use BlackBerry Bridge to securely open a Microsoft Word document in Microsoft Word secured by Intune from a BlackBerry Dynamics application |
| [GDHttpClientFileUpload](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/3-Archive/GDHttpClientFileUpload) | Demonstrates how to perform a forms based HTTP file upload using BlackBerry Dynamics networking APIs.|
| [Native-Media](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/3-Archive/Native-Media) | This sample application is based on a version of the Native-Media sample app from Google NDK samples. <br> The modifications to the generic sample application comprise: <br>- Integration of the Application Java code with the BlackBerry Dynamics lifecycle. Ensuring application can be activated and authorized. <br>- Code which will copy provided video from SDcard into Dynamics Secure Container to be played. <br>- Integration of the Application C code with the BlackBerry Dynamics Secure Container to read data direct from the Secure Container|
| [SecureSQL](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/3-Archive/SecureSQL) | Demonstrates use of the BlackBerry Dynamics Secure SQL Database API's to add, edit, delete and display contact entries in a secure database. |
| [SecureStore](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/3-Archive/SecureStore) | Shows how to read and write encrypted files securely and with intelligible file names and contents for both handheld and wearable apps. Note: the BlackBerry Dynamics wearable framework is no longer supported. |
| [UEMWebServices](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/3-Archive/UEMWebServices) | This sample demonstrates various REST and SOAP APIs including authentication to UEM, automating administrative tasks, and integrations with BlackBerry UEM. |

**Contribute**

* To contribute to this repository, [sign up as an official contributor](http://blackberry.github.com/howToContribute.html).

## License

Apache 2.0 License

## Disclaimer

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.