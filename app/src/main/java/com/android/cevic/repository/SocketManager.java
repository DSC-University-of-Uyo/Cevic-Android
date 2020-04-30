package com.android.cevic.repository;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketManager {

    private static final String TAG = "SocketManager";
    private static SocketManager INSTANCE = null;
    public static Socket mSocket = null;
    public static DataOutputStream dataOutputStream;

    private static int count = 0;

    private SocketManager() {

    }

    public static synchronized SocketManager getINSTANCE() {

        if (INSTANCE == null) {
            count++;
            INSTANCE = new SocketManager();
        }

        Log.d(TAG, "getINSTANCE: called" + count);

        return INSTANCE;

    }

    public Socket getClientSocket() {
        return mSocket;
    }

    public void setSocket(Socket socket) throws IOException {

//        if (mSocket == null) {
            mSocket = socket;
//            dataOutputStream = new DataOutputStream(socket.getOutputStream());

//        }
    }


}
