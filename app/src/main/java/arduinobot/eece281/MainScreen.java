/*
 * Author: Justin C. Wong
 * IR Decoder: Tim Choy
 * Student #: 35922137
 * EECE 281 Group: 8C
 * Android App for EECE 281
 * Using IR to control our Arduino Uno Ultrasonic Robot
 * Including Voice Recognition and Custom Buttons
 */



package arduinobot.eece281;
//import of documentation relevant classes/objects etc.
import arduinobot.eece281.util.SystemUiHider;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.ConsumerIrManager; // ir blaster
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import java.util.concurrent.TimeUnit;
import android.content.Intent;
import android.speech.RecognizerIntent;
import java.util.List;


/**
 *
 * MainScreen where you are going to be sending your IR commands in accordance to button clicks or
 * speech input.
 *
 */
public class MainScreen extends Activity {

    // CODE FOR BUTTONS ********************************************************************************
    ConsumerIrManager remote; //creating a ConsumerIrManager instance

    //IR codes are in accordance to decoding from Sony TV HEX codes to the IR frequency and pulse patterns (integer and integer[])
    //set frequency to 40k.
    private static final int FREQ = 40244;

    //pattern for auto mode
    private static final int[] AUTO = {96, 24, 24, 24, 24, 24, 24, 24, 24, 24, 48, 24, 24, 24, 24, 24, 48, 24, 24, 24, 24, 24, 24, 24, 24, 1080};

    //pattern for manual mode
    private static final int[] MANUAL = {96, 24, 48, 24, 24, 24, 24, 24, 24, 24, 48, 24, 24, 24, 24, 24, 48, 24, 24, 24, 24, 24, 24, 24, 24, 1055};

    //pattern for left
    private static final int[] LEFT = {96, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 48, 24, 24, 24, 24, 24, 24, 24, 24, 1104};

    //pattern for right
    private static final int[] RIGHT = {96, 24, 48, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 48, 24, 24, 24, 24, 24, 24, 24, 24, 1080};

    //pattern for forward
    private static final int[] FORWARD = {96, 24, 24, 24, 48, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 48, 24, 24, 24, 24, 24, 24, 24, 24, 1080};

    //pattern for reverse
    private static final int[] REVERSE = {96, 24, 48, 24, 48, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 48, 24, 24, 24, 24, 24, 24, 24, 24, 1055};


    /*
     * Each of the button methods below are linked to the buttons found in activity_main_screen.xml.
     * On the button clicks, they will get the usage of the IR blaster and transmit the IR freq and pattern
     * listed above.
     */
    public void forwardButtonOnClick(View v)  {
            remote = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
            remote.transmit(FREQ, FORWARD);
    }

    public void reverseButtonOnClick(View v) {
            remote = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
            remote.transmit(FREQ, REVERSE);
    }

    public void leftButtonOnClick(View v) {
            remote = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
            remote.transmit(FREQ, LEFT);
    }

    public void rightButtonOnClick(View v) {
            remote = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
            remote.transmit(FREQ, RIGHT);
    }

    public void autoButtonOnClick(View v) {
            remote = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
            remote.transmit(FREQ, AUTO);
    }

    public void stopButtonOnClick(View v) throws InterruptedException {
        //this is transmitted three times to ensure that the reading will be received when in auto mode
        //this requires three transmissions as they may miss transmissions while running the automation code
        int index;

            for (index = 0; index < 2; index++) {
                remote = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
                remote.transmit(FREQ, MANUAL);
                TimeUnit.MILLISECONDS.sleep(150);
            }
            remote = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
            remote.transmit(FREQ, MANUAL);

    }

//this speech button is linked to the speech button in activity_main_screen.xml
    public void speechButton (View v) {
        displaySpeechRecognizer(); //run the Google Android Developer speech recognizer
    }

// CODE FOR BUTTONS ********************************************************************************

// CODE FOR VOICE COMMAND***************************************************************************
private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    public void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text (the query will return in onActivityResult)
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            System.out.println(spokenText); //print the spokenText for debugging

            // Process the spokenText and send the corresponding IR codes depending on the query
            if (spokenText.contains("go forward")){
                remote = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
                remote.transmit(FREQ, FORWARD);
                System.out.println("Go forward received");
            }
            else if (spokenText.contains("reverse")){
                remote = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
                remote.transmit(FREQ, REVERSE);
                System.out.println("reverse received");

            }
            else if (spokenText.contains("activate auto mode")){
                remote = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
                remote.transmit(FREQ, AUTO);
                System.out.println("Activate Auto Mode received");
            }
            else if (spokenText.contains ("go left")){
                remote = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
                remote.transmit(FREQ, LEFT);
                System.out.println("Go left received");
            }
            else if (spokenText.contains("go right")){
                remote = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
                remote.transmit(FREQ, RIGHT);
                System.out.println("Go right received");
            }
            else if (spokenText.contains("activate manual mode")) {
                remote = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
                remote.transmit(FREQ, REVERSE);
                    System.out.println("Activate Manual Mode received");

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
// CODE FOR VOICE COMMAND***************************************************************************
// The below code is simply the standard UI code for an Android app


    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);


        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
