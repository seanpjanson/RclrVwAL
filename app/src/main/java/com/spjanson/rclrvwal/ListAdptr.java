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
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class ListAdptr extends RecyclerView.Adapter<ListAdptr.ViewHldr> {
  private Activity mAct;
  private LinearLayoutManager mLLMMgr;
  private CRUD mNames;
  private int mPos = -1;
  private View mSelVw;

  ListAdptr(final Activity act, RecyclerView recVw) {
    mAct = act;
    mNames = new CRUD();
    mLLMMgr = new LinearLayoutManager(mAct, LinearLayoutManager.VERTICAL, false);
    recVw.setAdapter(this);
    recVw.setLayoutManager(mLLMMgr);
    new ItemTouchHelper(
      new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView r, RecyclerView.ViewHolder v, RecyclerView.ViewHolder t) {
          return false;
        }
        @Override
        public void onSwiped(final RecyclerView.ViewHolder hldr, int d) {
          final int pos = hldr.getAdapterPosition();
          new AlertDialog.Builder(mAct)
            .setTitle("Delete " + ((ViewHldr) hldr).getItemName() + " @ " + mPos + " ?")
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
              @Override
              public void onCancel(DialogInterface dialog) {
                notifyItemChanged(pos);
              }
            })
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int w) {
                dialog.cancel();
                delItem(pos);
              }
            })
            .show();
        }
      }
    ).attachToRecyclerView(recVw);
  }
  @Override            // these are visible items (N + 3) on N line disp
  public ViewHldr onCreateViewHolder(ViewGroup prnt, int viewType) {
    return new ViewHldr(LayoutInflater.from(prnt.getContext()).inflate(R.layout.list_item, prnt, false));
  }
  @Override
  public void onBindViewHolder(ViewHldr hldr, int pos) {
    ContentValues cv = mNames.read(pos);
    if (cv == null) return; //-------->>>
    String name = cv.getAsString(CRUD.COL_TITL);
    if (name == null) return; //-------->>>
    hldr.setItemName(name);
    if (pos == mPos) {
      select(hldr.itemView);
    } else {
      clear(hldr.itemView);
    }
  }
  @Override
  public int getItemCount() { return mNames == null ? 0 : mNames.size(); }

  int addItem(String name) {
    int pos = mNames.create(CRUD.contVals(name));
    if (pos < 0) return -1;  //---------------->>>
    mSelVw = clear(mSelVw);
    mPos = pos;
    mLLMMgr.scrollToPosition(mPos);
    notifyItemInserted(mPos);
    return mPos;
  }
  void delItem(int pos){
    if (pos < mPos) {
      mPos--;
    } else if (pos == mPos) {
      mSelVw = clear(mSelVw);
      if (!select(mPos + 1))
        select(mPos -= 1);
    }
    mNames.delete(pos);
    notifyItemRemoved(pos);
  }
  boolean updItem(String oldName, String newName) {
    if (oldName.compareTo(newName) == 0) return false;  //----------->>>
    int pos = mNames.update(oldName, newName);
    if (pos < 0) return false; //------------------>>>
    mPos = pos;
    mLLMMgr.scrollToPosition(mPos);
    notifyDataSetChanged();
    return true;
  }

  private View clear(View vw) {
    if (vw != null)
      vw.setSelected(false);
    return null;
  }
  private boolean select(View vw) {
    if (vw != null) {
      vw.setSelected(true);
      mSelVw = vw;
      return true;
    }
    return false;
  }
  private boolean select(int pos) {
    return pos >= 0 && pos < mNames.size() && select(mLLMMgr.findViewByPosition(pos));
  }

  class ViewHldr extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private TextView mItemVw;

    ViewHldr(View llView) {  super(llView);
      llView.setClickable(true);
      llView.setOnClickListener(this);
      llView.setOnLongClickListener(this);
      mItemVw = (TextView)llView.findViewById(R.id.item_name);
    }

    void setItemName(String name) {   mItemVw.setText(name);  }
    String getItemName() {  return (String) mItemVw.getText();  }

    @Override
    public void onClick(View v) {
      int pos = getAdapterPosition();
      if (pos < 0 || pos >= mNames.size()) return;
      if (!itemView.isSelected()) {   //SELECT
        mSelVw = clear(mSelVw);
        select(mPos = pos);
      }
    }

    @Override
    public boolean onLongClick(View view) {
      final EditText edit = new EditText(mAct);
      final String oldName = getItemName();
      edit.setText(oldName);
      new AlertDialog.Builder(mAct).setTitle("Modify").setView(edit)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int w) {
            dialog.cancel();
            String newName = edit.getText().toString();
            if (updItem(oldName, newName))
              setItemName(newName);
          }
        })
        .show();
      return true;
    }

  }
}

//region REMOVED
//endregion