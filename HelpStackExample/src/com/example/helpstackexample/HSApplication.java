package com.example.helpstackexample;

import android.app.Application;

import com.tenmiles.helpstack.HSHelpStack;
import com.tenmiles.helpstack.gears.HSEmailGear;

public class HSApplication extends Application {

    public static HSHelpStack helpStack;

    @Override
    public void onCreate() {
        super.onCreate();

        helpStack = HSHelpStack.getInstance(this);
        helpStack.setOptions("foo@bar.com", R.xml.articles);

    }

}
