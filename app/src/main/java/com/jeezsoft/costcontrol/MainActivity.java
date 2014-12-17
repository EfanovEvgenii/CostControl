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
        db = ((MyApp)getApplication()).getDb(this);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
      //  outState.put

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



}
