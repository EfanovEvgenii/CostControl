package com.jeezsoft.costcontrol;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


import com.jeezsoft.costcontrol.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ExpenditureListFragment extends Fragment implements AbsListView.OnItemClickListener, AbsListView.MultiChoiceModeListener, LoaderManager.LoaderCallbacks<Cursor> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Context ctx;

    DB db;
    SimpleCursorAdapter scAdapter;
    AbsListView lvListExpenditure;

    // TODO: Rename and change types of parameters
    public static ExpenditureListFragment newInstance(String param1, String param2) {
        ExpenditureListFragment fragment = new ExpenditureListFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExpenditureListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenditure_list, container, false);

       // return super.onCreateView(inflater, container, savedInstanceState);
        String[] from = new String[] {DB.COLUMN_TXT};
        int[] to = new int[] {R.id.tvListItemExpenditureText};

        scAdapter = new SimpleCursorAdapter(this.getActivity(), R.layout.list_item_expenditure, null, from, to, 0);

        lvListExpenditure = (ListView) view.findViewById(R.id.lvListExpenditure);
        lvListExpenditure.setAdapter(scAdapter);
        scAdapter.notifyDataSetChanged();

        lvListExpenditure.setOnItemClickListener(this);

        lvListExpenditure.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvListExpenditure.setMultiChoiceModeListener(this);

        // создаем лоадер для чтения данных
        getLoaderManager().initLoader(1, null, this);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        ctx = getActivity();
        db = ((MainActivity)ctx).getDb();

//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

        // TODO: Change Adapter to display your content
//        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
//                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_expenditure_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_expenditure_add) {
           // Toast.makeText(ctx, "Add", Toast.LENGTH_SHORT).show();
            mListener.onExpenditureAdd();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//
//        if (null != mListener) {
//            // Notify the active callbacks interface (the activity, if the
//            // fragment is attached to one) that an item has been selected.
//            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
//        }
//    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_costlist_actionmode, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        SparseBooleanArray sparseBooleanArray = lvListExpenditure.getCheckedItemPositions();
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            if (sparseBooleanArray.valueAt(i)) {
                Cursor curRow = (Cursor) lvListExpenditure.getItemAtPosition(sparseBooleanArray.keyAt(i));
                int IDindex = curRow.getColumnIndex(db.COLUMN_ID);
                int curId = curRow.getInt(IDindex);
                db.delExpenditure(curId);
                getLoaderManager().getLoader(1).forceLoad();

            }
        }

        mode.finish();
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader2(this.getActivity(), db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        scAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor curRow = (Cursor) lvListExpenditure.getItemAtPosition(position);
        int IDindex = curRow.getColumnIndex(db.COLUMN_ID);
        int NAMEindex = curRow.getColumnIndex(db.COLUMN_TXT);
        int curId = curRow.getInt(IDindex);
        String curName = curRow.getString(NAMEindex);

        mListener.onExpenditureSelected(curId, curName);

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
        public void onExpenditureSelected(int id, String name);
        public void onExpenditureAdd();

    }

    static class MyCursorLoader2 extends CursorLoader {

        DB db;

        public MyCursorLoader2(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getAllExpenditure();
            return cursor;
        }

    }
}
