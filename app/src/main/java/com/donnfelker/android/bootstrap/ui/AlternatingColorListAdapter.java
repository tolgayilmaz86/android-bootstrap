
package com.donnfelker.android.bootstrap.ui;

import android.view.LayoutInflater;

import com.donnfelker.android.bootstrap.R;
import com.donnfelker.android.bootstrap.R.drawable;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;

import java.util.List;

/**
 * List adapter that colors rows in alternating colors
 *
 * @param <V>
 */
public abstract class AlternatingColorListAdapter<V> extends
        SingleTypeAdapter<V> {

    private final int mPrimaryResource;

    private final int mSecondaryResource;

    /**
     * Create adapter with alternating row colors
     *
     * @param layoutId
     * @param inflater
     * @param items
     */
    public AlternatingColorListAdapter(final int layoutId, final LayoutInflater inflater,
                                       final List<V> items) {
        this(layoutId, inflater, items, true);
    }

    /**
     * Create adapter with alternating row colors
     *
     * @param layoutId
     * @param inflater
     * @param items
     * @param selectable
     */
    public AlternatingColorListAdapter(final int layoutId,
            final LayoutInflater inflater, final List<V> items, final boolean selectable) {
        super(inflater, layoutId);

        if (selectable) {
            mPrimaryResource = drawable.table_background_selector;
            mSecondaryResource = drawable.table_background_alternate_selector;
        } else {
            mPrimaryResource = R.color.pager_background;
            mSecondaryResource = R.color.pager_background_alternate;
        }

        setItems(items);
    }

    @Override
    protected void update(final int position, final V item) {
        if (position % 2 != 0)
            updater.view.setBackgroundResource(mPrimaryResource);
        else
            updater.view.setBackgroundResource(mSecondaryResource);
    }
}
