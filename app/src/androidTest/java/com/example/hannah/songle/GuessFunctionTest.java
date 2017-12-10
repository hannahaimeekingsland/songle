package com.example.hannah.songle;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class GuessFunctionTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    GrantPermissionRule grantPermission = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void guessFunctionTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.button2), withText("Play"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.CoordinatorLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.button4), withText("Amateur"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        button.perform(click());

        ViewInteraction button2 = onView(
                allOf(withText("Song 1"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearlayout),
                                        2),
                                0)));
        button2.perform(scrollTo(), click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.guessButton), withText("Guess!"),
                        childAtPosition(
                                allOf(withId(R.id.frame_layout),
                                        childAtPosition(
                                                withId(R.id.map),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.guessText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("song 1 incorrect guss"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.submitGuess), withText("Guess!"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.tryAgain), withText("Try again"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.guessText),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("bohemian rhapsody"), closeSoftKeyboard());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.submitGuess), withText("Guess!"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.mainMenuButton), withText("Main Menu"),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                1),
                        isDisplayed()));
        appCompatButton6.perform(click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
