package net.finch.fastcontacts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import java.io.IOException;

public class NameViewHolder extends TreeNode.BaseNodeViewHolder<Contact> {


    public NameViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, Contact cont) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.name_group_layout, null, false);

        TextView tvNameGroup = view.findViewById(R.id.tv_nameGroup);
        tvNameGroup.setText(cont.getName());

        ImageView ivAvatar = view.findViewById(R.id.iv_avotar);
        Icon ico = Icon.createWithResource(context, R.mipmap.ic_contact_circle);
        if (cont.getPhotoUrl() != null) {
            try {
                ico = Icon.createWithBitmap(MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(cont.getPhotoUrl())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivAvatar.setImageIcon(ico);

        return view;
    }
}
