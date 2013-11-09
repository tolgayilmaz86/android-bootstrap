

package com.donnfelker.android.bootstrap.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.PaintDrawable;

/**
 * Drawable bar graph
 */
public class BarGraphDrawable extends PaintDrawable {

    private static final int MIN_HEIGHT = 4;

    private static final int SPACING_X = 1;

    private final int[][] mColors;

    private final long[][] mData;

    private long max = 1;

    /**
     * Create drawable bar graph for mData and mColors
     *
     * @param data
     * @param colors
     */
    public BarGraphDrawable(final long[][] data, final int[][] colors) {
        super(android.R.color.transparent);
        mData = data;
        mColors = colors;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                max = Math.max(max, data[i][j]);
            }
        }
    }

    @Override
    public void draw(final Canvas canvas) {
        final Paint paint = getPaint();
        final Rect bounds = getBounds();
        final float width = ((float) bounds.width() / mData.length) - SPACING_X;
        final int height = bounds.height();
        float x = 0;
        for (int i = 0; i < mData.length; i++) {
            for (int j = 0; j < mData[i].length; j++) {
                paint.setColor(mColors[i][j]);
                float percentage = (float) mData[i][j] / max;
                canvas.drawRect(x, height - Math.max(MIN_HEIGHT, percentage * height), x + width, bounds.bottom, paint);
            }
            x += width + SPACING_X;
        }
    }
}
