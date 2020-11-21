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

    RVAdapter(Context context, ArrayList<Contact> phones) {
        this.contacts = phones;
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

        ViewHolder(View view){
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvNames);
        }
    }
}
