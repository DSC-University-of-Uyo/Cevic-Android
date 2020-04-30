package com.android.cevic.view.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.cevic.R;
import com.android.cevic.repository.Repository.SlideStatus;
import com.android.cevic.repository.SocketManager;
import com.android.cevic.view.MainActivity;
import com.android.cevic.viewmodel.SlideViewModel;
import com.otaliastudios.zoom.ZoomImageView;

import java.nio.ByteBuffer;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.disposables.Disposable;

public class SlideFragment extends Fragment {


    private ZoomImageView imageViewScreenDisplay;
//    ZoomageView imageViewScreenDisplay;

    private String ip, name, id;

    private SharedPreferences preferences;

    private ProgressBar progressBarConnect;
    private TextView textViewConnectionFailed;

    private SweetAlertDialog sweetAlertDialog;

    private static final String TAG = "SlideFragment";

    private ByteBuffer bb = ByteBuffer.allocate(1024 * 1024);

    private SlideViewModel viewModel;
    private Disposable disposable;

    public SlideFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) requireActivity()).HideStatusBar();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_slide, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        viewModel.getSlideStatus().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<SlideStatus>() {
            @Override
            public void onChanged(SlideStatus slideStatus) {

                if (slideStatus == SlideStatus.LOADING) {
                    Log.d(TAG, "onChanged: loading");
                    progressBarConnect.setVisibility(View.VISIBLE);
                    textViewConnectionFailed.setVisibility(View.INVISIBLE);
                } else if (slideStatus == SlideStatus.STREAMING) {
                    Log.d(TAG, "onChanged: loading streaming");
                    progressBarConnect.setVisibility(View.INVISIBLE);
                    textViewConnectionFailed.setVisibility(View.INVISIBLE);
                } else if (slideStatus == SlideStatus.ERROR) {
                    Log.d(TAG, "onChanged: error");

                    SocketManager.mSocket = null;
                    sweetAlertDialog.show();
                    progressBarConnect.setVisibility(View.INVISIBLE);
//                    textViewConnectionFailed.setVisibility(View.VISIBLE);
//                    textViewConnectionFailed.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_error, 0, 0);
//                    textViewConnectionFailed.setText("an error occurred while connecting to server");
                }
            }
        });


        viewModel.getImageStreamingData().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {

                imageViewScreenDisplay.setImageBitmap(bitmap);

            }
        });

    }

    void setUpDialog() {

        sweetAlertDialog = new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                .setContentText("Connection to host failed!")
                .setConfirmText("Retry")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        viewModel.startStreaming();

                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        setUpDialog();
        Log.d(TAG, "onViewCreated: slide");
        imageViewScreenDisplay = view.findViewById(R.id.image_view_screen_display);
        textViewConnectionFailed = view.findViewById(R.id.tv_connection_failed);
        progressBarConnect = view.findViewById(R.id.pb_connection_to_server);


        ip = preferences.getString("ip", "192.168.XXX.XXX");
        name = preferences.getString("name", "anonymous");
        id = preferences.getString("id", "student ID");


        if (name == "anonymous") {
            textViewConnectionFailed.setVisibility(View.VISIBLE);
            textViewConnectionFailed.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_person_outline, 0, 0);
            textViewConnectionFailed.setText("set up with your name");
            return;
        } else if (id == "Student ID") {
            textViewConnectionFailed.setVisibility(View.VISIBLE);
            textViewConnectionFailed.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_person_outline, 0, 0);
            textViewConnectionFailed.setText("set up with your Id");
            return;
        }

        if (ip.equals("") || ip == null || ip.toLowerCase().contains("x")) {
//
////            Snackbar.make(view, "No Ip Address Set!", Snackbar.LENGTH_SHORT).show();
            Log.i(TAG, "onViewCreated: " + ip);
            textViewConnectionFailed.setVisibility(View.VISIBLE);
            textViewConnectionFailed.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_ip, 0, 0);
            textViewConnectionFailed.setText("Invalid Ip Address");
            return;
        } else {
            Log.i(TAG, "onViewCreated: " + ip);
        }


        progressBarConnect.setVisibility(View.VISIBLE);

        viewModel.startStreaming();


//        viewModel.createObservable().delay(5, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(bitmapObserver());


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: slide");
        viewModel = ViewModelProviders.of(this).get(SlideViewModel.class);

        preferences = viewModel.getAppPref();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");
//        viewModel.clearDisposable();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        disposable.dispose();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.d(TAG, "onDetach: called!");
    }


}
