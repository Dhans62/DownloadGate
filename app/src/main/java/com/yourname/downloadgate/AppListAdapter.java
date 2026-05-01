package com.yourname.downloadgate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView adapter untuk daftar app whitelist.
 * TODO: Implementasi penuh di fase UI build.
 */
public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    public static class AppItem {
        public final String name;
        public final String packageName;
        public boolean enabled;

        public AppItem(String name, String packageName, boolean enabled) {
            this.name        = name;
            this.packageName = packageName;
            this.enabled     = enabled;
        }
    }

    private final List<AppItem> items;
    private final OnToggleListener listener;

    public interface OnToggleListener {
        void onToggle(AppItem item, boolean enabled);
    }

    public AppListAdapter(List<AppItem> items, OnToggleListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO: inflate item_app.xml
        View v = new View(parent.getContext());
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // TODO: bind data ke view
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View v) { super(v); }
    }
}
