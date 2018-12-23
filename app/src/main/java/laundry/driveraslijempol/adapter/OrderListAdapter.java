package laundry.driveraslijempol.adapter;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import laundry.driveraslijempol.R;
import laundry.driveraslijempol.model.OrderItems;
import laundry.driveraslijempol.utils.MySingleton;
import laundry.driveraslijempol.utils.Tools;

/**
 * Created by Bagus on 26/07/2018.
 */

public class OrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;

    private List<OrderItems> orderItems;
    private Context mContext;
    private Context appContext;

    private ItemQtyListener itemQtyListener;

    public OrderListAdapter(Context context, Context appContext, List<OrderItems> orderItemsList, Integer cQty) {
        this.orderItems = orderItemsList;
        this.mContext = context;
        this.appContext = appContext;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;
        public TextView name;
        public View lyt_parent;
        public TextView desc;
        public FloatingActionButton fab_sub;
        public TextView item_qty;
        public FloatingActionButton fab_add;
        public ItemQtyListener itemQtyListener;

        public OriginalViewHolder(View v, ItemQtyListener itemQtyListener){
            super(v);
            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.name);
            desc = v.findViewById(R.id.description);
            fab_sub = v.findViewById(R.id.fab_qty_sub);
            item_qty = v.findViewById(R.id.item_qty);
            fab_add = v.findViewById(R.id.fab_qty_add);

            fab_sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qty = Integer.parseInt(item_qty.getText().toString());
                    if(qty>0){
                        qty--;
                        item_qty.setText(qty+"");
                    }
                }
            });

            fab_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qty = Integer.parseInt(item_qty.getText().toString());
                    if(qty < 99){
                        qty++;
                        item_qty.setText(qty+"");
                    }
                }
            });

            this.itemQtyListener = itemQtyListener;
            item_qty.addTextChangedListener(this.itemQtyListener);

            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder{
        public TextView title_section;

        public SectionViewHolder(View v){
            super(v);
            title_section = v.findViewById(R.id.title_section);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if(viewType == VIEW_ITEM){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wash_list, parent, false);
            vh = new OriginalViewHolder(v, new ItemQtyListener());
        }else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            vh = new SectionViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OrderItems o = orderItems.get(position);
        if(holder instanceof OriginalViewHolder){
            ((OriginalViewHolder) holder).name.setText(o.itemTitle);
            ((OriginalViewHolder) holder).desc.setText(o.itemDesc);
            Tools.displayImageRound(mContext, ((OriginalViewHolder) holder).image, o.imageUrl);
            ((OriginalViewHolder) holder).item_qty.setText(String.valueOf(o.itemQty));
            ((OriginalViewHolder) holder).itemQtyListener.updatePosition(position);
            ((OriginalViewHolder) holder).setIsRecyclable(false);
        }else {
            SectionViewHolder view = (SectionViewHolder) holder;
            view.title_section.setText(o.itemTitle);
        }

    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.orderItems.get(position).section ? VIEW_SECTION : VIEW_ITEM;
    }

    private class ItemQtyListener implements TextWatcher{
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!orderItems.get(position).section){
                OrderItems o = orderItems.get(position);
                String getTitle = o.itemTitle;
                Integer index = Arrays.asList(MySingleton.getInstance(appContext).getPakaianOrder()).indexOf(getTitle);
//                Log.d("position item", position+"-"+editable.toString());
                MySingleton.getInstance(appContext).setJmlpakaianOrderByPos(index, Integer.parseInt(editable.toString()));
                o.itemQty = Integer.parseInt(editable.toString());
//                Log.d("qty", String.valueOf(o.itemQty));
                orderItems.set(position, o);
            }
        }
    }
}
