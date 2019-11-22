package com.tenmiles.helpstack.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.tenmiles.helpstack.activities.HSActivityParent;
import com.tenmiles.helpstack.logic.HSSource;
import com.tenmiles.helpstack.logic.OnFetchedArraySuccessListener;
import com.tenmiles.helpstack.model.HSError;
import com.tenmiles.helpstack.model.HSKBItem;
import com.tenmiles.helpstack.model.HSTicket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;

public class TaskFragment extends HSFragmentParent {

    static final String TASK_KB_ARTICLES = "task_kb_articles";

    private Activity mActivity;
    private List<String> mTasks;
    private Task mTask;
    private TaskResponse mResponse;
    private TaskCallbacks mCallbacks;
    private boolean mRunning;
    private boolean isFailed;
    private HSSource mGearSource;

    /**
     * Hold a reference to the parent Activity so we can report the task's current
     * progress and results. The Android framework will pass us a reference to the
     * newly created Activity after each configuration change.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (HSActivityParent) context;
    }

    /**
     * This method is called once when the Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null)
            mCallbacks = (TaskCallbacks) getTargetFragment();
    }

    /**
     * Note that this method is <em>not</em> called when the Fragment is being
     * retained across Activity instances. It will, however, be called when its
     * parent Activity is being destroyed for good (such as when the user clicks
     * the back button, etc.).
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTask();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    /**
     * Start the background task.
     */
    void startTask(HSSource gearSource, String[] tasks) {
        if (!mRunning) {
            mGearSource = gearSource;
            mTasks = new ArrayList<>(Arrays.asList(tasks));
            mTask = new Task();
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tasks);
            mResponse = new TaskResponse();
            mRunning = true;
        }
    }

    /**
     * Cancel the background task.
     */
    private void cancelTask() {
        if (mRunning) {
            mGearSource = null;
            mTask.cancel(false);
            mTask = null;
            mRunning = false;
        }
    }

    /**
     * Returns the current state of the background task.
     */
    boolean isRunning() {
        return mRunning;
    }

    private void fetchKbArticles(final Task task) {
        mGearSource.requestKBArticle(new OnFetchedArraySuccessListener() {
            @Override
            public void onSuccess(Object[] kbArticles) {
                mResponse.kbArticles = (HSKBItem[]) kbArticles;
                mTasks.remove(TASK_KB_ARTICLES);
                task.onPostExecute(mResponse);
            }
        });
    }

    /**
     * Callback interface through which the fragment can report the task's
     * progress and results back to the Activity.
     */
    interface TaskCallbacks {
        void onPreExecute();

        void onProgressUpdate(int percent);

        void onCancelled();

        void onPostExecute(Object object);
    }

    private class Task extends AsyncTask<String, Integer, Object> {
        @Override
        protected void onPreExecute() {
            // Proxy the call to the Activity.
            if (mCallbacks != null)
                mCallbacks.onPreExecute();
            mRunning = true;
        }

        @Override
        protected Object doInBackground(String... params) {
            for (String param : params) {
                Log.d("Loading task", param);
                if (param.equals(TASK_KB_ARTICLES)) {
                    fetchKbArticles(this);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
            // Proxy the call to the Activity.
            if (mCallbacks != null)
                mCallbacks.onProgressUpdate(percent[0]);
        }

        @Override
        protected void onCancelled() {
            // Proxy the call to the Activity.
            if (mCallbacks != null)
                mCallbacks.onCancelled();
            mRunning = false;
        }

        @Override
        protected void onPostExecute(Object object) {
            // Proxy the call to the Activity.
            if(object != null) {
                if (object instanceof HSError && !isFailed) {
                    mRunning = false;
                    isFailed = true;
                    if (mCallbacks != null)
                        mCallbacks.onPostExecute(object);
                    cancelTask();
                }

                if (mTasks.size() == 0 && !isFailed) {
                    mRunning = false;
                    if (mCallbacks != null)
                        mCallbacks.onPostExecute(object);
                    cancelTask();
                }
            }
        }
    }

    static class TaskResponse {
        HSKBItem[] kbArticles;
        HSTicket[] tickets;
    }
}
