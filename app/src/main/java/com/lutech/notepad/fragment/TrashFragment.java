package com.lutech.notepad.fragment;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lutech.notepad.R;
import com.lutech.notepad.database.Database;
import com.lutech.notepad.adapter.NoteShowAdapter;
import com.lutech.notepad.model.NotesModel;

import java.util.ArrayList;

public class TrashFragment extends Fragment  implements NoteShowAdapter.OnNoteActionListener {
    Database database;
    ArrayList<NotesModel> mlist ;
    RecyclerView recyclerView;
    NoteShowAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment__trash, container, false);
        recyclerView = view.findViewById(R.id.trash_layout);
        database = new Database(getActivity(), "note.sqlite", null, 2);
        mlist = new ArrayList<>();
        GetDataJob();
        adapter = new NoteShowAdapter(mlist , database);
        adapter.setNoteActionListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
    private void GetDataJob(){
        Cursor dataNote = database.GetData("SELECT * FROM NOTE WHERE Status = 'off'");
        mlist.clear();
        while (dataNote.moveToNext()){
            int id = dataNote.getInt(0);
            String name = dataNote.getString(1);
            String note = dataNote.getString(2);
            String day = dataNote.getString(3);
            String time = dataNote.getString(4);
            String Status = dataNote.getString(5);
            mlist.add(new NotesModel(id, name, note,day, time,Status));
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNoteDeletedOrUndeleted() {
        GetDataJob();
    }
}