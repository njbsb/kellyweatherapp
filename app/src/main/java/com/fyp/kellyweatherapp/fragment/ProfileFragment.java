package com.fyp.kellyweatherapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.fyp.kellyweatherapp.R;
import com.fyp.kellyweatherapp.database.PrefConfig;
import com.fyp.kellyweatherapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private User user;
    private ImageView icon;
    private TextView textName, textEmail;

    @SuppressLint("ShowToast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        icon = view.findViewById(R.id.profileIcon);
        textName = view.findViewById(R.id.txt_profilename);
        textEmail = view.findViewById(R.id.txt_profileemail);

        user = PrefConfig.loadUser(Objects.requireNonNull(getContext()));
        loadProfileUI(user);
        return view;
    }

    private void loadProfileUI(User user) {
        textName.setText(user.getName());
        textEmail.setText(user.getEmail());
    }


}
