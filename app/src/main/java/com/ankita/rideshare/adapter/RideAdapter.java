package com.ankita.rideshare.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ankita.rideshare.R;
import com.ankita.rideshare.activity.RideDetailActivity;
import com.ankita.rideshare.bean.RideBean;

import java.util.List;


public class RideAdapter extends RecyclerView.Adapter<RideAdapter.Holder>{

    private List<RideBean> items;

    public RideAdapter(List<RideBean> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RideAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        RideBean model = items.get(position);
        holder.tvRiderName.setText(model.name);
        holder.tvRideDate.setText(model.dateTime);
        holder.tvEstTime.setText("Est Time(hrs) : "+model.estimateTime);
        holder.tvEndPoint.setText("To : "+model.destPlace);
        holder.tvStartPoint.setText("From : "+model.originPlace);
        holder.tvSendRequest.setOnClickListener(v -> {
            RideDetailActivity.start(holder.tvSendRequest.getContext(), model);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }


    public class Holder extends RecyclerView.ViewHolder {
        private final TextView tvRiderName, tvRideDate, tvEstTime, tvEndPoint ,tvStartPoint, tvSendRequest;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tvRiderName = itemView.findViewById(R.id.tvRiderName);
            tvRideDate = itemView.findViewById(R.id.tvRideDate);
            tvEstTime = itemView.findViewById(R.id.tvEstTime);
            tvEndPoint = itemView.findViewById(R.id.tvEndPoint);
            tvStartPoint = itemView.findViewById(R.id.tvStartPoint);
            tvSendRequest = itemView.findViewById(R.id.tvSendRequest);
        }
    }

    public void update(List<RideBean> newItems){
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }
}
