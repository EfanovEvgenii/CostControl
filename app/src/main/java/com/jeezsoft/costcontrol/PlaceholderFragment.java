package com.jeezsoft.costcontrol;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.jeezsoft.costcontrol.R;
import com.jeezsoft.costcontrol.onSomeEventListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {



    onSomeEventListener someEventListener;

    public PlaceholderFragment() {
    }

    public static PlaceholderFragment newInstance(String param1, String param2) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        //  args.putString(ARG_PARAM1, param1);
        //  args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            someEventListener = (onSomeEventListener) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+" must implement onSomeEventListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    final String LOG_TAG = "myLogs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final EditText etSumma = (EditText) rootView.findViewById(R.id.etSumma);
        Button button = (Button) rootView.findViewById(R.id.buttonSumOK);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float sum = Float.parseFloat(etSumma.getText().toString());
                someEventListener.someEvent(sum);
            }
        });

        // etSumma.setFilters(new InputFilter[]{new PriceInputFilter()});
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.showSoftInput(etSumma, 0);
//        }

        return rootView;
    }



}
