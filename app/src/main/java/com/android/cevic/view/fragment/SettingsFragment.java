package com.android.cevic.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.cevic.R;
import com.android.cevic.repository.RepositoryImpl;
import com.android.cevic.view.MainActivity;

import org.jetbrains.annotations.NotNull;


public class SettingsFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout layoutIpAddress, layoutUsername, layoutId;
    private TextView tvIpAdress, tvName, tvId;


    private SharedPreferences sharedPreferences;

    private static final String TAG = "FragmentMore";

    public SettingsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) requireActivity()).ShowStatusBar();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

        sharedPreferences = RepositoryImpl.create(requireActivity().getApplication()).getSharedPref();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvIpAdress = view.findViewById(R.id.tv_ip_address);
        tvName = view.findViewById(R.id.tv_username);
        tvId = view.findViewById(R.id.tv_id);
        layoutIpAddress = view.findViewById(R.id.layout_address);
        layoutUsername = view.findViewById(R.id.layout_username);
        layoutId = view.findViewById(R.id.layout_id);


        layoutIpAddress.setOnClickListener(this);
        layoutUsername.setOnClickListener(this);
        layoutId.setOnClickListener(this);


        tvIpAdress.setText(sharedPreferences.getString("ip", "192.168.XXX.XXX"));
        tvName.setText(sharedPreferences.getString("name", "anonymous"));
        tvId.setText(sharedPreferences.getString("id", "Student ID"));


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.layout_address:
                updateIpAddress();
                break;
            case R.id.layout_username:
                updateName();
                break;
            case R.id.layout_id:
                updateId();
                break;
        }

    }

    private void updateId() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        final EditText editText = new EditText(getContext());


        LinearLayout container = new LinearLayout(requireActivity());
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(40, 0, 40, 0);
        editText.setLayoutParams(lp);
        editText.setGravity(android.view.Gravity.TOP | android.view.Gravity.LEFT);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setLines(1);
        editText.setMaxLines(1);
        editText.setText(sharedPreferences.getString("id", "Student ID"));


        container.addView(editText, lp);


        builder.setView(container);
        builder.setTitle("Student ID");
        builder.setIcon(R.drawable.ic_person_outline);
        builder.setPositiveButton("Update ID", new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sharedPreferences.edit();


                String id = editText.getText().toString().trim();

                editor.putString("id", id); // save to db
                editor.apply();


                tvId.setText(id); //set to  text view

                dialog.dismiss();

            }
        });
        builder.show();
    }

    private void updateIpAddress() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final EditText editText = new EditText(getContext());


        LinearLayout container = new LinearLayout(requireActivity());
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(40, 0, 40, 0);
        editText.setLayoutParams(lp);
        editText.setGravity(android.view.Gravity.TOP | android.view.Gravity.LEFT);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setLines(1);
        editText.setMaxLines(1);
        editText.setText(sharedPreferences.getString("ip", "192.168.XXX.XXX"));


        container.addView(editText, lp);


        builder.setView(container);
        builder.setTitle("Server IP");
        builder.setIcon(R.drawable.ic_ip);
        builder.setPositiveButton("Update IP", new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sharedPreferences.edit();


                String ip = editText.getText().toString().trim();

                editor.putString("ip", ip); // save to db
                editor.apply();


                tvIpAdress.setText(ip); //set to  text view

                dialog.dismiss();

            }
        });
        builder.show();
    }


    private void updateName() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final EditText editText = new EditText(getContext());


        LinearLayout container = new LinearLayout(requireActivity());
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(40, 0, 40, 0);
        editText.setLayoutParams(lp);
        editText.setGravity(android.view.Gravity.TOP | android.view.Gravity.LEFT);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setLines(1);
        editText.setMaxLines(1);
        editText.setText(sharedPreferences.getString("name", "anonymous"));


        container.addView(editText, lp);


        builder.setView(container);
        builder.setTitle("Student Name");
        builder.setIcon(R.drawable.ic_person_outline);
        builder.setPositiveButton("Update name", new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sharedPreferences.edit();


                String name = editText.getText().toString().trim();

                editor.putString("name", name); // save to db
                editor.apply();


                tvName.setText(name); //set to  text view

                dialog.dismiss();

            }
        });
        builder.show();
    }


}
