package laundry.driveraslijempol.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import laundry.driveraslijempol.R;
import laundry.driveraslijempol.activity.DeliveryActivity;
import laundry.driveraslijempol.activity.MapsActivity;
import laundry.driveraslijempol.model.Delivery;

/**
 * Created by Bagus on 08/08/2018.
 */
public class AdapterListDelivery extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Delivery> deliveryList;
    private Context ctx;

    public AdapterListDelivery(Context context, List<Delivery> deliveries){
        this.deliveryList = deliveries;
        this.ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder{
        public TextView no_order;
        public TextView description;
        public Button btnMaps;
        public Button btnDetails;

        public OriginalViewHolder(View v) {
            super(v);
            no_order = v.findViewById(R.id.no_order);
            description = v.findViewById(R.id.description);
            btnMaps = v.findViewById(R.id.btnMaps);
            btnDetails = v.findViewById(R.id.btnDetails);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_order, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Delivery delivery = deliveryList.get(position);
        if (holder instanceof OriginalViewHolder){
            OriginalViewHolder viewHolder = (OriginalViewHolder) holder;
            viewHolder.no_order.setText("No Order #"+String.valueOf(delivery.id_order));
            String desc = "Nama Pelanggan : "+delivery.nama_pelanggan+"\n";
            desc += "Alamat : "+delivery.alamat+"\n";
            viewHolder.description.setText(desc);
            viewHolder.btnMaps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, MapsActivity.class);
                    intent.putExtra("lat", delivery.lat);
                    intent.putExtra("lng", delivery.lng);
                    intent.putExtra("title", delivery.nama_pelanggan);
                    intent.putExtra("alamat", delivery.alamat);
                    ctx.startActivity(intent);
                }
            });
            viewHolder.btnDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, DeliveryActivity.class);
                    intent.putExtra("id", delivery.id_order);
                    ctx.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }
}
