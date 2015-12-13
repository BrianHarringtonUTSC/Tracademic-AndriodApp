# Tracademic Android Application

## Warnings
- The Mag stripe reader uses the headphone jack to detect card swipes. To do this as accurately as possible, it turns the volume to max. If you have headphones plugged in, as I unfortunately once did, your ears will hear the delightful sound of something resembling a banshee screaming at max volume. Or me singing. DO NOT LAUNCH THE MAG STRIPE READER WITH HEADPHONES PLUGGED IN! The application resets the volume to what the user defined it before launching the mag stripe reader when the reader closes.

## Demo
https://www.youtube.com/watch?v=7H90pwhxFBQ

## Installation Instructions

#### User
- Download and launch app-release-unsigned.apk on your Android smartphone. 
- NOTE: Ensure that under Settings -> Security you have allowed "Unknown Sources".

#### Developer
- Install and launch Android Studio.
- VCS -> Checkout from Version Control -> GitHub.
- Enter your credentials and the URL for this repository.


## TODOs
- Persistent Cookies.
- Migrate raw HTTPUrlConnection to Volley.
- Checkin functionality (currently each swipe opens the corresponding student info and TA has to manually select 1 XP).
- Add Custom Application class that holds constants and cookie manager.
- Fix certificate issues to prevent Man in the Middle attacks.
- Handling server down, internet down after app logs in, edge cases.
- Logout.
- Make code more efficient.
  - Currently a student lookup takes 2n steps where n is the number of students. Students are looked up based on student number so it makes sense to have a map mapping student number to student objects. Unfortunately, the adapter which displays the list of students requires a list to work. The positions also need to be consistent (ruling out HashMap). One solution is to use a LinkedHashMap (2n storage), or TreeMap (logarithmic lookup and inserts).


## Development Tips
- Adhere to Google's best [design](http://developer.android.com/design/index.html) and [coding](http://developer.android.com/index.html) standards.
  - The Tracademic application was built with Material Design in mind. For example, it uses a [Floating Action Button](https://www.google.com/design/spec/components/buttons-floating-action-button.html) to launch the Mag stripe reader. If you are adding core functionaltiy, such as scanning T-cards using the phone's camera, or the checkin functionaltiy, you should use the menu which launches from the pressing the Floating Action Button [(example)](https://material-design.storage.googleapis.com/publish/material_v_4/material_ext_publish/0B6GnvA6rl3tYWEZGZFBuc1RxMEk/components_fab_flyouts_do.png).
- Change the BASE_URL to the dev server to test changes.
- Rebuild and push the APK after every commit.
