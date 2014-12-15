package com.jeezsoft.costcontrol;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import java.text.DecimalFormatSymbols;
import java.util.regex.Pattern;


public class MainActivity extends Activity implements onSomeEventListener, CostItemListFragment.OnFragmentInteractionListener, View.OnClickListener {

    CostItemListFragment costItemList;
    float sum = 0;
    DB db;

    public DB getDb() {
        return db;
    }


    FragmentTransaction fTrans;

      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DB(this);
        db.open();
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            fTrans = getFragmentManager().beginTransaction();
            fTrans.add(R.id.container, new PlaceholderFragment());
            fTrans.addToBackStack(null);
            fTrans.commit();
        }

        Button btnList = (Button) findViewById(R.id.buttonList);
        btnList.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void someEvent(float sum) {
        this.sum = sum;
       // Toast.makeText(this, "summa = "+sum, Toast.LENGTH_LONG).show();
        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.container, new CostItemListFragment());
        fTrans.addToBackStack(null);
        fTrans.commit();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        boolean b;
        if (imm != null) {
            b = imm.hideSoftInputFromWindow(findViewById(R.id.etSumma).getWindowToken(), 0);
        }
    }

    @Override
    public void onFragmentInteraction(Long id) {
       // Toast.makeText(this, "id = "+id, Toast.LENGTH_LONG).show();
        db.addListRec(id, sum);
        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.container, new PlaceholderFragment());
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonList: fTrans = getFragmentManager().beginTransaction();
                fTrans.replace(R.id.container, new CostListListFragment());
                fTrans.addToBackStack(null);
                fTrans.commit();
                break;
        }

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {



        onSomeEventListener someEventListener;

        public PlaceholderFragment() {
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

            etSumma.setFilters(new InputFilter[]{new PriceInputFilter()});
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etSumma, 0);
            }

            return rootView;
        }



    }

    public class PriceInputFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            String checkedText = dest.toString() + source.toString();
            String pattern = getPattern();

            if (!Pattern.matches(pattern, checkedText)) {
                return "";
            }

            return null;
        }

        private String getPattern() {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            String ds = String.valueOf(dfs.getDecimalSeparator());
            String pattern = "[0-9]+([" + ds + "]{1}||[" + ds + "]{1}[0-9]{1,2})?";
            return pattern;
        }
    }
}
