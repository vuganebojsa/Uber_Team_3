package com.example.uberapp_tim3.fragments.driver;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.uberapp_tim3.R;
import com.example.uberapp_tim3.fragments.DrawRouteFragment;
import com.example.uberapp_tim3.model.DTO.DriverRideDTO;
import com.example.uberapp_tim3.model.DTO.RideUserDTO;
import com.google.maps.model.Unit;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

public class DriverCurrentRideFragment extends Fragment {

    private TextView tvHours, tvMinutes, tvSeconds;
    private Handler handler = new Handler();
    private Runnable runnable;
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private int elapsedTime = 0;
    public DriverCurrentRideFragment() {
        // Required empty public constructor
    }

    public static DriverCurrentRideFragment newInstance(String param1, String param2) {
        DriverCurrentRideFragment fragment = new DriverCurrentRideFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        DriverRideDTO rideDTO = null;
        if (bundle != null)
            rideDTO = bundle.getParcelable("ride");


        setViews(rideDTO);
        initializeTime();
        startMeasuringTime();
        setOnClickListeners();
    }

    private void setOnClickListeners(){
        setFinishRideListener();
        setPanicListener();
    }

    private void setPanicListener() {
        Button btnPanic = getActivity().findViewById(R.id.btnDriverCurrentRidePanic);
        btnPanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setFinishRideListener() {
        Button finishRide = getActivity().findViewById(R.id.btnFinishRideDriver);
        finishRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(runnable);

            }
        });
    }

    private void startMeasuringTime() {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    handler.postDelayed(this, 1000);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    int hours  = elapsedTime/3600;
                    int minutes = elapsedTime/60;
                    int seconds = elapsedTime % 60;
                    tvHours.setText(String.format("%02d", hours));
                    tvMinutes.setText(String.format("%02d", minutes));
                    tvSeconds.setText(String.format("%02d", seconds));
                    elapsedTime++;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    private void initializeTime(){
        tvHours = getActivity().findViewById(R.id.tv_hour);
        tvMinutes = getActivity().findViewById(R.id.tv_minute);
        tvSeconds = getActivity().findViewById(R.id.tv_second);
    }

    private void setViews(DriverRideDTO rideDTO) {

        assert rideDTO != null;
        requireActivity().getSupportFragmentManager().beginTransaction().replace(
                R.id.currentRideContainerDriver, new DrawRouteFragment(rideDTO)
        ).commit();

        TextView tvStartTime = getActivity().findViewById(R.id.txtDriverCurrentRideStartTime);
        TextView tvEndTIme = getActivity().findViewById(R.id.txtDriverCurrentRideEndTime);
        TextView tvDeparture = getActivity().findViewById(R.id.txtDriverCurrentRideDeparture);
        TextView tvDestination = getActivity().findViewById(R.id.txtDriverCurrentRideDestination);
        TextView tvPassengers = getActivity().findViewById(R.id.txtDriverCurrentRidePassengerCount);
        TextView tvPrice = getActivity().findViewById(R.id.txtDriverCurrentRidePrice);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        tvStartTime.setText(sdf.format(rideDTO.getStartTime()));
        tvEndTIme.setText(sdf.format(rideDTO.getEndTime()));
        tvDeparture.setText(rideDTO.getLocations().get(0).getDeparture().getAddress());
        tvDestination.setText(rideDTO.getLocations().get(rideDTO.getLocations().size() - 1).getDestination().getAddress());
        String totalPassengers = Integer.toString(rideDTO.getPassengers().size());
        tvPassengers.setText(totalPassengers);
        String totalCost = Double.toString(rideDTO.getTotalCost());
        tvPrice.setText(totalCost);


        LinearLayout lyPassengers = getActivity().findViewById(R.id.lyDriverCurrentRidePassengers);
        LayoutInflater inflater = (LayoutInflater)getView().getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int order = 1;
        for(RideUserDTO passenger: rideDTO.getPassengers()){
            View passengerView = inflater.inflate(R.layout.passenger_item, (ViewGroup) getView(), false);
            TextView twOrderNumber = passengerView.findViewById(R.id.txtRidePassengerOrder);
            String orderText = Integer.toString(order) + ".";
            twOrderNumber.setText(orderText);
            TextView twEmail = passengerView.findViewById(R.id.txtRidePassengerEmail);
            twEmail.setText(passenger.getEmail());
            order++;
            lyPassengers.addView(passengerView);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_current_ride, container, false);
    }
}