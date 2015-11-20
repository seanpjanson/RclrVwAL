package com.spjanson.rclrvwal;
/**
 * Copyright 11/20/2015 by Sean. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class CrudDB extends SQLiteOpenHelper {
  private static final int DB_VER = 1;

  static final class Tbl implements BaseColumns {
    static final String TBL_TEMP = "temp";
    static final String COL_TITL = "titl";
  }
  static final String[] COLS = {
    Tbl._ID,
    Tbl.COL_TITL,
  };
  static final int IDX_ID = 0;
  static final int IDX_TITL = 1;

  private String mOrdr;

  CrudDB(Context ctx, String name) { super(ctx, name, null, DB_VER);
    mOrdr = Tbl.COL_TITL + " ASC";
  }
  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL("CREATE TABLE " + Tbl.TBL_TEMP + " (" +
        Tbl._ID + " INTEGER PRIMARY KEY," +
        Tbl.COL_TITL + " TEXT UNIQUE NOT NULL );"
    );
  }
  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Tbl.TBL_TEMP);
    onCreate(sqLiteDatabase);
  }

  int create(ContentValues vls) {
    Cursor cur = null;
    try {
      SQLiteDatabase db = getWritableDatabase();
      long recId = db.insert(Tbl.TBL_TEMP, null, vls);
      cur = db.query(Tbl.TBL_TEMP, null, null, null, null, null, mOrdr);
      if (cur.moveToFirst()) {
        do {
          if (cur.getLong(IDX_ID) == recId)
            return cur.getPosition();
        } while (cur.moveToNext());
      }
    } catch (Exception ignore) {}
    finally { if (cur != null) cur.close(); }
    return -1;
  }
  ContentValues read(int pos) {
    Cursor cur = null;
    try {
      cur = getReadableDatabase().query(Tbl.TBL_TEMP, null, null, null, null, null, mOrdr);
      if (cur.moveToPosition(pos)) {
        return contVals(cur.getString(IDX_TITL));
      }
    } catch (Exception ignore){}
    finally { if (cur != null) cur.close(); }
    return null;
  }
  int update(String oldNm, String newNm) {
    Cursor cur = null;
    try {
      SQLiteDatabase db = getWritableDatabase();
      if (1 == db.update(Tbl.TBL_TEMP, contVals(newNm), Tbl.COL_TITL + " = '" + oldNm + "'", null)) {
        cur = db.query(Tbl.TBL_TEMP, null, null, null, null, null, mOrdr);
        if (cur.moveToFirst()) {
          do {
            if (cur.getString(IDX_TITL).equals(newNm))
              return cur.getPosition();
          } while (cur.moveToNext());
        }
      }
    } catch (Exception ignore) {}
    finally { if (cur != null) cur.close(); }
    return -1;
  }
  boolean delete(int pos) {
    Cursor cur = null;
    try {
      SQLiteDatabase db = getWritableDatabase();
      cur = db.query(Tbl.TBL_TEMP, null, null, null, null, null, mOrdr);
      cur.moveToPosition(pos);
      long id = cur.getLong(IDX_ID);
      return (1 == db.delete(Tbl.TBL_TEMP, Tbl._ID + " = '" + String.valueOf(id) + "'", null));
    } catch (Exception ignore ) {}
    finally { if (cur != null) cur.close(); }
    return false;

  }

  int size() {
    Cursor cur = null;
    try {
      SQLiteDatabase db = getReadableDatabase();
      cur = db.query(Tbl.TBL_TEMP, null, null, null, null, null, null);
      return cur.getCount();
    } catch (Exception ignore ) {}
    finally { if (cur != null) cur.close(); }
    return 0;
  }
  static ContentValues contVals(String name) {
    ContentValues values = new ContentValues();
    if (name != null) values.put(Tbl.COL_TITL, name);
    return values;
  }
}

//region REMOVED
/*
  // alternativve using _COUNT
  int size() {
    Cursor cur = null;
    try {
      SQLiteDatabase db = getReadableDatabase();
      cur = db.query(Tbl.TBL_TEMP, new String[] {Tbl._COUNT}, null, null, null, null, null);
      if (cur.moveToFirst())
        return cur.getInt(0);
    } catch (Exception ignore ) {}
    finally { if (cur != null) cur.close(); }
    return 0;
  }
*/
//endregion

