package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.LiveSourceItem;
import com.github.tvbox.osc.bean.LiveSourceManager;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.ui.adapter.LiveSourceAdapter;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LiveSourceManageDialog extends BaseDialog implements LiveSourceAdapter.OnSourceActionListener {

    private TvRecyclerView rvSourceList;
    private LiveSourceAdapter adapter;
    private OnSourceChangeListener listener;

    public LiveSourceManageDialog(@NonNull @NotNull Context context) {
        super(context, R.style.CustomDialogStyleDim);
        setContentView(R.layout.dialog_live_source_manage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rvSourceList = findViewById(R.id.rvSourceList);
        rvSourceList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new LiveSourceAdapter(this);

        List<LiveSourceItem> sourceList = LiveSourceManager.get().getSourceList();
        adapter.setData(sourceList);
        adapter.setCurrentName(LiveSourceManager.get().getCurrentSourceName());

        rvSourceList.setAdapter(adapter);
        if (!sourceList.isEmpty()) {
            rvSourceList.setSelectedPosition(0);
        }

        rvSourceList.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                if (position >= 0 && position < sourceList.size()) {
                    LiveSourceItem item = LiveSourceManager.get().getSourceList().get(position);
                    if (item != null) {
                        onSelect(item);
                    }
                }
            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(null);
            }
        });

        findViewById(R.id.btnDeleteAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LiveSourceManager.get().getSourceList().isEmpty()) {
                    Toast.makeText(getContext(), "列表已空", Toast.LENGTH_SHORT).show();
                    return;
                }
                LiveSourceManager.get().clearAll();
                List<LiveSourceItem> defaultList = LiveSourceManager.get().getDefaultSources();
                LiveSourceManager.get().saveSourceList(defaultList);
                refreshList();
                Toast.makeText(getContext(), "已恢复默认源列表", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onChange(LiveSourceManager.get().getCurrentSourceUrl());
                }
            }
        });

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        findViewById(R.id.btnRemote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoteDialog remoteDialog = new RemoteDialog(getContext());
                remoteDialog.show();
            }
        });
    }

    private void showEditDialog(final LiveSourceItem editItem) {
        LiveSourceEditDialog editDialog = new LiveSourceEditDialog(getContext());
        if (editItem != null) {
            editDialog.setEditData(editItem.getName(), editItem.getUrl());
        }
        editDialog.setOnEditListener(new LiveSourceEditDialog.OnEditListener() {
            @Override
            public void onConfirm(String name, String url) {
                if (editItem != null) {
                    LiveSourceItem newItem = new LiveSourceItem(name, url);
                    LiveSourceManager.get().updateSource(editItem, newItem);
                } else {
                    LiveSourceManager.get().addSource(new LiveSourceItem(name, url));
                }
                refreshList();
                if (listener != null) {
                    listener.onChange(LiveSourceManager.get().getCurrentSourceUrl());
                }
            }
        });
        editDialog.show();
    }

    private void refreshList() {
        List<LiveSourceItem> sourceList = LiveSourceManager.get().getSourceList();
        adapter.setData(sourceList);
        adapter.setCurrentName(LiveSourceManager.get().getCurrentSourceName());
        if (!sourceList.isEmpty()) {
            rvSourceList.setSelectedPosition(0);
        }
    }

    @Override
    public void onSelect(LiveSourceItem item) {
        LiveSourceManager.get().setCurrentSource(item);
        adapter.setCurrentName(item.getName());
        Toast.makeText(getContext(), "已切换: " + item.getName(), Toast.LENGTH_SHORT).show();
        if (listener != null) {
            listener.onChange(item.getUrl());
        }
    }

    @Override
    public void onEdit(LiveSourceItem item) {
        showEditDialog(item);
    }

    @Override
    public void onDelete(LiveSourceItem item) {
        LiveSourceManager.get().removeSource(item);
        refreshList();
        LiveSourceItem current = LiveSourceManager.get().getCurrentSource();
        if (current == null && !LiveSourceManager.get().getSourceList().isEmpty()) {
            LiveSourceManager.get().setCurrentSource(LiveSourceManager.get().getSourceList().get(0));
            adapter.setCurrentName(LiveSourceManager.get().getCurrentSourceName());
        }
        Toast.makeText(getContext(), "已删除: " + item.getName(), Toast.LENGTH_SHORT).show();
        if (listener != null) {
            listener.onChange(LiveSourceManager.get().getCurrentSourceUrl());
        }
    }

    public void setOnSourceChangeListener(OnSourceChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSourceChangeListener {
        void onChange(String newUrl);
    }
}