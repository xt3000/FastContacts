package net.finch.fastcontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public final String TAG = "FINCH";
    public final String TAG_SEARCH = "FINCH_SEARCH";
    public final String TAG_CLICK = "FINCH_CLICK";

    final int REQUEST_CODE_PERMISSIONS = 3284;
    public  volatile ArrayList<Contact> allContacts;
    public ArrayList<Contact> searchContacts;

//    SearchView sv;
    AndroidTreeView tView;

    private Bundle sis = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sis = savedInstanceState;


//        sv = findViewById(R.id.sv);
//        sv.setIconifiedByDefault(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            listCreate();
        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE}, REQUEST_CODE_PERMISSIONS);
        }

//        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (newText.equals("")) {
//                    searchContacts = allContacts;
//                    updTreeView(searchContacts);
//                }
//                else search(newText);
//                return false;
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.length > 0) {
            boolean x = false;
            for (int res : grantResults) {
                if (res == PackageManager.PERMISSION_GRANTED) {
                    x = true;
                }else {
                    x = false;
                    Log.d(TAG, "onRequestPermissionsResult: false");
                }
            }
            if (x) {
                listCreate();
            }else {
                Toast.makeText(this, "Вы предоставили не все права доступа", Toast.LENGTH_LONG).show();
                finish();
            }

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Поиск в контактах");

        final int searchBarId = searchView.getContext().getResources().getIdentifier("android:id/search_bar", null, null);

        LinearLayout searchBar = searchView.findViewById(searchBarId);
        searchBar.setLayoutTransition(new LayoutTransition());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    searchContacts = allContacts;
                    updTreeView(searchContacts);
                }
                else search(newText);
                return false;
            }
        });

        return true;
    }

    ///  ПОЛЬЗОВАТЕЛЬСКИЕ МЕТОДЫ   ///
    private void listCreate() {
        allContacts = Contacts.getAll(this);
        searchContacts = allContacts;
        updTreeView(allContacts);
    }

    private void search(String query) {
        query = query.toLowerCase();
        searchContacts = new ArrayList<>();
        for (int i=0; i<allContacts.size(); i++) {
            if (allContacts.get(i).getName().toLowerCase().contains(query)) {
                searchContacts.add(allContacts.get(i));
            }
        }

        updTreeView(searchContacts);
    }

    private void updTreeView(ArrayList<Contact> contacts) {
        TreeNode root = TreeNode.root();

        for (int nameId=0; nameId<contacts.size(); nameId++) {
            TreeNode group = new TreeNode(contacts.get(nameId)).setViewHolder(new NameViewHolder(this));
            for (int phoneId=0; phoneId<contacts.get(nameId).getPhones().size(); phoneId++) {
                Phone phone = contacts.get(nameId).getPhoneByIndex(phoneId);
                TreeNode item = new TreeNode(phone).setViewHolder(new PhoneViewHolder(this));
                item.setClickListener(itemClickListener);
                group.addChild(item);
            }
            root.addChild(group);
        }

        tView = new AndroidTreeView(this, root);
        tView.setDefaultAnimation(true);
        final ViewGroup containerView = (ViewGroup) findViewById(R.id.llContainer);
        containerView.removeAllViews();
        containerView.addView(tView.getView());

        if (sis != null) {
            String state = sis.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }
    }


    private TreeNode.TreeNodeClickListener itemClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            Phone p = (Phone) value;
            Log.d(TAG_CLICK, "onClick: value = " + p.getLabel() + " : " + p.getNum());

            String tel = "tel:" + p.getNum();
            String toast = "Контакт: " + p.getLabel() + " (" + p.getNum() + ") добавлен на рабочий экран.";
            Intent cIntent = new Intent(Intent.ACTION_CALL, Uri.parse(tel));

            Contact contact = findContactById(p.getId());
            if (contact != null) {
                ShortcutManager sm = MainActivity.this.getSystemService(ShortcutManager.class);
                if (sm.isRequestPinShortcutSupported()) {
                    Icon ico = Icon.createWithResource(MainActivity.this, R.mipmap.ic_contact_circle);
                    if (contact.getPhotoUrl() != null) {
                        try {
                            ico = Icon.createWithBitmap(MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), Uri.parse(contact.getPhotoUrl())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    ShortcutInfo pinSI = new ShortcutInfo.Builder(MainActivity.this, contact.getName())
                            .setIntent(cIntent)
                            .setShortLabel(contact.getName())
                            .setIcon(ico)
                            .build();

                    Intent callbackIntent = sm.createShortcutResultIntent(pinSI);
                    PendingIntent succCallback = PendingIntent.getBroadcast(MainActivity.this, 0, callbackIntent, 0);
                    sm.requestPinShortcut(pinSI, succCallback.getIntentSender());

                    Toast.makeText(MainActivity.this, toast, Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private Contact findContactById(int id) {
        for (int i=0; i<allContacts.size(); i++) {
            if (allContacts.get(i).getId() == id) {
                return allContacts.get(i);
            }
        }

        return null;
    }

}