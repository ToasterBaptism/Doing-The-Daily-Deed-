package com.worktracker.dailydeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.worktracker.dailydeed.R;
import com.worktracker.dailydeed.models.WorkEntry;

import java.util.List;

public class WorkEntryAdapter extends RecyclerView.Adapter<WorkEntryAdapter.WorkEntryViewHolder> {
    
    private Context context;
    private List<WorkEntry> workEntries;
    private OnWorkEntryClickListener listener;
    
    public interface OnWorkEntryClickListener {
        void onEditClick(WorkEntry entry);
        void onDeleteClick(WorkEntry entry);
    }
    
    public WorkEntryAdapter(Context context, List<WorkEntry> workEntries) {
        this.context = context;
        this.workEntries = workEntries;
    }
    
    public void setOnWorkEntryClickListener(OnWorkEntryClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public WorkEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_work_entry, parent, false);
        return new WorkEntryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull WorkEntryViewHolder holder, int position) {
        WorkEntry entry = workEntries.get(position);
        
        holder.tvTime.setText(entry.getTime());
        holder.tvLocation.setText(entry.getLocation());
        holder.tvTaskDescription.setText(entry.getTaskDescription());
        holder.tvDuration.setText(entry.getFormattedDuration());
        
        if (entry.hasNotes()) {
            holder.tvNotes.setText(entry.getNotes());
            holder.tvNotes.setVisibility(View.VISIBLE);
        } else {
            holder.tvNotes.setVisibility(View.GONE);
        }
        
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(entry);
            }
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(entry);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return workEntries.size();
    }
    
    public void updateEntries(List<WorkEntry> newEntries) {
        this.workEntries = newEntries;
        notifyDataSetChanged();
    }
    
    public void removeEntry(int position) {
        workEntries.remove(position);
        notifyItemRemoved(position);
    }
    
    public static class WorkEntryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvLocation, tvTaskDescription, tvDuration, tvNotes;
        Button btnEdit, btnDelete;
        
        public WorkEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}