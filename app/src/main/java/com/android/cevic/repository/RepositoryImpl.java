package com.android.cevic.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.cevic.model.Quiz;
import com.android.cevic.model.QuizBuilder;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.android.cevic.Const.SHARED_PREF;

public class RepositoryImpl implements Repository {


    private static RepositoryImpl INSTANCE;

    private static final String TAG = "RepositoryImpl";
    private ObjectInputStream inputStream;
    private Application application;
    private Socket clientSocket;


    private MutableLiveData<SlideStatus> statusMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Bitmap> bitmapMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Quiz> questionMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<QuizStatus> quizStatusMutableLiveData = new MutableLiveData<>();


//    CompositeDisposable disposable = new CompositeDisposable();

    Disposable disposable;


    private RepositoryImpl(Application application) {
        this.application = application;
    }

    public static synchronized RepositoryImpl create(Application application) {

        if (INSTANCE == null) {
            INSTANCE = new RepositoryImpl(application);
        }
        return INSTANCE;
    }


    @Override
    public SharedPreferences getSharedPref() {
        return application.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

    }

    @Override
    public void onClear() {
//        disposable.clear();
    }


    @Override
    public LiveData<Bitmap> getLiveBitmapData() {
        return bitmapMutableLiveData;
    }

    @Override
    public LiveData<Quiz> getLiveQuestionData() {
        return questionMutableLiveData;
    }

    @Override
    public void toggleQuizState(QuizStatus quizStatus) {
        quizStatusMutableLiveData.postValue(quizStatus);
    }

    @Override
    public LiveData<SlideStatus> getSlideStatus() {
        return statusMutableLiveData;
    }

    @Override
    public LiveData<QuizStatus> getQuizStatus() {
        return quizStatusMutableLiveData;
    }

    @Override
    public void initStreaming() {

        clientSocket = SocketManager.mSocket;
        if (clientSocket != null && clientSocket.isConnected()) {
            Log.d(TAG, "initStreaming: already connected!");
            return;
        }

        Log.d(TAG, "initStreaming: passed");


        Observable.create(new ObservableOnSubscribe<Bitmap>() {

            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) {

                Log.d(TAG, "onSubscribe: 2");


                try {
                    statusMutableLiveData.postValue(SlideStatus.LOADING);
                    Thread.sleep(500);

                    if (clientSocket == null) {
                        Log.d(TAG, "subscribe: socket is null");
                        clientSocket = new Socket(getSharedPref().getString("ip", "xxx"), 8300);
                        SocketManager.getINSTANCE().setSocket(clientSocket);
                    }

                    DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());

                    String name = getSharedPref().getString("name", "anonymous");
                    String id = getSharedPref().getString("id", null);
                    String details = name + "#" + id;

                    toServer.writeUTF(details);
                    toServer.flush();

                    statusMutableLiveData.postValue(SlideStatus.STREAMING);

                    while (!emitter.isDisposed()) {

                        inputStream = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));

                        Object o = inputStream.readObject();
                        Log.d(TAG, "subscribe: instance of " + o.getClass());

                        if (o instanceof byte[]) {
                            Log.d(TAG, "subscribe: instance of byte");

                            byte[] buffer = (byte[]) o;

                            if (buffer == null || buffer.length < 5) {
                                return;
                            }

                            if (!emitter.isDisposed()) {


                                emitter.onNext(BitmapFactory
                                        .decodeByteArray(buffer, 0, buffer.length));
                            }
                        } else {

                            Map<String, String> stringMap = (HashMap<String, String>) o;


                            Quiz question
                                    = new QuizBuilder()
                                    .setQuestion(stringMap.get("question"))
                                    .setAnswer(stringMap.get("answer"))
                                    .setOptionA(stringMap.get("optionA"))
                                    .setOptionB(stringMap.get("optionB"))
                                    .setOptionC(stringMap.get("optionC"))
                                    .setOptionD(stringMap.get("optionD"))
                                    .setTime(stringMap.get("time"))
                                    .setTitle(stringMap.get("title"))
                                    .createQuiz();


                            questionMutableLiveData.postValue(question);
                            quizStatusMutableLiveData.postValue(QuizStatus.AVAILABLE);

                        }

//
                    }
                } catch (IOException | ClassNotFoundException e) {
                    statusMutableLiveData.postValue(SlideStatus.ERROR);
                    emitter.onError(e);
                    return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                emitter.onComplete();
                statusMutableLiveData.postValue(SlideStatus.COMPLETE);


            }
        }).subscribeOn(Schedulers.io())
                .distinctUntilChanged()
                .map(new Function<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap apply(Bitmap bitmap) {
                        return rotateImage(bitmap);
                    }
                }).subscribe(bitmapObserver());

    }


    Observer<Bitmap> bitmapObserver() {

        return new Observer<Bitmap>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
//                Log.d(TAG, "onSubscribe: 1");
//
//                if(disposable.size() >0){
//                    Log.d(TAG, "onSubscribe: > 0");
//                    disposable.clear();
//                    Log.d(TAG, "onSubscribe: disposable cleared!");
//                }
//
//                Log.d(TAG, "onSubscribe: size "+ disposable.size());
//                disposable.add(d);
//                Log.d(TAG, "onSubscribe: size "+ disposable.size());

            }

            @Override
            public void onNext(Bitmap bitmap) {
                bitmapMutableLiveData.postValue(bitmap);
            }

            @Override
            public void onError(Throwable e) {
                onClear();
                Log.d(TAG, "onError: " + e.getCause());
                Log.d(TAG, "onError: " + e.getMessage());
                Log.d(TAG, "onError: " + e.getLocalizedMessage());
                Log.d(TAG, "onError: " + e.toString());


            }

            @Override
            public void onComplete() {

            }
        };

    }


    private Bitmap rotateImage(Bitmap bitmap) {

        Matrix matrix = new Matrix();
        matrix.postRotate(-270);
        Bitmap rotatedImage = null;
        try {
            rotatedImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
        }

        return rotatedImage;


    }

}
