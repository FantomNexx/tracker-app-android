package com.fantomsoftware.tracker.data.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.data.Storage;


public class DBHelper extends SQLiteOpenHelper{
//--------------------------------------------------------------------
//private static final String DATABASE_NAME    = "tracker.db";
private static final int DATABASE_VERSION = 1;
//--------------------------------------------------------------------

//--------------------------------------------------------------------
public DBHelper( Context context ){
  //super( context, DATABASE_NAME, null, DATABASE_VERSION );
  super( context, Storage.getFullPathDatabase(), null, DATABASE_VERSION );

  SQLiteDatabase.openOrCreateDatabase(
      Storage.getFullPathDatabase(), null );

}
//--------------------------------------------------------------------
@Override
public void onCreate( SQLiteDatabase database ){
  try{
    database.execSQL( DBTrackPoints.GetDBCreateSql() );
  }catch( Exception e ){
    Log.d( AppData.log_key, "DBHelper.onCreate, DB create failed" );
  }//try
}
//--------------------------------------------------------------------
@Override
public void onUpgrade( SQLiteDatabase db, int version_o, int version_n ){

  Log.w( DBHelper.class.getName(),
      "Upgrading database from version " + version_o + " to "
          + version_n + ", which will destroy all old data" );

  db.execSQL( "DROP TABLE IF EXISTS " + DBTrackPoints.TABLE_NAME );

  onCreate( db );
}
//--------------------------------------------------------------------
}//DBHelper