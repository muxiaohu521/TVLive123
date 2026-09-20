package com.github.tvbox.osc.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.LiveSourceItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LiveSourceAdapter extends RecyclerView.Adapter<LiveSourceAdapter.ViewHolder> {

    private OnSourceActionListener listener;
    private String currentName;
    private List<LiveSourceItem> data = new ArrayList<>();

    public interface OnSourceActionListener {
        void onSelect(LiveSourceItem item);
        void onEdit(LiveSourceItem item);
        void onDelete(LiveSourceItem item);
    }

    public LiveSourceAdapter(OnSourceActionListener listener) {
        this.listener = listener;
    }

    public void setCurrentName(String name) {
        this.currentName = name;
        notifyDataSetChanged();
    }

    public void setData(List<LiveSourceItem> data) {
        this.data = new ArrayList<>(data);
        Collections.sort(this.data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvUrl;
        TextView btnEdit;
        TextView btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSourceName);
            tvUrl = itemView.findViewById(R.id.tvSourceUrl);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_source, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LiveSourceItem item = data.get(position);
        String prefix = item.getName().equals(currentName) ? "√ " : "";
        holder.tvName.setText(prefix + item.getName());
        holder.tvUrl.setText(item.getUrl());

        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSelect(item);
                }
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEdit(item);
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDelete(item);
                }
            }
        });
    }
}