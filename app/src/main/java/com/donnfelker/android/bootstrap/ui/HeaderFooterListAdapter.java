
package com.donnfelker.android.bootstrap.ui;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.ListView.FixedViewInfo;

import java.util.ArrayList;

/**
 * Utility adapter that supports adding headers and footers
 *
 * @param <E>
 */
public class HeaderFooterListAdapter<E extends BaseAdapter> extends
        HeaderViewListAdapter {

    private final ListView mList;

    private final ArrayList<FixedViewInfo> mHeaders;

    private final ArrayList<FixedViewInfo> mFooters;

    private final E mWrapped;

    /**
     * Create header footer adapter
     *
     * @param view
     * @param adapter
     */
    public HeaderFooterListAdapter(final ListView view, final E adapter) {
        this(new ArrayList<FixedViewInfo>(), new ArrayList<FixedViewInfo>(),
                view, adapter);
    }

    private HeaderFooterListAdapter(final ArrayList<FixedViewInfo> headerViewInfos,
                                    final ArrayList<FixedViewInfo> footerViewInfos, final ListView view, final E adapter) {
        super(headerViewInfos, footerViewInfos, adapter);

        mHeaders = headerViewInfos;
        mFooters = footerViewInfos;
        mList = view;
        mWrapped = adapter;
    }

    /**
     * Add non-selectable header view with no data
     *
     * @param view
     * @return this adapter
     * @see #addHeader(View, Object, boolean)
     */
    public HeaderFooterListAdapter<E> addHeader(final View view) {
        return addHeader(view, null, false);
    }

    /**
     * Add header
     *
     * @param view
     * @param data
     * @param isSelectable
     * @return this adapter
     */
    public HeaderFooterListAdapter<E> addHeader(final View view, final Object data,
                                                final boolean isSelectable) {
        final FixedViewInfo info = mList.new FixedViewInfo();
        info.view = view;
        info.data = data;
        info.isSelectable = isSelectable;

        mHeaders.add(info);
        mWrapped.notifyDataSetChanged();
        return this;
    }

    /**
     * Add non-selectable footer view with no data
     *
     * @param view
     * @return this adapter
     * @see #addFooter(View, Object, boolean)
     */
    public HeaderFooterListAdapter<E> addFooter(final View view) {
        return addFooter(view, null, false);
    }

    /**
     * Add footer
     *
     * @param view
     * @param data
     * @param isSelectable
     * @return this adapter
     */
    public HeaderFooterListAdapter<E> addFooter(final View view, final Object data,
                                                final boolean isSelectable) {
        final FixedViewInfo info = mList.new FixedViewInfo();
        info.view = view;
        info.data = data;
        info.isSelectable = isSelectable;

        mFooters.add(info);
        mWrapped.notifyDataSetChanged();
        return this;
    }

    @Override
    public boolean removeHeader(final View v) {
        final boolean removed = super.removeHeader(v);
        if (removed) {
            mWrapped.notifyDataSetChanged();
        }
        return removed;
    }

    /**
     * Remove all headers
     *
     * @return true if headers were removed, false otherwise
     */
    public boolean clearHeaders() {
        boolean removed = false;
        if (!mHeaders.isEmpty()) {
            final FixedViewInfo[] infos = mHeaders.toArray(new FixedViewInfo[mHeaders
                    .size()]);
            for (final FixedViewInfo info : infos) {
                removed = super.removeHeader(info.view) || removed;
            }
        }
        if (removed) {
            mWrapped.notifyDataSetChanged();
        }
        return removed;
    }

    /**
     * Remove all footers
     *
     * @return true if headers were removed, false otherwise
     */
    public boolean clearFooters() {
        boolean removed = false;
        if (!mFooters.isEmpty()) {
            final FixedViewInfo[] infos = mFooters.toArray(new FixedViewInfo[mFooters
                    .size()]);
            for (final FixedViewInfo info : infos) {
                removed = super.removeFooter(info.view) || removed;
            }
        }
        if (removed) {
            mWrapped.notifyDataSetChanged();
        }
        return removed;
    }

    @Override
    public boolean removeFooter(final View v) {
        final boolean removed = super.removeFooter(v);
        if (removed) {
            mWrapped.notifyDataSetChanged();
        }
        return removed;
    }

    @Override
    public E getWrappedAdapter() {
        return mWrapped;
    }

    @Override
    public boolean isEmpty() {
        return mWrapped.isEmpty();
    }
}
