package com.jeezsoft.costcontrol;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SendingCostsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SendingCostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendingCostsFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView tvStartDate;
    private TextView tvFinishDate;

    int DIALOG_DATE = 1;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendingCostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendingCostsFragment newInstance(String param1, String param2) {
        SendingCostsFragment fragment = new SendingCostsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SendingCostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sending_costs, container, false);
        tvStartDate = (TextView) rootView.findViewById(R.id.etSendingCostsStartDate);
        tvFinishDate = (TextView) rootView.findViewById(R.id.etSendingCostsFinishDate);

        tvStartDate.setOnClickListener(this);
        tvFinishDate.setOnClickListener(this);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd;

        switch (v.getId()){
            case R.id.etSendingCostsStartDate:
                dpd = new DatePickerDialog(getActivity(), this, year, month, day);
                DIALOG_DATE = 1;
                dpd.show();
//                DialogFragment newFragment = new DateDialogFragment();
//                newFragment.show(getFragmentManager(), "datePicker");
                break;
            case R.id.etSendingCostsFinishDate:
                dpd = new DatePickerDialog(getActivity(), this, year, month, day);
                DIALOG_DATE = 2;
                dpd.show();
                break;
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        switch (DIALOG_DATE){
            case 1:
                tvStartDate.setText(""+dayOfMonth+"-"+monthOfYear+"-"+year);
                break;
            case 2:
                tvFinishDate.setText(""+dayOfMonth+"-"+monthOfYear+"-"+year);
                break;

        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }



}
