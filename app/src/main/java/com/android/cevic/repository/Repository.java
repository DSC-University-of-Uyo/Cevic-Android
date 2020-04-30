package com.android.cevic.repository;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

import com.android.cevic.model.Quiz;

public interface Repository {

    LiveData<Bitmap> getLiveBitmapData();

    LiveData<Quiz> getLiveQuestionData();

    void toggleQuizState(QuizStatus done);

    enum SlideStatus {
        LOADING,
        STREAMING,
        COMPLETE,
        ERROR
    }


    enum QuizStatus {
        DONE,
        TAKEN,
        SEEN,
        AVAILABLE,
        NOTAVALABLE,
        ERROR
    }

    LiveData<SlideStatus> getSlideStatus();

    LiveData<QuizStatus> getQuizStatus();


    void initStreaming();

    SharedPreferences getSharedPref();

    void onClear();


}
