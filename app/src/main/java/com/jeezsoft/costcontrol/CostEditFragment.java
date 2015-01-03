package com.jeezsoft.costcontrol;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.util.ArrayMap;
import android.util.SparseLongArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.jeezsoft.costcontrol.R.id.etCostEditSumma;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CostEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CostEditFragment extends DialogFragment implements View.OnClickListener, TextView.OnEditorActionListener{

    private static final String ARG_ID = "id";
    private static final String ARG_DATE = "date";
    private static final String ARG_ID_EXPENDITURE = "idExpenditure";
    private static final String ARG_NAME_EXPENDITURE = "nameExpenditure";
    private static final String ARG_SUMMA = "summa";

    public static final String EXTRA_ID =
            "com.jeezsoft.costcontrol.id";
    public static final String EXTRA_DATE =
            "com.jeezsoft.costcontrol.date";
    public static final String EXTRA_ID_EXPENDITURE =
            "com.jeezsoft.costcontrol.id_expenditure";
    public static final String EXTRA_SUMMA =
            "com.jeezsoft.costcontrol.summa";

    private static final int REQUEST_EDITDATE = 0;

    private Long mId;
    private Long mDate;
    private Long mIdExpenditure;
    private Double mSum;
    private String mNameExpenditure;

    private int mYearDate;
    private int mMonthDate;
    private int mDayDate;

    private EditText etDate;
    private EditText etSumma;

    private DB db;

    public static CostEditFragment newInstance(Long id, Long date, Long idExpenditure, String nameExpenditure, Double sum) {
        CostEditFragment fragment = new CostEditFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        args.putLong(ARG_DATE, date);
        args.putLong(ARG_ID_EXPENDITURE, idExpenditure);
        args.putDouble(ARG_SUMMA, sum);
        args.putString(ARG_NAME_EXPENDITURE, nameExpenditure);
        fragment.setArguments(args);
        return fragment;
    }

    public CostEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setDateComponents(){

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mDate);

            mDayDate = calendar.get(Calendar.DAY_OF_MONTH);
            mMonthDate = calendar.get(Calendar.MONTH);
            mYearDate = calendar.get(Calendar.YEAR);

    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mId = getArguments().getLong(ARG_ID, 0);
            mDate = getArguments().getLong(ARG_DATE, 0L);
            setDateComponents();
            mIdExpenditure = getArguments().getLong(ARG_ID_EXPENDITURE, 0);
            mSum = getArguments().getDouble(ARG_SUMMA, 0.00);
            mNameExpenditure = getArguments().getString(ARG_NAME_EXPENDITURE, "");

        }

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_cost_edit, null);
        etSumma = (EditText) rootView.findViewById(R.id.etCostEditSumma);
        etSumma.setText(Double.toString(mSum));
        etSumma.setOnEditorActionListener(this);

        //EditText etExpenditure = (EditText) rootView.findViewById(R.id.etCostEditExpenditure);
        etDate = (EditText) rootView.findViewById(R.id.etCostEditDate);
        etDate.setText(getDateString(mDate));
        etDate.setOnClickListener(this);

        //etExpenditure.setText(mNameExpenditure);

        db = ((MainActivity)getActivity()).getDb();
        // адаптер
        Cursor cursor = db.getAllExpenditure();
        String from[] = new String[]{DB.COLUMN_TXT};
        int to[] = new int[]{R.id.tvExpenditureSpinnerItemName};

        SimpleCursorAdapter adapter = new MyCursorAdapter(getActivity(), R.layout.expenditure_spinner_item, cursor, from, to);
        adapter.setDropDownViewResource(R.layout.expenditure_spinner_dropdown_item);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.spCostEditExpenditure);
        spinner.setAdapter(adapter);
        // заголовок
        //spinner.setPrompt("Title");
        // выделяем элемент

        spinner.setSelection(getPositionOfId(spinner, mIdExpenditure));
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                mIdExpenditure = id;
                getArguments().putLong(ARG_ID_EXPENDITURE, mIdExpenditure);
                //Toast.makeText(getActivity(), "id = " + id, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        return new AlertDialog.Builder(getActivity()).setView(rootView)
                .setTitle("Изменение расхода")
                .setNegativeButton(
                        android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_CANCELED);
                            }
                        }
                )
                .setPositiveButton(
                        android.R.string.ok,

                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                sendResult(Activity.RESULT_OK);

                            }
                        })

                .create();
    }

    private int getPositionOfId(Spinner spinner, Long id) {
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemIdAtPosition(i) == id){
                index = i;
                break;
            }
        }
        return index;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getArguments().putLong(ARG_ID, mId);
        getArguments().putLong(ARG_DATE, mDate);
        getArguments().putLong(ARG_ID_EXPENDITURE, mIdExpenditure);
        getArguments().putDouble(ARG_SUMMA, mSum);
        getArguments().putString(ARG_NAME_EXPENDITURE, mNameExpenditure);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) return;

        Intent i = new Intent();
        i.putExtra(EXTRA_ID, mId);
        i.putExtra(EXTRA_DATE, mDate);
        i.putExtra(EXTRA_SUMMA, mSum);
        i.putExtra(EXTRA_ID_EXPENDITURE, mIdExpenditure);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.etCostEditDate:
                setDateComponents();
                DatepickerFragment newFragmentStartDate = DatepickerFragment.newInstance(mYearDate, mMonthDate, mDayDate);
                newFragmentStartDate.setTargetFragment(CostEditFragment.this, REQUEST_EDITDATE);
                newFragmentStartDate.show(getActivity().getFragmentManager(), "datePicker");
                break;
        }

    }


    public void sumInput(){

        mSum = getSum();
        getArguments().putDouble(ARG_SUMMA, mSum);

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
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            boolean b;
            if (imm != null) {
                b = imm.hideSoftInputFromWindow(etSumma.getWindowToken(), 0);
            }
            return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode){
            case REQUEST_EDITDATE:
                mYearDate = data.getIntExtra(DatepickerFragment.EXTRA_YEAR, 0);
                mMonthDate = data.getIntExtra(DatepickerFragment.EXTRA_MONTH, 0);
                mDayDate = data.getIntExtra(DatepickerFragment.EXTRA_DAY, 0);
                setDate(mYearDate, mMonthDate, mDayDate);
                break;

        }

    }
    public void setDate(int year, int month, int day){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0);
        mDate = cal.getTimeInMillis();

        etDate.setText(getDateString(mDate));
    }

    public String getDateString(Long date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return sdf.format(cal.getTime());
    }

    public class MyCursorAdapter extends SimpleCursorAdapter{

        private Cursor cursor;
        private Context context;


        public MyCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
            this.cursor = c;
            this.context = context;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);
            long id=getItemId(position);
            TextView tv = (TextView) view.findViewById(R.id.tvExpenditureSpinnerDropDownItemName);
            cursor.moveToPosition(position);
            tv.setText(cursor.getString(cursor.getColumnIndex(DB.COLUMN_TXT)));
            view.setTag(position);
            return view;
        }


    }
}
