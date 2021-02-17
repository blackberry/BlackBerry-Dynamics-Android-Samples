# BlackBerry Dynamics Android Samples

## BlackBerry Dynamics Overview



The BlackBerry Dynamics Secure Mobility Platform from BlackBerry consists of the BlackBerry Dynamics (BD) SDK services that provide a powerful set of tools to ISV and enterprise developers. It allows them to focus on building their commercial and business apps rather than learning how to secure, deploy and manage those apps.

For more information refer to the [BlackBerry Dynamics Developer Web Site](https://developers.blackberry.com/us/en/products/blackberry-dynamics/android.html).



## Samples

This repository contains sample projects that demonstrate the use of BlackBerry Dynamics APIs.  Some of these samples were created for a BlackBerry Developer Blog post, event or online session.  Samples are updated with best effort support, but not all are tested updated against each SDK release.  Note that the samples included with the BlackBerry Dynamics SDK installation are updated and verified against each SDK release.

The table below describes the main features each sample demonstrates and notes if it is actively maintained.

------



| **Sample Name**                                              | **Key Features**                                             | **Actively Maintained** |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ----------------------- |
| [AndroidThingsSensorDemo](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/AndroidThingsSensorDemo) | An example of an Android Things application integrated with the BlackBerry Dynamics SDK. | No                      |
| [AndroidWebView](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/AndroidWebView) | Extends the native [Android WebView](https://developer.android.com/reference/android/webkit/WebView) and implements BlackBerry Dynamics networking APIs, allowing connectivity to your corporate intranet. | Yes                     |
| [AutomatedTestSupportLibrary](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/AutomatedTestSupportLibrary) | The BlackBerry Dynamics SDK includes the BlackBerry Dynamics Automated Test Support Library (ATSL) to support automated testing for your BlackBerry Dynamics apps. This library is delivered as a binary with the BlackBerry Dynamics SDK, but is made available as sources here to enable you to review the implementation and customize it.<br /><br />The library includes helper functions for testing common user interactions in BlackBerry Dynamics apps, such as activation and authorization. The configuration and structure of the library is compatible with the native Android Testing Support Library.<br /><br />You can use this library, the native library components, Gradle and JUnit tools to automate the building, execution, and reporting of your application tests. | Yes                     |
| [BDVideoPlayback](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/BDVideoPlayback) | Demonstrates how to play back video files that are saved in the BlackBerry Dynamics secure file system using the Android MediaPlayer with a custom MediaPlayerDataSource. | No                      |
| [BEMSDirectoryLookupSample](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/BEMSDirectoryLookupSample) | Demonstrates the use of the BlackBerry Enterprise Mobility Server (BEMS) Directory Lookup Service, which can be used to search a global address book and the user's personal address book. | No                      |
| [BEMSDocsServiceSample](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/BEMSDocsServiceSample) | Demonstrates the use of the BlackBerry Enterprise Mobility Server (BEMS) Docs Service, which can be used to list, upload and download files from various file repositories. | No                      |
| [BEMsPresenceServiceSample](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/BEMsPresenceServiceSample) | Demonstrates the use of the BlackBerry Enterprise Mobility Server (BEMS) Presence Service, which can be used to poll for and receive updates of contacts presence status within Skype for Business (Microsoft Lync). | No                      |
| [CrossTheBridge](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/CrossTheBridge) | Demonstrates how an application can use BlackBerry Bridge to securely open a Microsoft Word document in Microsoft Word secured by Intune from a BlackBerry Dynamics application. This approach can be used for other Microsoft file types and other apps in the Microsoft Office Suite. | No                      |
| [ExoPlayerVideoPlayback](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/ExoPlayerVideoPlayback) | Demonstrates how to play back video files that are saved in the BlackBerry Dynamics secure file system using ExoPlayer. The same approach could be used for audio files. | No                      |
| [GDHttpClientFileUpload](./GDHttpClientFileUpload)           | Demonstrates how to perform a forms based HTTP file upload using BlackBerry Dynamics networking APIs. | No                      |
| [GettingStarted](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/GettingStarted) | Pairs with GettingStartedBD to provide examples of an Android application before and after integrating with the BlackBerry Dynamics SDK. This sample provides examples of features commonly used in an application that require modification for integrate with the BlackBerry Dynamics SDK, including file storage, SQLite usage and HTTP and socket communication. | Yes                     |
| [GettingStartedBD](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/GettingStartedBD) | Pairs with GettingStarted to provide examples of an Android application before and after integrating with the BlackBerry Dynamics SDK. This sample provides examples of features commonly used in an application that require modification for integrate with the BlackBerry Dynamics SDK, including file storage, SQLite usage and HTTP and socket communication. | Yes                     |
| [GettingStartedKotlin](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/GettingStartedKotlin) | Pairs with GettingStartedKotlinBD to provide examples of an Android Kotlin application before and after integrating with the BlackBerry Dynamics SDK. This sample provides examples of features commonly used in an application that require modification for integrate with the BlackBerry Dynamics SDK, including file storage, SQLite usage and HTTP and socket communication. | Yes                     |
| [GettingStartedKotlinBD](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/GettingStartedKotlinBD) | Pairs with GettingStartedKotlin to provide examples of an Android Kotlin application before and after integrating with the BlackBerry Dynamics SDK. This sample provides examples of features commonly used in an application that require modification for integrate with the BlackBerry Dynamics SDK, including file storage, SQLite usage and HTTP and socket communication. | Yes                     |
| [Native-Media](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/Native-Media) | This sample application is based on a version of the Native-Media sample app from [Google NDK samples](https://github.com/googlesamples/android-ndk/tree/master/native-media)<br />The modifications to the generic sample application comprise -<br/><br/>- Integration of the Application Java code with the Good Dynamics lifecycle. Ensuring application can be activated and authorized<br/>- Code which will copy provided video from SDcard into GD Secure Container to be played<br/>- Integration of the Application C code with the Good Dynamics Secure Container to read data direct from the Secure Container | No                      |
| [SharedServicesTestApp](./SharedServicesTestApp)             | Demonstrates how various BlackBerry Dynamics Shared Services (also known as AppKinetics). | No                      |
| [TheConfigurator](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/TheConfigurator) | Demonstrates how to use a custom App Policy uploaded into the BlackBerry UEM Admin Console, which can be used to remotely configure an application. | No                      |
| [UEMWebServices](https://github.com/blackberry/BlackBerry-Dynamics-Android-Samples/tree/master/UEMWebServices) | Demonstrates various REST APIs including authentication to UEM, automating administrative tasks, and integrations with BlackBerry UEM. | No                      |



## License

Apache 2.0 License