//  HSSource
//
//Copyright (c) 2014 HelpStack (http://helpstack.io)
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package com.tenmiles.helpstack.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.tenmiles.helpstack.HSHelpStack;
import com.tenmiles.helpstack.activities.HSActivityManager;
import com.tenmiles.helpstack.fragments.HSFragmentParent;
import com.tenmiles.helpstack.model.HSAttachment;
import com.tenmiles.helpstack.model.HSCachedTicket;
import com.tenmiles.helpstack.model.HSCachedUser;
import com.tenmiles.helpstack.model.HSDraft;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSUploadAttachment;
import com.tenmiles.helpstack.model.HSUser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class HSSource {

    private static final String HELPSTACK_DIRECTORY = "helpstack";
    private static final String HELPSTACK_TICKETS_FILE_NAME = "tickets";
    private static final String HELPSTACK_TICKETS_USER_DATA = "user_credential";
    private static final String HELPSTACK_DRAFT = "draft";

    private HSGear gear;
    private Context mContext;
    private HSCachedTicket cachedTicket;
    private HSCachedUser cachedUser;
    private HSDraft draftObject;

    public HSSource(Context context) {
        this.mContext = context;
        setGear(HSHelpStack.getInstance(context).getGear());
        refreshFieldsFromCache();
    }

    private static String getDeviceInformation(Context activity) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n\n\n");
        builder.append("========");
        builder.append("\nDevice brand: ");
        builder.append(Build.MODEL);
        builder.append("\nAndroid version: ");
        builder.append(Build.VERSION.SDK_INT);
        builder.append("\nApp package: ");
        try {
            builder.append(activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).packageName);
        } catch (NameNotFoundException e) {
            builder.append("NA");
        }
        builder.append("\nApp version: ");
        try {
            builder.append(activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode);
        } catch (NameNotFoundException e) {
            builder.append("NA");
        }

        return builder.toString();
    }

    public void requestKBArticle(OnFetchedArraySuccessListener success) {
        try {
            HSArticleReader reader = new HSArticleReader(gear.getLocalArticleResourceId());
            success.onSuccess(reader.readArticlesFromResource(mContext));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setGear(HSGear gear) {
        this.gear = gear;
    }

    public HSUser getUser() {
        return cachedUser.getUser();
    }

    public String getSupportEmailAddress() {
        return gear.getCompanySupportEmailAddress();
    }

    /***
     * Depending on the setting set on gear, it launches new ticket activity.
     * <p>
     * if email : launches email [Done]
     * else:
     * if user logged in : launches user details [Done]
     * else: launches new ticket [Done]
     *
     * @param fragment
     * @param requestCode
     */
    public void launchCreateNewTicketScreen(HSFragmentParent fragment, int requestCode) {
        launchEmailAppWithEmailAddress(fragment.getActivity());
    }

    /////////////////////////////////////////////////
    ////////     Utility Functions  /////////////////
    /////////////////////////////////////////////////

    public void launchEmailAppWithEmailAddress(Activity activity) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", getSupportEmailAddress(), null));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getSupportEmailAddress()});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(Intent.EXTRA_TEXT, getDeviceInformation(activity));

        activity.startActivity(Intent.createChooser(emailIntent, "Email"));
    }

    public void refreshFieldsFromCache() {
        // read the ticket data from cache and maintain here
        doReadTicketsFromCache();
        doReadUserFromCache();
        doReadDraftFromCache();
    }

    /**
     * Opens a file and read its content. Return null if any error occured or file not found
     *
     * @param file
     * @return
     */
    private String readJsonFromFile(File file) {

        if (!file.exists()) {
            return null;
        }

        String json = null;
        FileInputStream inputStream;

        try {
            StringBuilder datax = new StringBuilder();
            inputStream = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(isr);

            String readString = bufferReader.readLine();
            while (readString != null) {
                datax.append(readString);
                readString = bufferReader.readLine();
            }

            isr.close();

            json = datax.toString();
            return json;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeJsonIntoFile(File file, String json) {
        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doReadTicketsFromCache() {
        File ticketFile = new File(getProjectDirectory(), HELPSTACK_TICKETS_FILE_NAME);
        String json = readJsonFromFile(ticketFile);

        if (json == null) {
            cachedTicket = new HSCachedTicket();
        } else {
            Gson gson = new Gson();
            cachedTicket = gson.fromJson(json, HSCachedTicket.class);
        }
    }

    protected void doReadUserFromCache() {
        File userFile = new File(getProjectDirectory(), HELPSTACK_TICKETS_USER_DATA);
        String json = readJsonFromFile(userFile);

        if (json == null) {
            cachedUser = new HSCachedUser();
        } else {
            Gson gson = new Gson();
            cachedUser = gson.fromJson(json, HSCachedUser.class);
        }
    }

    protected void doReadDraftFromCache() {
        File draftFile = new File(getProjectDirectory(), HELPSTACK_DRAFT);
        String json = readJsonFromFile(draftFile);

        if (json == null) {
            draftObject = new HSDraft();
        } else {
            Gson gson = new Gson();
            draftObject = gson.fromJson(json, HSDraft.class);
        }
    }

    private void writeDraftIntoFile() {
        Gson gson = new Gson();
        String draftJson = gson.toJson(draftObject);
        File draftFile = new File(getProjectDirectory(), HELPSTACK_DRAFT);

        writeJsonIntoFile(draftFile, draftJson);
    }

    protected File getProjectDirectory() {
        File projDir = new File(mContext.getFilesDir(), HELPSTACK_DIRECTORY);
        if (!projDir.exists())
            projDir.mkdirs();

        return projDir;
    }
}
