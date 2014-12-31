package com.jeezsoft.costcontrol;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;


import android.widget.ListView;
import android.widget.Toast;

import java.text.DecimalFormatSymbols;
import java.util.regex.Pattern;


public class MainActivity extends Activity implements onSomeEventListener, ListView.OnItemClickListener,
        CostItemListFragment.OnFragmentInteractionListener, ExpenditureListFragment.OnFragmentInteractionListener,
        ExpenditureEditFragment.OnFragmentInteractionListener, SendingCostsFragment.OnFragmentInteractionListener,
        View.OnClickListener {


    private String[] mMainMenu;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private String mTitle = "";

    CostItemListFragment costItemList;
    Double sum = 0.00;
    DB db;

    public DB getDb() {
        return db;
    }

    private static long back_pressed;

    FragmentTransaction fTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = ((MyApp)getApplication()).getDb(this);
        db.open();
        setContentView(R.layout.activity_main);

        mTitle = (String) getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMainMenu = getResources().getStringArray(R.array.mainmenu);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Выбор");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(getBaseContext(),
                  R.layout.drawer_listitem, mMainMenu));
          // Set the list's click listener
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);


         mDrawerList.setOnItemClickListener(this);


        if (savedInstanceState == null) {
            selectItem(0);
        }
        else {
            sum = savedInstanceState.getDouble("sum", 0.00);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("sum", sum);

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

        if (mDrawerToggle.onOptionsItemSelected(item)){
            showKeyboard(false);
            return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void showKeyboard(boolean show) {
        if (show) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            if (imm != null) {
                View v = getCurrentFocus();
                if (v != null) {
                    imm.showSoftInput(getCurrentFocus(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
            }
        }else{
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                boolean b;
                if (imm != null){
                    View v = getCurrentFocus();
                    if (v!=null) {
                        b = imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
    }

    @Override
    public void someEvent(Double sum) {
        this.sum = sum;
       // Toast.makeText(this, "summa = "+sum, Toast.LENGTH_LONG).show();
        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.container, new CostItemListFragment());
        //fTrans.addToBackStack(null);
        fTrans.commit();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        boolean b;
        if (imm != null) {
            b = imm.hideSoftInputFromWindow(findViewById(R.id.etSumma).getWindowToken(), 0);
        }
    }

    @Override
    public void onFragmentInteraction(Long id) {

        db.addListRec(id, sum);
        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.container, new PlaceholderFragment());
        //fTrans.addToBackStack(null);
        fTrans.commit();
        Toast.makeText(this, "Добавлен расход:  "+sum+" руб.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), "Нажмите еще раз для выхода",
                    Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    private void selectItem(int position) {

        mTitle = mMainMenu[position];

        switch (position){
            case 0: fTrans = getFragmentManager().beginTransaction();
                fTrans.replace(R.id.container, new PlaceholderFragment());
                fTrans.commit();
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 1: fTrans = getFragmentManager().beginTransaction();
                fTrans.replace(R.id.container, new CostListListFragment());
                fTrans.commit();
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 2: fTrans = getFragmentManager().beginTransaction();
                fTrans.replace(R.id.container, new SendingCostsFragment(), "sendingCosts");
                fTrans.commit();
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 3: fTrans = getFragmentManager().beginTransaction();
                fTrans.replace(R.id.container, new ExpenditureListFragment());
                fTrans.commit();
                mDrawerLayout.closeDrawer(mDrawerList);
                break;

        }
    }




    @Override
    public void onExpenditureSelected(int id, String name) {
       //Toast.makeText(this, "expenditure selected. id:"+id, Toast.LENGTH_SHORT).show();
        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.container, ExpenditureEditFragment.newInstance((long) id, name));
        //fTrans.addToBackStack(null);
        fTrans.commit();
    }

    @Override
    public void onExpenditureCancel() {
        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.container, new ExpenditureListFragment());
        fTrans.commit();
    }

    @Override
    public void onExpenditureAdd() {
        //Toast.makeText(this, "expenditure add.", Toast.LENGTH_SHORT).show();
        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.container, new ExpenditureEditFragment());
        //fTrans.addToBackStack(null);
        fTrans.commit();
    }

    @Override
    public void onExpenditureMustEdit(String name, Long id) {
        if (id == null || id == 0) {
            db.addExpenditure(name, R.drawable.exenditure_item);
        }
        else {
            db.updateExpenditure(id, name);
        }
        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.container, new ExpenditureListFragment());
        fTrans.commit();
    }

    @Override
    public void onDateMustSelect(int year, int month, int day) {
        fTrans = getFragmentManager().beginTransaction();
        fTrans.add(R.id.container, DatepickerFragment.newInstance(year, month, day), "datePicker");
        fTrans.commit();
    }

//    @Override
//    public void onDateSelected(int year, int month, int day) {
//        android.app.FragmentManager fragmentManager = getFragmentManager();
//        fTrans = fragmentManager.beginTransaction();
//        fTrans.remove(fragmentManager.findFragmentByTag("datePicker"));
//        fTrans.commit();
//
//        SendingCostsFragment sCF = (SendingCostsFragment) fragmentManager.findFragmentByTag("sendingCosts");
//        if (sCF != null) {
//            sCF.setStartDate(year,month,day);
//            Toast.makeText(this, "Год: " + year + "\n" + "Месяц: "
//                    + (month + 1) + "\n" + "День: " + day, Toast.LENGTH_LONG).show();
//        }
//    }
}
