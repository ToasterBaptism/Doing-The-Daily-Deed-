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
import com.worktracker.dailydeed.database.DatabaseHelper;
import com.worktracker.dailydeed.models.Profile;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {
    
    private Context context;
    private List<Profile> profiles;
    private OnProfileClickListener listener;
    private DatabaseHelper databaseHelper;
    
    public interface OnProfileClickListener {
        void onLoadClick(Profile profile);
        void onDeleteClick(Profile profile);
    }
    
    public ProfileAdapter(Context context, List<Profile> profiles) {
        this.context = context;
        this.profiles = profiles;
        this.databaseHelper = new DatabaseHelper(context);
    }
    
    public void setOnProfileClickListener(OnProfileClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false);
        return new ProfileViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Profile profile = profiles.get(position);
        
        holder.tvProfileName.setText(profile.getName());
        
        int entryCount = databaseHelper.getEntryCountForProfile(profile.getName());
        String profileInfo = "Created: " + profile.getFormattedCreatedDate() + 
                           " | Entries: " + entryCount;
        holder.tvProfileInfo.setText(profileInfo);
        
        // Disable delete button for Default profile
        if ("Default".equals(profile.getName())) {
            holder.btnDeleteProfile.setEnabled(false);
            holder.btnDeleteProfile.setAlpha(0.5f);
        } else {
            holder.btnDeleteProfile.setEnabled(true);
            holder.btnDeleteProfile.setAlpha(1.0f);
        }
        
        holder.btnLoadProfile.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLoadClick(profile);
            }
        });
        
        holder.btnDeleteProfile.setOnClickListener(v -> {
            if (listener != null && !"Default".equals(profile.getName())) {
                listener.onDeleteClick(profile);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return profiles.size();
    }
    
    public void updateProfiles(List<Profile> newProfiles) {
        this.profiles = newProfiles;
        notifyDataSetChanged();
    }
    
    public void removeProfile(int position) {
        profiles.remove(position);
        notifyItemRemoved(position);
    }
    
    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView tvProfileName, tvProfileInfo;
        Button btnLoadProfile, btnDeleteProfile;
        
        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProfileName = itemView.findViewById(R.id.tvProfileName);
            tvProfileInfo = itemView.findViewById(R.id.tvProfileInfo);
            btnLoadProfile = itemView.findViewById(R.id.btnLoadProfile);
            btnDeleteProfile = itemView.findViewById(R.id.btnDeleteProfile);
        }
    }
}