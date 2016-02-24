package edu.gcc.whiletrue.pingit;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.PreferenceMatchers.isEnabled;
import static android.support.test.espresso.matcher.PreferenceMatchers.withKey;
import static android.support.test.espresso.matcher.PreferenceMatchers.withSummaryText;
import static android.support.test.espresso.matcher.PreferenceMatchers.withTitleText;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagKey;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by BOWMANRS1 on 2/23/2016.
 */

@RunWith(AndroidJUnit4.class)
public class SettingsUITest {


    @Rule//startup activity to test
    public ActivityTestRule<SettingsActivity> mActivityRule = new ActivityTestRule<>(
            SettingsActivity.class);

    //Test that a user can open the Ringtone dialogue option in the Settings menu
    /*@Test
    public void test40() {
        onData(withKey("notification_sound_preference")).perform(click());

        //wait for dialog to appear, then dismiss it
        pressBack();
        TODO: Fix this; test freezes once dialogue is opened
    }*/

    //Ensure the switch moves from off to on when tapped. Switch starts off.
    /*@Test
    public void test42() {
        onData(withKey("notification_resend_toggle")).perform(click());

        onData(withSummaryText("Notification will only be sent once.")).check(matches(isDisplayed()));
        //TODO: Find a way to check this because I don't know how.
    }*/

    //Ensure the switch moves from on to off when tapped. Switch starts on.
    /*@Test
    public void test43() {
        onData(withKey("notification_resend_toggle")).perform(click());

        onData(withSummaryText("Repeat notification if not dismissed.")).check(matches(isDisplayed()));
        //TODO: Find a way to check this because I don't know how.
    }*/

    //Test that tapping Notification Resend Delay launches a dialog box
    @Test
    public void test44() {
        onData(withKey("notification_resend_delay")).perform(click());

        onView(withText("Notification Resend Delay")).perform(ViewActions.pressBack());
    }

    //just test that the dialog box appears to change the display name. Then cancel the dialog
    @Test
    public void test46(){
        onData(withKey("display_name")).perform(click());
        //type a name
        //onView(withText("Display Name")).perform(typeTextIntoFocusedView("Stuart Bowman"), closeSoftKeyboard());

        //wait for dialog to appear, then dismiss it
        pressBack();
        //TODO: Ensure this is a thorough enough test. This also hangs quite a bit
    }

    //tap logout button and make sure confirmation dialog appears
    @Test
    public void test51() {
        onView(withId(R.id.logoutBtn)).perform(click());
        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.confirmLogoutMsg)).perform(ViewActions.pressBack());
    }

}
