package com.lutech.notepad.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lutech.notepad.R;
import com.lutech.notepad.database.Database;
import com.lutech.notepad.acivity.NoteContentActivity;
import com.lutech.notepad.model.NotesModel;

import java.util.ArrayList;
import java.util.List;

public class NoteShowAdapter extends RecyclerView.Adapter<NoteShowAdapter.Note_ViewHolder> {
    List<NotesModel> modelList;
    Database database;
    private List<Integer> selectedPosition = new ArrayList<>();
    Context context ;
    boolean isBold = false;
    private boolean isSelectAllActive = false;
    private OnItemLongClick onitemLongClick ;
    private OnNoteActionListener noteActionListener;

    public NoteShowAdapter(List<NotesModel> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    public NoteShowAdapter() {
    }

    public interface OnNoteActionListener {
        void onNoteDeletedOrUndeleted();
    }
    public interface OnItemLongClick{
        void onItemLongClick();
    }

    public OnNoteActionListener getNoteActionListener() {
        return noteActionListener;
    }
    public OnItemLongClick getOnitemLongClick() {
        return onitemLongClick;
    }
    public void setNoteActionListener(OnNoteActionListener noteActionListener) {
        this.noteActionListener = noteActionListener;
    }
    public void setOnitemLongClick(OnItemLongClick onitemLongClick) {
        this.onitemLongClick = onitemLongClick;
    }


    public NoteShowAdapter(List<NotesModel> modelList, Database database) {
        this.modelList = modelList;
        this.database = database;
        notifyDataSetChanged();
    }
    public void setFilter(ArrayList<NotesModel> filteredList) {
        modelList = new ArrayList<>();
        modelList.addAll(filteredList);
        notifyDataSetChanged();
    }

    public NoteShowAdapter(List<NotesModel> modelList, List<Integer> selectedPositions) {
        this.modelList = modelList;
        this.selectedPosition = selectedPositions;
    }

    @NonNull
    @Override
    public Note_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_recycle, parent, false);
        return new Note_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Note_ViewHolder holder, int position) {
        NotesModel item = modelList.get(position);
        holder.text_title.setText(item.getTitle());
        holder.daynote.setText(item.getDay());
        holder.timenote.setText(item.getTime());
        String keyword = item.getKeyword();
        holder.itemView.setBackground(isSelected(position) ?
                ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.backgroundchange) :
                ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.background));
        if (keyword != null && !keyword.isEmpty()) {
            String title = item.getTitle();
            int startIndex = title.toLowerCase().indexOf(keyword.toLowerCase());
            if (startIndex != -1) {
                SpannableString spannableString = new SpannableString(title);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, startIndex + keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.RED), startIndex, startIndex + keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.text_title.setText(spannableString);
            } else {
                holder.text_title.setText(item.getTitle());
            }
        } else {
            holder.text_title.setText(item.getTitle());
        }
        holder.itemView.setActivated(selectedPosition.contains(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable drawable = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.backgroundchange);
                holder.itemView.setBackground(drawable);
                selectedPosition.add(holder.getAdapterPosition());
                if (onitemLongClick != null) {
                    onitemLongClick.onItemLongClick();
                }

                return true;
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isBold) {
                    Drawable drawable = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.backgroundchange);
                    holder.itemView.setBackground(drawable);
                    notifyDataSetChanged();
                } else {
                    Drawable drawable = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.background);
                    holder.itemView.setBackground(drawable);
                    toggleSelection(holder.getAdapterPosition());
                    if (item.getStatus().equals("on")) {
                        Intent intent = new Intent(view.getContext(), NoteContentActivity.class);
                        intent.putExtra("note_title", item.getTitle());
                        intent.putExtra("note_id", String.valueOf(item.getId()));
                        intent.putExtra("note_content", item.getContent());
                        view.getContext().startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage("Select an action for the note?")
                                .setCancelable(true)
                                .setPositiveButton("Undelete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        database.QueryData("UPDATE NOTE SET Status='on' WHERE Id=" + item.getId());
                                        if (noteActionListener != null) {
                                            noteActionListener.onNoteDeletedOrUndeleted();
                                        }

                                        notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        database.QueryData("DELETE FROM NOTE WHERE Id=" + item.getId());
                                        if (noteActionListener != null) {
                                            noteActionListener.onNoteDeletedOrUndeleted();
                                        }

                                        notifyDataSetChanged();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                }
                isBold = !isBold;
            }
        });

    }
    public void selectAll() {
        selectedPosition.clear(); // Xóa tất cả các vị trí đã chọn trước đó
        for (int i = 0; i < modelList.size(); i++) {
            selectedPosition.add(i); // Thêm tất cả các vị trí vào danh sách đã chọn
        }
        notifyDataSetChanged(); // Thông báo cho Adapter là dữ liệu đã thay đổi
    }

    public boolean isSelected(int position) {
        return selectedPosition.contains(position);
    }

    public void toggleSelection(int position) {
        if (selectedPosition.contains(position)) {
            selectedPosition.remove(Integer.valueOf(position)); // Xóa vị trí khỏi danh sách
        } else {
            selectedPosition.add(position); // Thêm vị trí vào danh sách
        }
        notifyItemChanged(position); // Thông báo cho Adapter là dữ liệu đã thay đổi
    }


    public void deleteSelectedItems() {
        List<NotesModel> deletedItems = new ArrayList<>();
        for (int i : selectedPosition) {
            NotesModel deletedItem = modelList.get(i);
            database.QueryData("DELETE FROM NOTE WHERE Id=" + deletedItem.getId());
            deletedItems.add(deletedItem);
        }
        modelList.removeAll(deletedItems); // Xóa các mục khỏi danh sách dữ liệu
        selectedPosition.clear(); // Xóa tất cả các vị trí đã chọn
        notifyDataSetChanged(); // Thông báo cho Adapter là dữ liệu đã thay đổi
        if (noteActionListener != null) {
            noteActionListener.onNoteDeletedOrUndeleted();
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class Note_ViewHolder extends RecyclerView.ViewHolder{
        TextView text_title, daynote, timenote;
        public Note_ViewHolder(@NonNull View itemView) {
            super(itemView);
            text_title = itemView.findViewById(R.id.text_title);
            daynote = itemView.findViewById(R.id.day_note);
            timenote = itemView.findViewById(R.id.time_note);
        }
    }
}