package com.jeezsoft.costcontrol;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SendingCostsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SendingCostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendingCostsFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int REQUEST_STARTDATE = 0;
    private static final int REQUEST_FINISHDATE = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView tvStartDate;
    private TextView tvFinishDate;

    private int mYearStartDate;
    private int mYearFinishDate;
    private int mMonthStartDate;
    private int mMonthFinishDate;
    private int mDayStartDate;
    private int mDayFinishDate;

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
        Calendar c = Calendar.getInstance();
        mYearStartDate = mYearFinishDate = c.get(Calendar.YEAR);
        mMonthStartDate = mMonthFinishDate = c.get(Calendar.MONTH);
        mDayStartDate = mDayFinishDate = c.get(Calendar.DAY_OF_MONTH);
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

        Button btnSend = (Button) rootView.findViewById(R.id.btnSendingCostsSend);
        btnSend.setOnClickListener(this);

        return rootView;
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

        switch (v.getId()){
            case R.id.etSendingCostsStartDate:

                DatepickerFragment newFragmentStartDate = DatepickerFragment.newInstance(mYearStartDate, mMonthStartDate, mDayStartDate);
                newFragmentStartDate.setTargetFragment(SendingCostsFragment.this, REQUEST_STARTDATE);
                newFragmentStartDate.show(getActivity().getFragmentManager(), "datePicker");
                break;
            case R.id.etSendingCostsFinishDate:

                DatepickerFragment newFragmentFinishDate = DatepickerFragment.newInstance(mYearStartDate, mMonthStartDate, mDayStartDate);
                newFragmentFinishDate.setTargetFragment(SendingCostsFragment.this, REQUEST_FINISHDATE);
                newFragmentFinishDate.show(getActivity().getFragmentManager(), "datePicker");
                break;
            case R.id.btnSendingCostsSend:
                if (mListener != null){
                    mListener.onSendCosts(mYearStartDate, mMonthStartDate, mDayStartDate, mYearFinishDate, mMonthFinishDate, mDayFinishDate);
                }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode){
            case REQUEST_STARTDATE:
                mYearStartDate = data.getIntExtra(DatepickerFragment.EXTRA_YEAR, 0);
                mMonthStartDate = data.getIntExtra(DatepickerFragment.EXTRA_MONTH, 0);
                mDayStartDate = data.getIntExtra(DatepickerFragment.EXTRA_DAY, 0);
                setStartDate(mYearStartDate, mMonthStartDate, mDayStartDate);
                break;
            case REQUEST_FINISHDATE:
                mYearFinishDate = data.getIntExtra(DatepickerFragment.EXTRA_YEAR, 0);
                mMonthFinishDate = data.getIntExtra(DatepickerFragment.EXTRA_MONTH, 0);
                mDayFinishDate = data.getIntExtra(DatepickerFragment.EXTRA_DAY, 0);
                setFinishDate(mYearFinishDate, mMonthFinishDate, mDayFinishDate);
                break;
        }

    }



    public void setStartDate(int year, int month, int day){

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        String datetime = sdf.format(cal.getTime());
        tvStartDate.setText(datetime);
    }

    public void setFinishDate(int year, int month, int day){

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        String datetime = sdf.format(cal.getTime());
        tvFinishDate.setText(datetime);

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onSendCosts(int yearStart, int monthStart, int dayStart, int yearFinish, int monthFinish, int dayFinish);
    }



}
