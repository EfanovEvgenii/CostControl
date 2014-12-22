package com.jeezsoft.costcontrol;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jeezsoft.costcontrol.R;
import com.jeezsoft.costcontrol.onSomeEventListener;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {

    EditText etSumma;
    private Handler mHandler = new Handler();

    Context ctx;

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
        ctx = activity;
        try {
            someEventListener = (onSomeEventListener) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+" must implement onSomeEventListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("sum", Float.parseFloat(etSumma.getText().toString()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    final String LOG_TAG = "myLogs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        etSumma = (EditText) rootView.findViewById(R.id.etSumma);
        Button button = (Button) rootView.findViewById(R.id.buttonSumOK);
        button.setOnClickListener(this);
        etSumma.setOnEditorActionListener(this);

        if (savedInstanceState != null){
            Float sum = savedInstanceState.getFloat("sum", 0);
            etSumma.setText(sum.toString());
        }

//
        //etSumma.requestFocus();
        //etSumma.set
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);



//        Runnable showingSoftKeyboard = new Runnable() {
//            public void run() {
//                try {
//                    EditText edtText = (EditText) getActivity().getCurrentFocus();
//                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
//                    inputMethodManager.showSoftInput(getActivity().getCurrentFocus(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
//                    edtText.setSelection(edtText.getText().length());
//                } catch(Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        new Handler().postDelayed(showingSoftKeyboard, 100);
        mHandler.postDelayed(mShowInputMethodTask, 100);
        //mHandler.post(mShowInputMethodTask);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * прячем программную клавиатуру
     */
    protected void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(ctx.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etSumma.getWindowToken(), 0);
        }
    }

    /**
     * показываем программную клавиатуру
     */
    protected void showInputMethod() {

        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(ctx.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(etSumma, InputMethodManager.RESULT_UNCHANGED_SHOWN);
            etSumma.setSelection(etSumma.getText().length());
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private Runnable mShowInputMethodTask = new Runnable() {
        public void run() {
            //Toast.makeText(ctx,"A!", Toast.LENGTH_LONG).show();
            showInputMethod();
            //someEventListener.showKeyboard(true);
        }
    };


    @Override
    public void onClick(View view) {

        sumInput();
    }

    public void sumInput(){
        float sum = Float.parseFloat(etSumma.getText().toString());
        someEventListener.someEvent(sum);

    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
            // обрабатываем нажатие кнопки GO
            sumInput();
            return true;
        }

        return false;
    }
}
