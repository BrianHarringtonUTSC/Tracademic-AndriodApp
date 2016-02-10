# Tracademic Android Application

## Warnings
- The Mag stripe reader uses the headphone jack to detect card swipes. To do this as accurately as possible, it turns the volume to max. If you have headphones plugged in, as I unfortunately once did, your ears will hear the delightful sound of something resembling a banshee screaming at max volume. Or me singing. DO NOT LAUNCH THE MAG STRIPE READER WITH HEADPHONES PLUGGED IN! The application resets the volume to what the user defined it before launching the mag stripe reader when the reader closes.

## Demo
https://www.youtube.com/watch?v=7H90pwhxFBQ

## Installation Instructions

#### User
- Download and launch app/app-release.apk on your Android smartphone. 
- NOTE: Ensure that under Settings -> Security you have allowed "Unknown Sources".

#### Developer
- Install and launch Android Studio.
- VCS -> Checkout from Version Control -> GitHub.
- Enter your credentials and the URL for this repository.

### Known Issues
- Current HTTP Cookie implementation in Android does not calculate hasExpired() correctly (it always returns False) - [see relevant open issue](https://code.google.com/p/android/issues/detail?id=191981). 
  - Currently, as a temporary workaround, if we get the list of students with an expired cookie, we should get back a list of students with no username (the server only returns username when a TA is logged in) - in this case we force a logout as we can assume the cookie expired.
  - The ideal situation is that the HTTP Cookie hasExpired implementation is fixed or the sever is modified to allow a special param when logging in so that the cookie never expires on the server, or has a very long expiry time. 

## TODOs
- Cancel all Volley requests for all network connecting activities on the onStop.
- Add XML entry to detect Acer tablet.
- Checkin functionality (currently each swipe opens the corresponding student info and TA has to manually select 1 XP).
- Fix certificate issues to prevent spoofing (currently all certificates are trusted via the "trustEveryone" method in HTTPClientSingleton).
- Make code more efficient.
  - Currently a student lookup takes 2n steps where n is the number of students. Students are looked up based on student number so it makes sense to have a map mapping student number to student objects. Unfortunately, the adapter which displays the list of students requires a list to work. The positions also need to be consistent (ruling out HashMap). One solution is to use a LinkedHashMap (2n storage), or TreeMap (logarithmic lookup and inserts).
  - The app currently gets all students and re renders the list in the background every time a user goes to view the list of students. This lets us avoid having to manually update an indivudal student when a point is given to them and since the update happens in the background, the user does not have to wait, but it is inefficient. A better way would be to update the student's points manually and then do a GET api/user/:id to confirm that the server is consistent with our info.
- The Fragments currently are passed the MainActivity as callback. This is not the best coding style. The best way is to create an interface that the MainActivity implements, then the callback should only be the interface. This stops any other methods from being exposed to the fragment and the fragment becomes flexible to be used by any class that implements the interface. 

## Development Tips
- Adhere to Google's best [design](http://developer.android.com/design/index.html) and [coding](http://developer.android.com/index.html) standards.
  - The Tracademic application was built with Material Design in mind. For example, it uses a [Floating Action Button](https://www.google.com/design/spec/components/buttons-floating-action-button.html) to launch the Mag stripe reader. If you are adding core functionaltiy, such as scanning T-cards using the phone's camera, or the checkin functionaltiy, you should use the menu which launches from the pressing the Floating Action Button [(example)](https://material-design.storage.googleapis.com/publish/material_v_4/material_ext_publish/0B6GnvA6rl3tYWEZGZFBuc1RxMEk/components_fab_flyouts_do.png).
  - The Tracademic application uses [Fragments](http://developer.android.com/guide/components/fragments.html) as recommended by Google to provide a responsive and fluid experience. The main activity controls two fragments (one to list all students and one for opening a student's info). It implements an interface which allows us to launch the appropriate fragments easily.
- Change the BASE_URL to the dev server to test changes.
- Rebuild and push the APK after every commit (Build -> Generate Signed APK).

## Card Reader
- The current card reader in use is the [Unimag Shuttle](http://www.idtechproducts.com/products/mobile-readers/141.html). It leaves much to be desired but does work. There is one giant XML file with configurations and frequencies for the headphone jack of various phones and tablets. To add support for a new phone or tablet simply add a corresponding entry to the xml file. They seem to periodically update the XML file so it should also be manually updated in the app. Apparently they claim they can grab configurations from the internet but their code is for pre 2.3 Android and hardly works to begin with so better to rely as little as possible on it. Ideally in the future T cards will have NFC built in so we won't need this.
- The XML file, documentation and code can be found in the [Unimag Shuttle SDK](http://sdk.idtechproducts.com/sdk_info.asp). The password is 393FFCC6.
