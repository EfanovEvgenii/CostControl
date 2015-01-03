package com.jeezsoft.costcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by jeezic on 07.12.2014.
 */
public class DB {
    private static final String DB_NAME = "my_db";
    private static final int DB_VERSION = 2;
    private static final String DB_TABLE = "costItems";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TXT = "txt";
    public static final String COLUMN_IMG = "img";

    private static final String DB_TABLE_LIST = "_list";
    public static final String LIST_COLUMN_ID = "_id";
    public static final String LIST_COLUMN_DATE = "_date";
    public static final String LIST_COLUMN_MDATE = "_mdate";
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

    private static final String DB_CREATE_TABLE_LIST_2 =
            "create table " + DB_TABLE_LIST + "("+
                    LIST_COLUMN_ID + " integer primary key autoincrement, " +
                    LIST_COLUMN_DATE + " text not null default(DATETIME('now', 'localtime')), " +
                    LIST_COLUMN_MDATE + " integer, " +
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
                DB_TABLE_LIST+"."+LIST_COLUMN_MDATE + ", " +
                DB_TABLE_LIST+"."+LIST_COLUMN_SUM + ", " +
                DB_TABLE_LIST+"."+LIST_COLUMN_IDCOST + ", " +
                DB_TABLE + "." + COLUMN_TXT + " " +
                "from " + DB_TABLE_LIST + " AS " + DB_TABLE_LIST + " " +
                "left join " + DB_TABLE + " AS "+ DB_TABLE + " ON " + DB_TABLE_LIST+"."+LIST_COLUMN_IDCOST +" = " + DB_TABLE+"."+COLUMN_ID;

        return mDB.rawQuery(sqlQuery, null);
    }

    public Cursor getAllExpenditure() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    public Cursor getCostListForExport(Long millisStart, Long millisFinish) {
        String sqlQuery = "select " +
                "datetime("+DB_TABLE_LIST+"."+LIST_COLUMN_MDATE+", 'unixepoch', 'localtime')" + " AS "+LIST_COLUMN_DATE+", " +
                DB_TABLE + "." + COLUMN_TXT + ", " +
                DB_TABLE_LIST+"."+LIST_COLUMN_SUM + " " +
                "from " + DB_TABLE_LIST + " AS " + DB_TABLE_LIST + " " +
                "inner join " + DB_TABLE + " AS "+ DB_TABLE + " ON " + DB_TABLE_LIST+"."+LIST_COLUMN_IDCOST +" = " + DB_TABLE+"."+COLUMN_ID + " " +
                "where "+LIST_COLUMN_MDATE + " >= ? and " + LIST_COLUMN_MDATE + " <= ? " ;

        return mDB.rawQuery(sqlQuery, new String[]{Long.toString(millisStart/1000), Long.toString(millisFinish/1000)});
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

    public void updateCostListRec(Long id, Double sum, Long date, Long idExpenditure) {
        ContentValues cv = new ContentValues();
        cv.put(LIST_COLUMN_IDCOST, idExpenditure);
        cv.put(LIST_COLUMN_MDATE, date);
        cv.put(LIST_COLUMN_SUM, sum);
        mDB.update(DB_TABLE_LIST, cv, LIST_COLUMN_ID + "=" + id, null);
    }

    public void addListRec(Long id, Double sum, Long date) {
        ContentValues cv = new ContentValues();
        cv.put(LIST_COLUMN_IDCOST, id);
        cv.put(LIST_COLUMN_SUM, sum);
        if (date == 0){
            Calendar cal = Calendar.getInstance();
            Long _date = cal.getTimeInMillis()/1000;
            cv.put(LIST_COLUMN_MDATE, _date);
        }else {
            cv.put(LIST_COLUMN_MDATE, date);
        }
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
            db.execSQL(DB_CREATE_TABLE_LIST_2);

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

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            if (newVersion == 2){

                db.execSQL("ALTER TABLE " + DB_TABLE_LIST + " ADD " + LIST_COLUMN_MDATE + " integer ");
                db.execSQL("UPDATE " + DB_TABLE_LIST + " SET " + LIST_COLUMN_MDATE + " = " + "strftime('%s',"+LIST_COLUMN_DATE+")" + " WHERE 1=1");

            }

        }
    }
}
