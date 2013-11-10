package com.donnfelker.android.bootstrap.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.donnfelker.android.bootstrap.R;
import com.donnfelker.android.bootstrap.core.User;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;

import static com.donnfelker.android.bootstrap.core.Constants.Extra.USER;

public class UserActivity extends BootstrapActivity {

    @InjectView(R.id.iv_avatar) protected ImageView mAvatar;
    @InjectView(R.id.tv_name) protected TextView mName;

    private User mUser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_view);

        if (getIntent() != null && getIntent().getExtras() != null) {
            mUser = (User) getIntent().getExtras().getSerializable(USER);
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Picasso.with(this).load(mUser.getAvatarUrl())
                .placeholder(R.drawable.gravatar_icon)
                .into(mAvatar);

        mName.setText(String.format("%s %s", mUser.getFirstName(), mUser.getLastName()));

    }


}
