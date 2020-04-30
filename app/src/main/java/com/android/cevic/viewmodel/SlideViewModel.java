package com.android.cevic.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.android.cevic.model.Quiz;
import com.android.cevic.repository.Repository;
import com.android.cevic.repository.RepositoryImpl;

public class SlideViewModel extends AndroidViewModel {


    //object declarations
    private Repository repository;

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.onClear();
    }

    public void clearDisposable() {
        repository.onClear();
    }

    public void startStreaming() {
        repository.initStreaming();
    }

    public SharedPreferences getAppPref() {
        return repository.getSharedPref();
    }

    public LiveData<Bitmap> getImageStreamingData() {
        return repository.getLiveBitmapData();
    }

    public LiveData<Repository.SlideStatus> getSlideStatus() {
        return repository.getSlideStatus();
    }

    public SlideViewModel(@NonNull Application application) {
        super(application);
        repository = RepositoryImpl.create(application);

    }


    public LiveData<Quiz> getQuestionData() {

        return repository.getLiveQuestionData();
    }

    public LiveData<Repository.QuizStatus> getQuizStatusLiveData() {
        return repository.getQuizStatus();
    }

    public void setQuizState(Repository.QuizStatus state) {
        repository.toggleQuizState(state);
    }
}
