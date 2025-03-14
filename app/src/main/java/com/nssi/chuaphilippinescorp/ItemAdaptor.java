package com.nssi.chuaphilippinescorp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemAdaptor extends RecyclerView.Adapter<ItemAdaptor.ViewHolder> {

    ArrayList<ItemModel> list;
    ArrayList<String> selectedItem = new ArrayList<>();
    Context context;
    int pos = -1;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    public ItemAdaptor(ArrayList<ItemModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdaptor.ViewHolder holder, int position) {
        holder.dateTime.setWidth(Globalclass.for_all);
        holder.barcode.setWidth(Globalclass.for_all);
        holder.quantity.setWidth(Globalclass.for_all);

        ItemModel itemModel = list.get(position);

        holder.dateTime.setText(itemModel.getDateStamp());
        holder.barcode.setText(itemModel.getBarcode());
        holder.quantity.setText(itemModel.getQuantity());

        holder.table.setOnClickListener(v -> {
            if (Globalclass.selectedIds.contains(itemModel.getId()))
                Globalclass.selectedIds.remove(itemModel.getId());
            else
                Globalclass.selectedIds.add(itemModel.getId());
//            Log.d("TAG", "onBindViewHolder: " + Globalclass.selectedIds);
            notifyDataSetChanged();
        });

        int c = (Globalclass.selectedIds.contains(itemModel.getId())
                ? R.drawable.selected
                : R.drawable.back2
        );
        holder.dateTime.setBackgroundResource(c);
        holder.barcode.setBackgroundResource(c);
        holder.quantity.setBackgroundResource(c);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView barcode, quantity, dateTime;
        TableLayout table;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            table = itemView.findViewById(R.id.table);
            barcode = itemView.findViewById(R.id.txtbarcode);
            quantity = itemView.findViewById(R.id.txtquantity);
            dateTime = itemView.findViewById(R.id.txtdateTime);

        }
    }

}
