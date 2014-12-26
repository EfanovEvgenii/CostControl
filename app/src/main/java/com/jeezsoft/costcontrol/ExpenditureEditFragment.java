package com.jeezsoft.costcontrol;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpenditureEditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExpenditureEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenditureEditFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "ExpenditureID";
    private static final String ARG_PARAM2 = "ExpenditureName";

    // TODO: Rename and change types of parameters
    private String mExpenditureName;
    private Long mExpenditureID;

    private Context ctx;
    private DB db;

    private EditText etEditExpenditureName;
    private Button btnSave;
    private Button btnCancel;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ExpenditureID Id from database.
     * @param ExpenditureName Parameter 2.
     * @return A new instance of fragment ExpenditureEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenditureEditFragment newInstance(Long ExpenditureID, String ExpenditureName) {
        ExpenditureEditFragment fragment = new ExpenditureEditFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, ExpenditureID);
        args.putString(ARG_PARAM2, ExpenditureName);
        fragment.setArguments(args);
        return fragment;
    }

    public ExpenditureEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = getActivity();
        db = ((MainActivity)ctx).getDb();

        if (getArguments() != null) {
            mExpenditureID = getArguments().getLong(ARG_PARAM1);
            mExpenditureName = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expenditure_edit, container, false);

        etEditExpenditureName = (EditText) rootView.findViewById(R.id.etEditExpenditureName);
        etEditExpenditureName.setOnEditorActionListener(this);

        Button btnButtonSave = (Button) rootView.findViewById(R.id.btnEditExpenditureSave);
        Button btnButtonCancel = (Button) rootView.findViewById(R.id.btnEditExpenditureCancel);

        btnButtonSave.setOnClickListener(this);
        btnButtonCancel.setOnClickListener(this);

        if (savedInstanceState != null){
            mExpenditureID = savedInstanceState.getLong("ExpenditureID", 0);
            mExpenditureName = savedInstanceState.getString("ExpenditureName", "");
        }

        etEditExpenditureName.setText(mExpenditureName);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("ExpenditureID", mExpenditureID);
        outState.putString("ExpenditureName", mExpenditureName);
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

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

        switch(v.getId()){
            case R.id.btnEditExpenditureSave:
                mExpenditureName = etEditExpenditureName.getText().toString();
                //Toast.makeText(ctx, "Save. ID:"+mExpenditureID + ", Name:"+mExpenditureName, Toast.LENGTH_SHORT).show();
                mListener.onExpenditureMustEdit(mExpenditureName, mExpenditureID);
                break;
            case R.id.btnEditExpenditureCancel:
                //Toast.makeText(ctx, "Cancel. ID:"+mExpenditureID + ", Name:"+mExpenditureName, Toast.LENGTH_SHORT).show();
                mListener.onExpenditureCancel();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        //if (actionId == EditorInfo.IME_ACTION_DONE) {
            // обрабатываем нажатие кнопки GO
            mExpenditureName = etEditExpenditureName.getText().toString();
            return true;
        //}

        //return false;
    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onExpenditureMustEdit(String name, Long id);
        public void onExpenditureCancel();
    }

}
