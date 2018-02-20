package com.github.grishberg.instrumentaltestwithtestgroupsordering;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by grishberg on 19.02.18.
 */
@RunWith(AndroidJUnit4.class)
public class ExampleEspressoTest {

    @EspressoTest
    @Test
    public void espressoTest1() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.github.grishberg.instrumentaltestwithtestgroupsordering", appContext.getPackageName());
    }

    @EspressoTest
    @Test
    public void espressoTest2() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.github.grishberg.instrumentaltestwithtestgroupsordering", appContext.getPackageName());
    }

    @EspressoTest
    @Test
    public void espressoTest3() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.github.grishberg.instrumentaltestwithtestgroupsordering", appContext.getPackageName());
    }
}
