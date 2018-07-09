BEMSPresenceServiceSample
This sample demonstrates the use of the BlackBerry Enterprise Mobility Server (BEMS) Presence Service, which
can be used to poll for and receive updates of contacts presence status within Microsoft Lync.

Applies To
Android Studio
BlackBerry Dynamics SDK

Author(s)
Mark Sohm

Contributing

To contribute code to this repository you must be signed up as an official contributor.

This sample was tested with the following environment:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Good Dynamics SDK 4.0.0.5079
Android Studio 3.1.2
Android 7.1.1


How To Open the Project
~~~~~~~~~~~~~~~~~~~~~~~

**Note that you must install the BlackBerry Dynamics SDK for Android in order to build this sample.**

1. Clone the repo to your computer.
2. Launch Android Studio.
3. Click on File menu, Open.
4. Navigate to the directory you saved the project and click OK.
5. Open the settings.grade file and update the gd projectDir to point to the BlackBerry Dynamics SDK location on your computer.
6. Open the settings.json file in the assets directory and update the GDApplicationID to be unique.
7. Open AppConstants.java and complete the 2 TODO items.
	a) Add email addresses for your contacts.
	b) Create a unique identifier for your application that will identify it with the BEMS Presence Service.


THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.