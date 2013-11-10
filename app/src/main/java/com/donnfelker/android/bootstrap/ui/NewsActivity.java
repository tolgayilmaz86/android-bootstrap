package com.donnfelker.android.bootstrap.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.donnfelker.android.bootstrap.R;
import com.donnfelker.android.bootstrap.core.News;

import butterknife.InjectView;

import static com.donnfelker.android.bootstrap.core.Constants.Extra.NEWS_ITEM;

public class NewsActivity extends BootstrapActivity {

    protected News mNewsItem;

    @InjectView(R.id.tv_title) protected TextView mTitle;
    @InjectView(R.id.tv_content) protected TextView mContent;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.news);

        if (getIntent() != null && getIntent().getExtras() != null) {
            mNewsItem = (News) getIntent().getExtras().getSerializable(NEWS_ITEM);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(mNewsItem.getTitle());

        mTitle.setText(mNewsItem.getTitle());
        mContent.setText(mNewsItem.getContent());

    }

}
