package com.android.cevic.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.cevic.R;
import com.android.cevic.model.Quiz;
import com.android.cevic.repository.Repository.QuizStatus;
import com.android.cevic.repository.SocketManager;
import com.android.cevic.view.MainActivity;
import com.android.cevic.viewmodel.SlideViewModel;

import java.io.DataOutputStream;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class QuizFragment extends Fragment implements View.OnClickListener {

    //----------------------------------------------------------------------------------------------
    //variables
    private int time = 0;
    private Disposable disposable;
    private SlideViewModel viewModel;
    private CountDownTimer countDownTimer;
    private ConstraintLayout constraintLayout;
    private TextView tvQuestion, tvTitle, tvTime;
    private String answer, answerText, selection, selectionText;
    private static final String TAG = "QuizFragment";
    private Button button1, button2, button3, button4;

    private Quiz quiz;

    //----------------------------------------------------------------------------------------------
    //constructor and override methods
    public QuizFragment() {// Required empty public constructor
    }

    @Override
    public void onClick(View view) {

        Log.d(TAG, "onClick: " + view.getId());
        switch (view.getId()) {
            case R.id.button1:
                selection = "A";
                selectionText = quiz.getOptionA();
                break;
            case R.id.button2:
                selection = "B";
                selectionText = quiz.getOptionB();
                break;
            case R.id.button3:
                selection = "c";
                selectionText = quiz.getOptionC();
                break;
            case R.id.button4:
                selection = "D";
                selectionText = quiz.getOptionD();

                break;
        }
        createCompletableObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createCompletableObserver());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = ViewModelProviders.of(this).get(SlideViewModel.class);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) requireActivity()).ShowStatusBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theView = inflater.inflate(R.layout.fragment_quiz, container, false);

        button1 = theView.findViewById(R.id.button1);
        button2 = theView.findViewById(R.id.button2);
        button3 = theView.findViewById(R.id.button3);
        button4 = theView.findViewById(R.id.button4);

        tvQuestion = theView.findViewById(R.id.textViewQuestion);
        tvTitle = theView.findViewById(R.id.textViewTitle);
        tvTime = theView.findViewById(R.id.textViewTime);
        constraintLayout = theView.findViewById(R.id.layout_quiz);

        return theView;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.getQuestionData().observe(getViewLifecycleOwner(), new Observer<Quiz>() {
            @Override
            public void onChanged(Quiz question) {
                Log.d(TAG, "onChanged: called");
                constraintLayout.setVisibility(View.VISIBLE);
                viewModel.setQuizState(QuizStatus.SEEN);

                quiz = question;
                if (question.getAnswer().equals("A")) {
                    answerText = question.getOptionA();
                } else if (question.getAnswer().equals("B")) {
                    answerText = question.getOptionB();

                } else if (question.getAnswer().equals("C")) {
                    answerText = question.getOptionC();

                } else if (question.getAnswer().equals("D")) {
                    answerText = question.getOptionD();

                }

                button1.setText(question.getOptionA());
                button2.setText(question.getOptionB());
                button3.setText(question.getOptionC());
                button4.setText(question.getOptionD());

                tvQuestion.setText(question.getQuestion());
                tvTitle.setText(question.getTitle());
                time = Integer.parseInt(question.getTime()) * 1000;
                answer = question.getAnswer();


                countDownTimer = new CountDownTimer(time, 1000) {


                    public void onTick(long millisUntilFinished) {
                        tvTime.setText("" + millisUntilFinished / 1000);
                    }

                    public void onFinish() {

                        constraintLayout.setVisibility(View.INVISIBLE);
//                        new KAlertDialog(requireActivity(), KAlertDialog.WARNING_TYPE)
//                                .setTitleText("Oops...")
//                                .setContentText("Time Over!").show();

                    }
                }.start();


            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);

    }


    //----------------------------------------------------------------------------------------------
    //helper methods
    private CompletableObserver createCompletableObserver() {

        return new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                constraintLayout.setVisibility(View.INVISIBLE);
                viewModel.setQuizState(QuizStatus.NOTAVALABLE);

                showAlertDialog();

            }

            @Override
            public void onError(Throwable e) {

            }
        };
    }

    private Completable createCompletableObservable() {


        return new Completable() {
            @Override
            protected void subscribeActual(CompletableObserver observer) {

                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(SocketManager.mSocket.getOutputStream());

                    dataOutputStream.writeUTF(selection);
                    dataOutputStream.flush();
                    observer.onComplete();

                } catch (IOException e) {
                    e.printStackTrace();
                    observer.onError(e.getCause());
                }

            }
        };
    }

    private void showAlertDialog() {
//
        String fail = "The correct answer was " + answerText;
        String correct = selectionText + " was correct!";

        Log.d(TAG, "showAlertDialog: " + selection);
        Log.d(TAG, "showAlertDialog: " + quiz.getAnswer());

        SweetAlertDialog kAlertDialog;

        if (selection.toLowerCase().equals(quiz.getAnswer().toLowerCase())) {

            kAlertDialog = new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Good Job!...")
                    .setContentText(correct);
        } else {

            kAlertDialog = new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText(fail);
        }


        kAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog dialog) {

                Log.d(TAG, "onClick: dialog called");
                dialog.dismissWithAnimation();
            }
        });

        kAlertDialog.show();

    }

}
