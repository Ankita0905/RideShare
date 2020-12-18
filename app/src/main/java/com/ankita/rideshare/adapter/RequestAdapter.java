package com.ankita.rideshare.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ankita.rideshare.R;
import com.ankita.rideshare.bean.RequestUser;

import java.util.List;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.Holder>{

    private List<RequestUser> items;
    private OnItemClickListener listener;

    public RequestAdapter(List<RequestUser> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        RequestUser model = items.get(position);
        holder.tvRiderName.setText(model.name);
        holder.tvRiderEmail.setText(model.email);

        holder.tvAccept.setOnClickListener(v -> {
            listener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }


    public class Holder extends RecyclerView.ViewHolder {
        private final TextView tvRiderName, tvRiderEmail, tvAccept;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tvRiderName = itemView.findViewById(R.id.tvRiderName);
            tvRiderEmail = itemView.findViewById(R.id.tvRiderEmail);
            tvAccept = itemView.findViewById(R.id.tvAccept);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
