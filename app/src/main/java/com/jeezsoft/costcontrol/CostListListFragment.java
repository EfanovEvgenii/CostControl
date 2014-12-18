package com.jeezsoft.costcontrol;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class CostListListFragment extends Fragment implements AbsListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DB db;
    SimpleCursorAdapter scAdapter;
    private static final int CM_DELETE_ID = 1;
    ListView lvData;

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

        db = ((MainActivity)getActivity()).getDb();
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

//        db = new DB(getActivity());
//        db.open();

        //cursor = db.getAllCostList();
//
//        if (cursor.moveToFirst()) {
//            do {
//                Toast.makeText(getActivity(), cursor.getString(1), Toast.LENGTH_SHORT).show();
//            } while (cursor.moveToNext());
//        }
        //this.getActivity().startManagingCursor(cursor);

        String[] from = new String[] {DB.LIST_COLUMN_DATE, DB.COLUMN_TXT, DB.LIST_COLUMN_SUM};
        int[] to = new int[] {R.id.tvListDate, R.id.tvListText, R.id.tvListSum};

        scAdapter = new SimpleCursorAdapter(this.getActivity(), R.layout.listitem, null, from, to, 0);

        ListView lvData = (ListView) view.findViewById(R.id.lvListData);
        lvData.setAdapter(scAdapter);

        lvData.setOnItemClickListener(this);

        // добавляем контекстное меню к списку
        //registerForContextMenu(lvData);

        // создаем лоадер для чтения данных
        getLoaderManager().initLoader(0, null, this);

///

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_costlist, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.sendmail_item:
                Toast.makeText(getActivity(),"Отправка почты",Toast.LENGTH_SHORT).show();
                boolean filecreated = false;
                String DIR_SD = "CostControl";
                String FILENAME_SD = "list.csv";
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    //Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
                    break;
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
                    Cursor cursor = db.getCostListForExport();

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
                        //this.getActivity().startManagingCursor(cursor);
                    // пишем данные
                    //bw.write("Содержимое файла на SD");
                    // закрываем поток
                    bw.close();
                    filecreated = true;
                    //Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (filecreated && sdFile != null) {

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("*/*");
                    String[] to = new String[]{""};
                    sharingIntent.putExtra(Intent.EXTRA_EMAIL, to);
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "Отчет по расходам в формате csv");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(sdFile));

                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Отчет по расходам");

//                    ArrayList<Uri> uris = new ArrayList<Uri>();
//                    String[] filePaths = new String[] {"list.csv"};
//                    for (String file : filePaths)
//                    {
//                        File fileIn = new File(file);
//                        Uri u = Uri.fromFile(fileIn);
//                        uris.add(u);
//                       }
//                    sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

                    startActivity(Intent.createChooser(sharingIntent, "Send email"));
                }
                break;
        }

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
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(id);
           }
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
