package org.unsane.spirit.tasks;

import android.os.AsyncTask;

/** Without this it's not possbile to use AsyncTask in Scala
   *
   * @author Sebastian Stallenberger
   */

public abstract class AsyncTaskAdapter<T1, T2, T3> extends AsyncTask<T1, T2, T3> {
    protected T3 doInBackground(T1... param) {
        return doInBackground(param[0]);
    }

    abstract protected T3 doInBackground(T1 param);
}
