package com.jeezsoft.costcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jeezic on 07.12.2014.
 */
public class DB {
    private static final String DB_NAME = "my_db";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "costItems";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TXT = "txt";
    public static final String COLUMN_IMG = "img";

    private static final String DB_TABLE_LIST = "_list";
    public static final String LIST_COLUMN_ID = "_id";
    public static final String LIST_COLUMN_DATE = "_date";
    public static final String LIST_COLUMN_SUM = "sum";
    public static final String LIST_COLUMN_IDCOST = "idcost";

    //private static final String DB_VIEW_LIST = "vlist";

    private static final String DB_CREATE_TABLE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_IMG + " integer, " +
                    COLUMN_TXT + " text " +
                      ");";
    private static final String DB_CREATE_TABLE_LIST =
            "create table " + DB_TABLE_LIST + "("+
                    LIST_COLUMN_ID + " integer primary key autoincrement, " +
                    LIST_COLUMN_DATE + " text not null default(DATETIME('now', 'localtime'))," +
                    LIST_COLUMN_SUM + " real, " +
                    LIST_COLUMN_IDCOST + " integer);";

    private final Context mCtx;


    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    public Cursor getAllCostList() {
        String sqlQuery = "select " +
                DB_TABLE_LIST+"."+LIST_COLUMN_ID + ", " +
                DB_TABLE_LIST+"."+LIST_COLUMN_DATE + ", " +
                DB_TABLE_LIST+"."+LIST_COLUMN_SUM + ", " +
                DB_TABLE_LIST+"."+LIST_COLUMN_IDCOST + ", " +
                DB_TABLE + "." + COLUMN_TXT + " " +
                "from " + DB_TABLE_LIST + " AS " + DB_TABLE_LIST + " " +
                "inner join " + DB_TABLE + " AS "+ DB_TABLE + " ON " + DB_TABLE_LIST+"."+LIST_COLUMN_IDCOST +" = " + DB_TABLE+"."+COLUMN_ID;

        return mDB.rawQuery(sqlQuery, null);
    }

    public Cursor getAllExpenditure() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    public Cursor getCostListForExport() {
        String sqlQuery = "select " +
                DB_TABLE_LIST+"."+LIST_COLUMN_DATE + ", " +
                DB_TABLE + "." + COLUMN_TXT + ", " +
                DB_TABLE_LIST+"."+LIST_COLUMN_SUM + " " +
                "from " + DB_TABLE_LIST + " AS " + DB_TABLE_LIST + " " +
                "inner join " + DB_TABLE + " AS "+ DB_TABLE + " ON " + DB_TABLE_LIST+"."+LIST_COLUMN_IDCOST +" = " + DB_TABLE+"."+COLUMN_ID;

        return mDB.rawQuery(sqlQuery, null);
    }

    // добавить запись в DB_TABLE
    public void addRec(String txt, int img) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TXT, txt);
        cv.put(COLUMN_IMG, img);
        mDB.insert(DB_TABLE, null, cv);
    }

    public void addExpenditure(String txt, int img) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TXT, txt);
        cv.put(COLUMN_IMG, img);
        mDB.insert(DB_TABLE, null, cv);
    }

    public void updateExpenditure(Long id, String txt) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TXT, txt);
        mDB.update(DB_TABLE, cv, COLUMN_ID + "=" + id, null);
    }

    public void addListRec(Long id, Double sum) {
        ContentValues cv = new ContentValues();
        cv.put(LIST_COLUMN_IDCOST, id);
        cv.put(LIST_COLUMN_SUM, sum);
        mDB.insert(DB_TABLE_LIST, null, cv);
    }


    // удалить запись из DB_TABLE
    public void delRec(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    // удалить запись из DB_TABLE
    public void delExpenditure(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    public void delListRec(long id) {
        mDB.delete(DB_TABLE_LIST, LIST_COLUMN_ID + " = " + id, null);
    }
    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {

            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_TABLE);
            db.execSQL(DB_CREATE_TABLE_LIST);

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_TXT, "Супермаркет");
            cv.put(COLUMN_IMG, R.drawable.ic_launcher);
            db.insert(DB_TABLE, null, cv);
            cv.put(COLUMN_TXT, "Аптека");
            cv.put(COLUMN_IMG, R.drawable.ic_launcher);
            db.insert(DB_TABLE, null, cv);
            cv.put(COLUMN_TXT, "Кафе и рестораны");
            cv.put(COLUMN_IMG, R.drawable.ic_launcher);
            db.insert(DB_TABLE, null, cv);
            cv.put(COLUMN_TXT, "Спорт");
            cv.put(COLUMN_IMG, R.drawable.ic_launcher);
            db.insert(DB_TABLE, null, cv);
            cv.put(COLUMN_TXT, "Транспорт");
            cv.put(COLUMN_IMG, R.drawable.ic_launcher);
            db.insert(DB_TABLE, null, cv);
            cv.put(COLUMN_TXT, "Связь и интернет");
            cv.put(COLUMN_IMG, R.drawable.ic_launcher);
            db.insert(DB_TABLE, null, cv);

//
//            cv.clear();
//            cv.put(LIST_COLUMN_IDCOST, 0);
//            cv.put(LIST_COLUMN_SUM, 500);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 1);
//            cv.put(LIST_COLUMN_SUM, 200);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 1);
//            cv.put(LIST_COLUMN_SUM, 1300);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 3);
//            cv.put(LIST_COLUMN_SUM, 45345.45);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 3);
//            cv.put(LIST_COLUMN_SUM, 500);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 5);
//            cv.put(LIST_COLUMN_SUM, 200);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 1);
//            cv.put(LIST_COLUMN_SUM, 1300);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 4);
//            cv.put(LIST_COLUMN_SUM, 45345.45);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 0);
//            cv.put(LIST_COLUMN_SUM, 500);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 1);
//            cv.put(LIST_COLUMN_SUM, 200);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 1);
//            cv.put(LIST_COLUMN_SUM, 1300);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 3);
//            cv.put(LIST_COLUMN_SUM, 45345.45);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 3);
//            cv.put(LIST_COLUMN_SUM, 500);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 5);
//            cv.put(LIST_COLUMN_SUM, 200);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 1);
//            cv.put(LIST_COLUMN_SUM, 1300);
//            db.insert(DB_TABLE_LIST, null, cv);
//
//            cv.put(LIST_COLUMN_IDCOST, 4);
//            cv.put(LIST_COLUMN_SUM, 45345.45);
//            db.insert(DB_TABLE_LIST, null, cv);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
