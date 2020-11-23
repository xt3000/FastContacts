package net.finch.fastcontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

public class PhoneViewHolder extends TreeNode.BaseNodeViewHolder<String[]> {
    public PhoneViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, String[] phone) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.phones_items_layout, null, false);

        TextView tvPL = view.findViewById(R.id.tv_phoneLabel);
        tvPL.setText(phone[0]);

        TextView tvPN = view.findViewById(R.id.tv_phoneNum);
        tvPN.setText(phone[1]);

        return view;
    }
}