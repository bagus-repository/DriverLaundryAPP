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
import laundry.driveraslijempol.activity.MapsActivity;
import laundry.driveraslijempol.activity.PickupActivity;
import laundry.driveraslijempol.model.Pickup;

/**
 * Created by Bagus on 08/08/2018.
 */
public class AdapterListPickup extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Pickup> pickupList;
    private Context ctx;

    public AdapterListPickup(Context context, List<Pickup> list){
        this.pickupList = list;
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
        final Pickup pickup = pickupList.get(position);
        if (holder instanceof OriginalViewHolder){
            OriginalViewHolder viewHolder = (OriginalViewHolder) holder;
            viewHolder.no_order.setText("No Order #"+String.valueOf(pickup.id_order));
            String desc = "Nama Pelanggan : "+pickup.nama_pelanggan+"\n";
            desc += "Tanggal Pickup : "+pickup.tgl_pickup+"\n";
            desc += "Jam Pickup : "+pickup.jam_pickup+"\n";
            desc += "Alamat : "+pickup.alamat+"\n";
            viewHolder.description.setText(desc);
            viewHolder.btnMaps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, MapsActivity.class);
                    intent.putExtra("lat", pickup.lat);
                    intent.putExtra("lng", pickup.lng);
                    intent.putExtra("title", pickup.nama_pelanggan);
                    intent.putExtra("alamat", pickup.alamat);
                    ctx.startActivity(intent);
                }
            });
            viewHolder.btnDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, PickupActivity.class);
                    intent.putExtra("id", pickup.id_order);
                    ctx.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return pickupList.size();
    }
}
