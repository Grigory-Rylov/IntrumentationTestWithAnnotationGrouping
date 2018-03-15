package com.github.grishberg.instrumentaltestwithtestgroupsordering.compound;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by grishberg on 15.03.18.
 */
@RunWith(AndroidJUnit4.class)
public class CompoundTest1 {
    @Test
    public void compoundTest1() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.github.grishberg.instrumentaltestwithtestgroupsordering", appContext.getPackageName());
    }

    @Test
    public void compoundTest2() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.github.grishberg.instrumentaltestwithtestgroupsordering", appContext.getPackageName());
    }
}
