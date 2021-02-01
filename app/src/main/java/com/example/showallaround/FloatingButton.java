package com.example.showallaround;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class FloatingButton extends Fragment {

    public FloatingButton() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_floating_button,
                container, false);
        ImageView imageView = view.findViewById(R.id.imageViewFloatingButton);
        imageView.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "This is my Toast message!",
                Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
//            intent.putExtra("hahstag",listOfHashtags.get(position).getName());

            startActivity(intent);
        });
        return view;
    }
}