package com.android.cevic.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.android.cevic.R;
import com.android.cevic.repository.Repository;
import com.android.cevic.repository.RepositoryImpl;
import com.android.cevic.repository.SocketManager;
import com.android.cevic.viewmodel.SlideViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    public static Socket socket;
    private SocketManager socketManager;
    private SharedPreferences sharedPreferences;
    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private SlideViewModel slideViewModel;


    public void ShowStatusBar() {

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );


    }

    public void HideStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void observeQuizStatusLiveData() {
        slideViewModel
                .getQuizStatusLiveData()
                .observe(this, new Observer<Repository.QuizStatus>() {
                    @Override
                    public void onChanged(Repository.QuizStatus quizStatus) {

                        switch (quizStatus) {
                            case AVAILABLE:
                                bottomNavigationView.showBadge(R.id.quizFragment);
                                break;
                            case NOTAVALABLE:
                            case SEEN:
                                bottomNavigationView.removeBadge(R.id.quizFragment);
                                break;


                        }
                    }
                });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {

            if (bottomNavigationView.getSelectedItemId() == R.id.slideFragment) {
                HideStatusBar();
            } else {
                ShowStatusBar();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        socketManager = SocketManager.getINSTANCE();
        sharedPreferences = RepositoryImpl.create(getApplication()).getSharedPref();

        //set up bottom navigation with nav controller
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        //get reference to slideViewModel;
        slideViewModel = ViewModelProviders.of(this).get(SlideViewModel.class);
        observeQuizStatusLiveData();


    }


}
