package net.finch.fastcontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Contact> contacts;

    RVAdapter(Context context, ArrayList<Contact> contacts) {
        this.contacts = contacts;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_names, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RVAdapter.ViewHolder holder, int position) {
        Contact c = contacts.get(position);


        holder.tvName.setText(c.getName());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvPhone;
        boolean isGroup = true; // группа = true, подгруппа = false;
        boolean expended = false; // группа растянута = true;

        ViewHolder(View view){
            super(view);
            tvName = view.findViewById(R.id.tvNames);
            tvPhone = view.findViewById(R.id.tvPhones);
        }

        public void setExpended(boolean expended) {
            this.expended = expended;
        }

        public void setGroup(boolean group) {
            isGroup = group;
        }
    }
}
