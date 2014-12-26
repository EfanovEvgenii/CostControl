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
        outState.putDouble("sum", getSum());
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


        mHandler.postDelayed(mShowInputMethodTask, 500);


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
//    protected void hideInputMethod() {
//        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(ctx.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(etSumma.getWindowToken(), 0);
//        }
//    }

    /**
     * показываем программную клавиатуру
     */
    protected void showInputMethod() {

        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(ctx.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(etSumma, InputMethodManager.RESULT_UNCHANGED_SHOWN);
            etSumma.setSelection(etSumma.getText().length());

        }
    }

    private Runnable mShowInputMethodTask = new Runnable() {
        public void run() {
            showInputMethod();
        }
    };


    @Override
    public void onClick(View view) {

        sumInput();
    }

    public void sumInput(){

        someEventListener.someEvent(getSum());

    }

    public Double getSum()
    {
        Double sum = 0.00;
        String textSum = etSumma.getText().toString();
        try{
            sum = Double.parseDouble(textSum);
        }catch (NumberFormatException e){};

        return sum;
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
