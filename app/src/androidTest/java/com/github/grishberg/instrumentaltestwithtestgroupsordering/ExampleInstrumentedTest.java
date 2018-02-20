package com.github.grishberg.instrumentaltestwithtestgroupsordering;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @InstrumentalTest
    @Test
    public void instrumentalTest1() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.github.grishberg.instrumentaltestwithtestgroupsordering", appContext.getPackageName());
    }

    @InstrumentalTest
    @Test
    public void instrumentalTest2() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.github.grishberg.instrumentaltestwithtestgroupsordering", appContext.getPackageName());
    }

    @InstrumentalTest
    @Test
    public void instrumentalTest3() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.github.grishberg.instrumentaltestwithtestgroupsordering", appContext.getPackageName());
    }
}
