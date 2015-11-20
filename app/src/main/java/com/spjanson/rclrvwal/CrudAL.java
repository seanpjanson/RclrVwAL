package com.spjanson.rclrvwal;
/**
 * Copyright 2015 Sean Janson. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CrudAL {
  static final String COL_TITL = "titl";
  static ContentValues contVals(String name) {
    ContentValues values = new ContentValues();
    if (name != null)
      values.put(COL_TITL, name);
    return values;
  }

  private List<ContentValues> mNames;

  CrudAL(Context ctx, String name) {
    mNames = new ArrayList<>();
  }

  int create(ContentValues vls) {
    if (!mNames.contains(vls)) try {
      mNames.add(vls);
      sort();
      return mNames.indexOf(vls);
    } catch (Exception ignore) {}
    return -1;
  }
  ContentValues read(int pos) {
    ContentValues cv = null;
    try {
      return mNames.get(pos);
    } catch (Exception ignore) {}
    return null;
  }
  int update(String oldNm, String newNm) {
    try {
      ContentValues oldCV = contVals(oldNm);
      ContentValues newCV = contVals(newNm);
      int oldPos = mNames.indexOf(oldCV);
      mNames.set(oldPos, newCV);
      sort();
      return mNames.indexOf(newCV);
    } catch (Exception ignore) {}
    return -1;
  }
  boolean delete(int pos) {
    try {
      return mNames.remove(pos) != null;
    } catch (Exception ignore) {}
    return false;
  }

  int size() {
    return mNames.size();
  }

  private void sort() {
    Collections.sort(mNames, new Comparator<ContentValues>() {
      @Override
      public int compare(ContentValues s1, ContentValues s2) {
        return s1.getAsString(COL_TITL).compareTo(s2.getAsString(COL_TITL));
      }
    });
  }
}
