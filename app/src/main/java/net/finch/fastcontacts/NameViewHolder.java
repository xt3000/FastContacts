package net.finch.fastcontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

public class NameViewHolder extends TreeNode.BaseNodeViewHolder<Contact> {


    public NameViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, Contact cont) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.name_group_layout, null, false);

        TextView tvNameGroup = view.findViewById(R.id.tv_nameGroup);
        tvNameGroup.setText(cont.getName());

        return view;
    }
}
