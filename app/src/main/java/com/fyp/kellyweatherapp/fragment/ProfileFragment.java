package com.fyp.kellyweatherapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.fyp.kellyweatherapp.R;
import com.fyp.kellyweatherapp.database.PrefConfig;
import com.fyp.kellyweatherapp.model.User;


import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private User user;
    private ImageView icon;
    private TextView textName, textEmail;
    private Button saveButton;

    @SuppressLint("ShowToast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        icon = view.findViewById(R.id.profileIcon);
        textName = view.findViewById(R.id.txt_profilename);
        textEmail = view.findViewById(R.id.txt_profileemail);
        saveButton = view.findViewById(R.id.btn_saveProfile);
        saveButton.setOnClickListener(this);
        user = PrefConfig.loadUser(Objects.requireNonNull(getContext()));
        loadProfileUI(user);
        return view;
    }

    private void loadProfileUI(User user) {
        textName.setText(user.getName());
        textEmail.setText(user.getEmail());
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_saveProfile) {

        }
    }
}
