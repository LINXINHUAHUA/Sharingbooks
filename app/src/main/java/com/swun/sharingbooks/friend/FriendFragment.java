package com.swun.sharingbooks.friend;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.swun.sharingbooks.R;

import io.rong.imkit.RongIM;

/**
 * Created by baolei on 2017/5/15.
 */

public class FriendFragment extends Fragment {

    private SharedPreferences preferences;
    private Button button1;
    private Button button2;
    private String t = "dWCjdw9yC6uArspuo+RRZFiol+/uF+TVBqE8l3rGujek6pRMO4bq+pf1n8Amq4oftUU8zwijXB6OyUbgqEkFr3b9xMHa7tgH";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        button1 = (Button) view.findViewById(R.id.btn_1);
        button2 = (Button) view.findViewById(R.id.btn_2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RongIM.getInstance().startPrivateChat(getActivity(), "201531003043","201431003120");
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RongIM.getInstance().startPrivateChat(getActivity(), "201431003120", "201531003043");
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String token = preferences.getString("token", "");
        if (token.equals(t)) {
            button1.setEnabled(false);
        } else {
            button2.setEnabled(false);
        }
        return view;
    }
}
