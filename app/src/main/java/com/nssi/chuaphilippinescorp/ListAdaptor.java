package com.nssi.chuaphilippinescorp;

import android.content.Context;
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

public class ListAdaptor extends RecyclerView.Adapter<ListAdaptor.ViewHolder> {

    ArrayList<ItemModel> list;
    ArrayList<String> selectedItem = new ArrayList<>();
    Context context;
    int pos = -1;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    public ListAdaptor(ArrayList<ItemModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ListAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdaptor.ViewHolder holder, int position) {
        holder.barcode.setWidth(270);
        holder.quantity.setWidth(100);

        ItemModel itemModel = list.get(position);

        holder.barcode.setText(itemModel.getBarcode());
        holder.quantity.setText(itemModel.getQuantity());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView barcode, quantity;
        TableLayout table;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            table = itemView.findViewById(R.id.table);
            barcode = itemView.findViewById(R.id.txtBarcode);
            quantity = itemView.findViewById(R.id.txtQuantity);

        }
    }
}
