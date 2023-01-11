package com.example.uberapp_tim3.fragments.passenger;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.uberapp_tim3.R;
import com.example.uberapp_tim3.model.DTO.PassengerDTO;
import com.example.uberapp_tim3.model.mockup.Passenger;
import com.example.uberapp_tim3.services.ServiceUtils;
import com.example.uberapp_tim3.tools.PassengerMockup;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PassengerEditInfoFragment extends Fragment {

    private Button btnChangeAvatar;
    private SharedPreferences sharedPreferences;
    private CircleImageView imgAvatar;
    public PassengerEditInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        imgAvatar = getActivity().findViewById(R.id.imgEditPassengerAvatar);
        btnChangeAvatar = getActivity().findViewById(R.id.btnChangePassengerAvatar);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            btnChangeAvatar.setEnabled(false);
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setTitle(R.string.edit_info);
        this.getPassenger(this.sharedPreferences.getLong("pref_id", 0));
    }

    public void getPassenger(Long id){

        Call<PassengerDTO> call = ServiceUtils.passengerService.getPassenger(id);
        call.enqueue(new Callback<PassengerDTO>() {
            @Override
            public void onResponse(Call<PassengerDTO> call, Response<PassengerDTO> response) {
                if(!response.isSuccessful()) return;
                PassengerDTO passenger = response.body();

                TextView tvName = getActivity().findViewById(R.id.txtPassengerEditFirstName);
                tvName.setText(passenger.getName());

                TextView tvSurname = getActivity().findViewById(R.id.txtPassengerEditLastName);
                tvSurname.setText(passenger.getSurname());

                TextView tvPhoneNumber = getActivity().findViewById(R.id.txtPassengerEditPhoneNumber);
                tvPhoneNumber.setText(passenger.getTelephoneNumber());

                TextView emailAddress = getActivity().findViewById(R.id.txtPassengerEmailEdit);
                emailAddress.setText(passenger.getEmail());

                TextView homeAddress = getActivity().findViewById(R.id.txtHomeAddressEdit);
                homeAddress.setText(passenger.getAddress());

                if(!passenger.getProfilePicture().contains(",")){return;}

                String base64Image = passenger.getProfilePicture().split(",")[1];
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                CircleImageView cv = getActivity().findViewById(R.id.imgEditPassengerAvatar);
                cv.setImageBitmap(decodedByte);
            }

            @Override
            public void onFailure(Call<PassengerDTO> call, Throwable t) {
                Log.d("FAIIIL", t.getMessage());
                Log.d("FAIIIL", "BLATRUC");
            }
        });

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_passenger_edit_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        fillViews();
        setOnClickListeners();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setOnClickListeners() {
        btnChangeAvatar = getActivity().findViewById(R.id.btnChangePassengerAvatar);
        btnChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ((Button) getActivity().findViewById(R.id.btnCancelEditPassenger)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        ((Button) getActivity().findViewById(R.id.btnAcceptEditPassenger)).setOnClickListener(null);

    }



    private void fillViews() {

        Passenger passenger = PassengerMockup.getPassengers().get(0);

        ((EditText) getActivity().findViewById(R.id.txtPassengerEditFirstName)).setText(passenger.getName());
        ((EditText) getActivity().findViewById(R.id.txtPassengerEditLastName)).setText(passenger.getLastName());
        ((EditText) getActivity().findViewById(R.id.txtPassengerEditPhoneNumber)).setText(passenger.getPhoneNumber());

        ((EditText) getActivity().findViewById(R.id.txtHomeAddressEdit)).setText(passenger.getAddress());

    }

}