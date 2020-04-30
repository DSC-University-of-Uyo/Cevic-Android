package com.android.cevic.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.cevic.R;
import com.android.cevic.repository.RepositoryImpl;
import com.android.cevic.repository.SocketManager;
import com.android.cevic.view.MainActivity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class InquiryFragment extends Fragment {

    private String question;
    private static final String TAG = "InquiryFragment";
    private Disposable disposable;

    private SharedPreferences sharedPreferences;

    public InquiryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferences = RepositoryImpl.create(requireActivity().getApplication()).getSharedPref();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) requireActivity()).ShowStatusBar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inquiry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final EditText editText = view.findViewById(R.id.editText);

        Button button = view.findViewById(R.id.button_inquiry);
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                .setContentText("You are not Connected to a host!");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SocketManager.mSocket == null && !SocketManager.mSocket.isConnected()) {
                    sweetAlertDialog.show();
                    return;
                }

                question = editText.getText().toString().trim();

                if (question.length() > 50) {

                    Toast.makeText(getContext(),
                            "limit of 50 exceeded!",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (question.length() < 10) {

                    Toast.makeText(getContext(),
                            "message length too short!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                completableTask()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(completableObserver(editText));
            }
        });


    }

    //----------------------------------------------------------------------------------------------
    //helper methods

    private CompletableObserver completableObserver(final EditText editText) {
        return new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                editText.setText("");

                Toast.makeText(getContext(),
                        "message sent!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {

            }
        };
    }


    private Completable completableTask() {


        return new Completable() {
            @Override
            protected void subscribeActual(CompletableObserver observer) {

                String msg = question + "#" + sharedPreferences.getString("name", "anonymous");
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(SocketManager.mSocket.getOutputStream());
                    dataOutputStream.writeUTF(msg);
                    dataOutputStream.flush();
                    observer.onComplete();
                } catch (IOException e) {
                    e.printStackTrace();
//                    observer.onError(e.getCause());
                }

            }
        };
    }


}
