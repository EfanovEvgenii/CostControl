package com.jeezsoft.costcontrol;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
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


import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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
    private int mDrawerItem = 0;

    CostItemListFragment costItemList;
    Double sum = 0.00;
    DB db;

    // имена атрибутов для Map
    final String ATTRIBUTE_NAME_TEXT = "text";
    final String ATTRIBUTE_NAME_IMG = "img";

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
                showKeyboard(false);
                ActionBar actionBar = getActionBar();
                if (actionBar != null) {
                    actionBar.setTitle("Выбор");
                }
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (mDrawerItem == 0){
                    showKeyboard(true);
                }
                ActionBar actionBar = getActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(mTitle);
                }
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Set the adapter for the list view
        ArrayList<Map<String, Object>> alMainMenu = new ArrayList<Map<String, Object>>(mMainMenu.length);
        Map<String, Object> m;
        for (int i = 0; i < mMainMenu.length; i++) {
            m = new HashMap<String, Object>();
            if (mMainMenu[i].equals("Настройки")) {
                m.put(ATTRIBUTE_NAME_TEXT, new LayoutDrawerList(mMainMenu[i], Gravity.RIGHT+Gravity.CENTER_VERTICAL, android.R.drawable.ic_menu_preferences));
            }else{
                m.put(ATTRIBUTE_NAME_TEXT, new LayoutDrawerList(mMainMenu[i], Gravity.LEFT+Gravity.CENTER_VERTICAL, 0));
            }
            alMainMenu.add(m);
        }


        String[] from = {ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_TEXT};
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.tvDrawerItemText, R.id.ivDrawerItemImage};

        SimpleAdapter sAdapter = new SimpleAdapter(getBaseContext(), alMainMenu, R.layout.drawer_listitem, from, to);
        sAdapter.setViewBinder(new MyViewBinder());
        mDrawerList.setAdapter(sAdapter);
          // Set the list's click listener
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


         mDrawerList.setOnItemClickListener(this);


        if (savedInstanceState == null) {
            selectItem(0);
        }
        else {
            sum = savedInstanceState.getDouble("sum", 0.00);
        }

    }

    private class LayoutDrawerList{
        int img;
        String txt;
        int grav;

        private LayoutDrawerList(String txt, int grav, int img) {
            this.img = img;
            this.txt = txt;
            this.grav = grav;
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
            //showKeyboard(false);
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

        db.addListRec(id, sum, 0L);
        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.container, new PlaceholderFragment());
        //fTrans.addToBackStack(null);
        fTrans.commit();
        Toast.makeText(this, "Добавлен расход:  "+sum+" руб.", Toast.LENGTH_SHORT).show();
    }

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
        mDrawerItem = position;

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
                fTrans.replace(R.id.container, SendingCostsFragment.newInstance(PreferenceManager.getDefaultSharedPreferences(this).getString("emailAddress", "")), "sendingCosts");
                fTrans.commit();
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 3: fTrans = getFragmentManager().beginTransaction();
                fTrans.replace(R.id.container, new ExpenditureListFragment());
                fTrans.commit();
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 4: fTrans = getFragmentManager().beginTransaction();
                fTrans.replace(R.id.container, PreferenceFragmentImpl.newInstance());
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
    public void onSendCosts(int yearStart, int monthStart, int dayStart, int yearFinish, int monthFinish, int dayFinish, String email) {
        Toast.makeText(this,"Отправка почты",Toast.LENGTH_SHORT).show();
        boolean filecreated = false;
        String DIR_SD = "CostControl";
        String FILENAME_SD = "list.csv";
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this,"SD-карта не доступна: ",Toast.LENGTH_SHORT).show();
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            Calendar cal = Calendar.getInstance();
            cal.set(yearStart, monthStart, dayStart);
            Long millisStart = cal.getTimeInMillis();
            cal.set(yearFinish, monthFinish, dayFinish);
            Long millisFinish = cal.getTimeInMillis();

            Cursor cursor = db.getCostListForExport(millisStart, millisFinish);

            if (cursor.moveToFirst()) {
                do {
                    String listString = "";
                    for (int i=0; i<cursor.getColumnCount();i++) {
                        //Toast.makeText(getActivity(), cursor.getString(i), Toast.LENGTH_SHORT).show();
                        listString = listString + cursor.getString(i) + ((i==cursor.getColumnCount()-1) ? "" : ";");
                    }
                    bw.write(listString+"\n");
                } while (cursor.moveToNext());
            }

            bw.close();
            filecreated = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (filecreated && sdFile != null) {

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("*/*");
            String[] to = new String[]{email};
            sharingIntent.putExtra(Intent.EXTRA_EMAIL, to);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "Отчет по расходам в формате csv");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(sdFile));

            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Отчет по расходам");

            startActivity(Intent.createChooser(sharingIntent, "Send email"));
        }
    }

    class MyViewBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            LayoutDrawerList ldl = (LayoutDrawerList) data;
            switch (view.getId()) {
                // LinearLayout
                case R.id.tvDrawerItemText:
                    TextView tv = (TextView) view;
                    tv.setText(ldl.txt);
                   // tv.setGravity(ldl.grav);

                    return true;
                case R.id.ivDrawerItemImage:
                    ImageView iv = (ImageView) view;
                    if (ldl.img != 0) {
                        try {
                            iv.setImageResource(ldl.img);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return true;

            }
            return false;
        }
    }
}
