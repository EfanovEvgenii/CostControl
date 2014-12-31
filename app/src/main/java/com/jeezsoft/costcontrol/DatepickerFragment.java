package com.jeezsoft.costcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;



public class DatepickerFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "year";
    private static final String ARG_PARAM2 = "month";
    private static final String ARG_PARAM3 = "day";

    public static final String EXTRA_YEAR =
            "com.jeezsoft.costcontrol.year";
    public static final String EXTRA_MONTH =
            "com.jeezsoft.costcontrol.month";
    public static final String EXTRA_DAY =
            "com.jeezsoft.costcontrol.day";

    // TODO: Rename and change types of parameters
    private int mYear;
    private int mMonth;
    private int mDay;

    private DatePicker pickerDate;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param year Year.
     * @param month month of year (0-11).
     * @param day day of month
     * @return A new instance of fragment DatepickerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DatepickerFragment newInstance(int year, int month, int day) {
        DatepickerFragment fragment = new DatepickerFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, year);
        args.putInt(ARG_PARAM2, month);
        args.putInt(ARG_PARAM3, day);
        fragment.setArguments(args);
        return fragment;
    }

    public DatepickerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mYear = getArguments().getInt(ARG_PARAM1);
//            mMonth = getArguments().getInt(ARG_PARAM2);
//            mDay = getArguments().getInt(ARG_PARAM3);
//        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mYear = getArguments().getInt(ARG_PARAM1);
            mMonth = getArguments().getInt(ARG_PARAM2);
            mDay = getArguments().getInt(ARG_PARAM3);
        }

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_datepicker, null);
        pickerDate = (DatePicker) rootView.findViewById(R.id.datePicker);
        pickerDate.init(mYear,mMonth,mDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;

                getArguments().putInt(ARG_PARAM1, mYear);
                getArguments().putInt(ARG_PARAM2, mMonth);
                getArguments().putInt(ARG_PARAM3, mDay);
            }
        });
        return new AlertDialog.Builder(getActivity()).setView(rootView)
                .setTitle("Выбор даты")
                .setPositiveButton(
                        android.R.string.ok,

                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                sendResult(Activity.RESULT_OK);

                            }
                        })

                .create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) return;

        Intent i = new Intent();
        i.putExtra(EXTRA_YEAR, mYear);
        i.putExtra(EXTRA_MONTH, mMonth);
        i.putExtra(EXTRA_DAY, mDay);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, i);
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onDateSelected(int year, int month, int day);
//    }

}
