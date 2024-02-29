package com.lutech.notepad.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lutech.notepad.R;
import com.lutech.notepad.adapter.NoteShowAdapter;
import com.lutech.notepad.model.CategoriesModel;
import com.lutech.notepad.adapter.EditCategoriesAdapter;
import com.lutech.notepad.database.Database;

import java.util.ArrayList;


public class EditCategoriesFragment extends Fragment implements EditCategoriesAdapter.CheckListenerChanged{

    EditText name_categories;
    Button btnadd;
    RecyclerView reclyview_edit_categories;
    ArrayList<CategoriesModel> mlist;
    Database database;
    EditCategoriesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_categories, container, false);
        database = new Database(getActivity(), "note.sqlite", null, 2);
        name_categories = view.findViewById(R.id.name_categories);
        btnadd = view.findViewById(R.id.btn_addCategories);
        reclyview_edit_categories = view.findViewById(R.id.reclyview_edit_categories);
        mlist = new ArrayList<>();

        adapter = new EditCategoriesAdapter(mlist, getContext(), database);
        GetCatagori();
        adapter.setCheckListenerChanged(this);
        reclyview_edit_categories.setAdapter(adapter);
        reclyview_edit_categories.setLayoutManager(new LinearLayoutManager(getActivity()));

        database = new Database(getContext(), "note.sqlite", null, 2);


        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name =  name_categories.getText().toString();
                if(isCategoryExists(name)) {
                    Toast.makeText(getContext(), "Categories alredy exist", Toast.LENGTH_SHORT).show();
                }
                else if(name.equals("")|| name == null) {
                    Toast.makeText(getContext(), "Categories is empty", Toast.LENGTH_SHORT).show();
                }else {
                    database.QueryData("INSERT INTO Categories (Id, Name ) VALUES (null, '" + name + "');");
                    listener.onAddButtonClicked();
                    GetCatagori();
                    adapter.notifyDataSetChanged();
                    if (listener != null) {
                        listener.onAddButtonClicked();
                    }
                }


            }
        });
        return view;
    }

    @Override
    public void onCheckChange() {
        adapter.notifyDataSetChanged();
        GetCatagori();

    }


    public interface OnAddButtonClickListener {
        void onAddButtonClicked();
    }

    private OnAddButtonClickListener listener;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnAddButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnAddButtonClickListener");
        }
    }
    public void GetCatagori(){
        Cursor dataCate = database.GetData("SELECT * FROM Categories");
        mlist.clear();
        if (dataCate!=null){
            while (dataCate.moveToNext()){
                int id = dataCate.getInt(0);
                String name = dataCate.getString(1);
                mlist.add(new CategoriesModel(id , name));
            }

        }
        adapter.notifyDataSetChanged();
    }
    public boolean isCategoryExists(String categoryName) {
        Cursor dataCate = database.GetData("SELECT * FROM Categories WHERE Name= '" + categoryName + "'");
        if (dataCate != null && dataCate.moveToFirst()) {
            dataCate.close(); // Đóng con trỏ dữ liệu
            return true;
        }
        return false;
    }


}