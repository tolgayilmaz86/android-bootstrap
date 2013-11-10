
package com.donnfelker.android.bootstrap.ui;

import android.content.Context;

import com.donnfelker.android.bootstrap.util.Ln;


/**
 * Loader that support throwing an exception when loading in the background
 *
 * @param <D>
 */
public abstract class ThrowableLoader<D> extends AsyncLoader<D> {

    private final D mData;

    private Exception mException;

    /**
     * Create loader for context and seeded with initial data
     *
     * @param context
     * @param data
     */
    public ThrowableLoader(final Context context, final D data) {
        super(context);

        this.mData = data;
    }

    @Override
    public D loadInBackground() {
        mException = null;
        try {
            return loadData();
        } catch (final Exception e) {
            Ln.d(e, "Exception loading data");
            mException = e;
            return mData;
        }
    }

    /**
     * @return exception
     */
    public Exception getException() {
        return mException;
    }

    /**
     * Clear the stored exception and return it
     *
     * @return exception
     */
    public Exception clearException() {
        final Exception throwable = mException;
        mException = null;
        return throwable;
    }

    /**
     * Load data
     *
     * @return data
     * @throws Exception
     */
    public abstract D loadData() throws Exception;
}
