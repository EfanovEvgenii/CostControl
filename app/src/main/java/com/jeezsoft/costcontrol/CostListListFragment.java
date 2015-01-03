package com.jeezsoft.costcontrol;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class CostListListFragment extends Fragment implements AbsListView.OnItemClickListener, AbsListView.MultiChoiceModeListener, LoaderManager.LoaderCallbacks<Cursor> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int REQUEST_EDITCOST = 0;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DB db;
    SimpleCursorAdapter scAdapter;
    private static final int CM_DELETE_ID = 1;
    AbsListView lvData;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
//    public static CostItemListFragment newInstance(String param1, String param2) {
//        CostItemListFragment fragment = new CostItemListFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CostListListFragment() {

    }

 //   @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        db = getDb();
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//
//        // TODO: Change Adapter to display your content
//        mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
//                android.R.layout.simple_list_item_2, android.R.id.text1, DummyContent.ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_list, container, false);

        String[] from = new String[] {DB.LIST_COLUMN_MDATE, DB.COLUMN_TXT, DB.LIST_COLUMN_SUM};//, DB.LIST_COLUMN_ID};
        int[] to = new int[] {R.id.tvListDate, R.id.tvListText, R.id.tvListSum};//, R.id.tvListId};

        scAdapter = new SimpleCursorAdapter(this.getActivity(), R.layout.listitem, null, from, to, 0);
        scAdapter.setViewBinder(new MyViewBinder());

        //final ListView
        lvData = (ListView) view.findViewById(R.id.lvListData);
        lvData.setAdapter(scAdapter);
        scAdapter.notifyDataSetChanged();

        lvData.setOnItemClickListener(this);

        lvData.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvData.setMultiChoiceModeListener(this);

        // создаем лоадер для чтения данных
        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    class MyViewBinder implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

            switch (view.getId()) {
                case R.id.tvListSum:
                    Double sum = cursor.getDouble(columnIndex);
                    ((TextView)view).setText(sum.toString());
                    ((TextView) view).setTextColor(Color.BLUE);
                    return true;
                case R.id.tvListDate:
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    ((TextView)view).setText(sdf.format(new Date(cursor.getLong(columnIndex)*1000)));
                    return true;

            }
            return false;
        }
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_costlist_actionmode, menu);
        return true;
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        SparseBooleanArray sparseBooleanArray = lvData.getCheckedItemPositions();
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            if (sparseBooleanArray.valueAt(i)) {
                Cursor curRow = (Cursor) lvData.getItemAtPosition(sparseBooleanArray.keyAt(i));
                int IDindex = curRow.getColumnIndex(DB.LIST_COLUMN_ID);
                int curId = curRow.getInt(IDindex);
                getDb().delListRec(curId);
                getLoaderManager().getLoader(0).forceLoad();

            }
        }

        mode.finish();
        return false;
    }

    public void onDestroyActionMode(ActionMode mode) {
    }

    public void onItemCheckedStateChanged(ActionMode mode,
                                          int position, long id, boolean checked) {
        //Toast.makeText(getActivity(), "position = " + position + ", checked = ", Toast.LENGTH_LONG).show();
//                        + checked);
//                Log.d(LOG_TAG, "position = " + position + ", checked = "
//                        + checked);
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_costlist, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        switch (item.getItemId()){
//            case R.id.sendmail_item:
//                Toast.makeText(getActivity(),"Отправка почты",Toast.LENGTH_SHORT).show();
//                boolean filecreated = false;
//                String DIR_SD = "CostControl";
//                String FILENAME_SD = "list.csv";
//                if (!Environment.getExternalStorageState().equals(
//                        Environment.MEDIA_MOUNTED)) {
//                    //Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
//                    break;
//                }
//                // получаем путь к SD
//                File sdPath = Environment.getExternalStorageDirectory();
//                // добавляем свой каталог к пути
//                sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
//                // создаем каталог
//                sdPath.mkdirs();
//                // формируем объект File, который содержит путь к файлу
//                File sdFile = new File(sdPath, FILENAME_SD);
//                try {
//                    // открываем поток для записи
//                    BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
//                    Cursor cursor = db.getCostListForExport();
//
//                    if (cursor.moveToFirst()) {
//                        do {
//                            String listString = "";
//                            for (int i=0; i<cursor.getColumnCount();i++) {
//                                //Toast.makeText(getActivity(), cursor.getString(i), Toast.LENGTH_SHORT).show();
//                                listString = listString + cursor.getString(i) + ((i==cursor.getColumnCount()-1) ? "" : ";");
//                            }
//                            bw.write(listString+"\n");
//                        } while (cursor.moveToNext());
//                    }
//
//                    bw.close();
//                    filecreated = true;
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if (filecreated && sdFile != null) {
//
//                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                    sharingIntent.setType("*/*");
//                    String[] to = new String[]{""};
//                    sharingIntent.putExtra(Intent.EXTRA_EMAIL, to);
//                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "Отчет по расходам в формате csv");
//                    sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(sdFile));
//
//                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Отчет по расходам");
//
//                    startActivity(Intent.createChooser(sharingIntent, "Send email"));
//                }
//                break;
//        }
//
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor curRow = (Cursor) lvData.getItemAtPosition(position);
        int IDindex = curRow.getColumnIndex(DB.LIST_COLUMN_ID);
        int SUMindex = curRow.getColumnIndex(DB.LIST_COLUMN_SUM);
        int DATEindex = curRow.getColumnIndex(DB.LIST_COLUMN_MDATE);
        int IDCOSTindex = curRow.getColumnIndex(DB.LIST_COLUMN_IDCOST);
        int NAMECOSTindex = curRow.getColumnIndex(DB.COLUMN_TXT);

        Long curId = curRow.getLong(IDindex);
        Long curMDate = curRow.getLong(DATEindex)*1000;
        Long curIdExpenditure = curRow.getLong(IDCOSTindex);
        String curNameExpenditure = curRow.getString(NAMECOSTindex);
        Double curSum = curRow.getDouble(SUMindex);


        CostEditFragment costEditFragment = CostEditFragment.newInstance(curId, curMDate, curIdExpenditure, curNameExpenditure, curSum);
        costEditFragment.setTargetFragment(CostListListFragment.this, REQUEST_EDITCOST);
        costEditFragment.show(getActivity().getFragmentManager(), "editCost");

    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
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
        public void onFragmentInteraction(Long id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode){
            case REQUEST_EDITCOST:
                Long id = data.getLongExtra(CostEditFragment.EXTRA_ID, 0L);
                Long mDate = data.getLongExtra(CostEditFragment.EXTRA_DATE, 0L);
                Double mSum = data.getDoubleExtra(CostEditFragment.EXTRA_SUMMA, 0.00);
                Long mIdExpenditure = data.getLongExtra(CostEditFragment.EXTRA_ID_EXPENDITURE, 0L);

                getDb().updateCostListRec(id, mSum, mDate/1000, mIdExpenditure);
                getLoaderManager().getLoader(0).forceLoad();
                break;

        }
    }

    public DB getDb(){
        if (db == null) {
            db = ((MainActivity) getActivity()).getDb();
        }
        return db;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this.getActivity(), db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        scAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    static class MyCursorLoader extends CursorLoader {

        DB db;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getAllCostList();
//            try {
//                 TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            return cursor;
        }

    }

}
