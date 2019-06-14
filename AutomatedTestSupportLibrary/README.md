# Automated Test Support Library (ATSL) for Android

The BlackBerry Dynamics SDK includes the BlackBerry Dynamics Automated Test Support Library (ATSL) to support automated testing for your BlackBerry Dynamics apps. This library is delivered as a binary with the BlackBerry Dynamics SDK, but is made available as sources here to enable you to review the implementation and customize it.

The library includes helper functions for testing common user interactions in BlackBerry Dynamics apps, such as activation and authorization. The configuration and structure of the library is compatible with the native Android Testing Support Library. 

You can use this library, the native library components, Gradle and JUnit tools to automate the building, execution, and reporting of your application tests.

For more information refer to 'Implementing automated testing for BlackBerry Dynamics apps' within the [BlackBerry Dynamics Developer Guide](https://docs.blackberry.com/en/development-tools/blackberry-dynamics-sdk-android/) 

### Prerequisites

 - BlackBerry Dynamics SDK for Android
 - Android SDK supported by BlackBerry Dynamics 
 - Android Testing Support Library

Please see 'Requirements' within the [BlackBerry Dynamics Developer Guide](https://docs.blackberry.com/en/development-tools/blackberry-dynamics-sdk-android/) for the currently supported SDK and Android versions.

### Usage

1. Add the Android Testing Support Library and the BlackBerry Dynamics ATSL to your project.  Add the following to the build.gradle file:
	    
	    // Dependencies on the modules of the Android Testing Support Library.
	    androidTestCompile 'com.android.support:support-annotations:23.0.1'
	    androidTestCompile 'com.android.support.test:rules:0.4'
	    androidTestCompile 'com.android.support.test:runner:0.4'
	    androidTestCompile 'com.android.support.test.uiautomator:uiautomator-v18:2.0.0'

	    // Include the Automated Test Support Library in the androidTest target.
	    androidTest {
	        java.srcDirs = ['tests']
	        // Include the Automated Test Support Library package.
	        // The automated_test_support_library_path value is defined in the
	        // gradle.properties file.
	        java.srcDirs += [automated_test_support_library_path + '/src']
	        java.srcDirs += [automated_test_support_library_path + '/src-handheld']
	        assets.srcDirs = [automated_test_support_library_path + '/assets']
	    }
	    
	    // Following configuration is required to set the download location of
	    // dependent projects.
	    allprojects {
	        repositories {
	            mavenCentral()
	        }
	    }

2. Add a line similar to the following to the build.gradle file to define a runner for test 
`instrumentation: testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"`

3. Add or write code for your app tests. Use the helper functions in the ATSL in your test code.

You can use the code from the tests in any of the BlackBerry Dynamics sample apps as a starting point. For example, in the SecureSQL sample app, the first test (test_1_activation), executes BlackBerry Dynamics activation and unlock as an automated test. Put the code in one of the source directories specified in the androidTest target. In the above example, only the tests sub-directory is specified.

## Support

-   BlackBerry Developer Community:  [https://developers.blackberry.com](https://developers.blackberry.com)

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
