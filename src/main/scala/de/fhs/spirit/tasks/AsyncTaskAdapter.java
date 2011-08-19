package de.fhs.spirit.tasks;

import android.os.AsyncTask;

/**
 * Author: Illaz
 * Date: 13.07.11
 * Time: 15:49
 */

public abstract class AsyncTaskAdapter<T1, T2, T3> extends AsyncTask<T1, T2, T3> {
    protected T3 doInBackground(T1... param) {
        return doInBackground(param[0]);
    }

    abstract protected T3 doInBackground(T1 param);
}
