package net.finch.fastcontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
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

    String[] cNames;
    public  final int P_LABEL = 0;
    public  final int P_NUMBER = 1;

    // коллекция для групп
    ArrayList<Map<String, String>> groupData;

    // коллекция для элементов одной группы
    ArrayList<Map<String, String>> childDataItem;

    // общая коллекция для коллекций элементов
    ArrayList<ArrayList<Map<String, String>>> childData;
    // в итоге получится childData = ArrayList<childDataItem>

    // список атрибутов группы или элемента
    Map<String, String> m;

    final int REQUEST_CODE_PERMISSIONS = 3284;
    public  volatile ArrayList<Contact> allContacts;
    public ArrayList<Contact> searchContacts;

    ExpandableListView elvMain;
    SearchView sv;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        elvMain = findViewById(R.id.elv);
        sv = findViewById(R.id.sv);
        rv = findViewById(R.id.rv);

//        SearchManager sManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        sv.setSearchableInfo(sManager.getSearchableInfo(getComponentName()));
        sv.setIconifiedByDefault(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            listCreate();
        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE}, REQUEST_CODE_PERMISSIONS);
        }

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        elvMain.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int namePos,   int phonePos, long id) {
                Log.d(TAG, "onChildClick groupPosition = " + namePos +
                        " childPosition = " + phonePos +
                        " id = " + id);
                String tel = "tel:" + searchContacts.get(namePos).getPhoneByIndex(phonePos)[P_NUMBER];
                String toast = "Контакт: " +  searchContacts.get(namePos).getName() + " (" + searchContacts.get(namePos).getPhoneByIndex(phonePos)[P_NUMBER] + ") добавлен на рабочий экран.";
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_LONG).show();
                Intent cIntent = new Intent(Intent.ACTION_CALL, Uri.parse(tel));

                ShortcutManager sm = MainActivity.this.getSystemService(ShortcutManager.class);
                if (sm.isRequestPinShortcutSupported()) {
                    Icon ico = Icon.createWithResource(MainActivity.this, R.mipmap.ic_contact_circle);
                    if (searchContacts.get(namePos).getPhotoUrl() != null) {
                        try {ico = Icon.createWithBitmap(MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), Uri.parse(searchContacts.get(namePos).getPhotoUrl())));}
                        catch (IOException e) {e.printStackTrace();}
                    }

                    ShortcutInfo pinSI = new ShortcutInfo.Builder(MainActivity.this, searchContacts.get(namePos).getName())
                            .setIntent(cIntent)
                            .setShortLabel(searchContacts.get(namePos).getName())
                            .setIcon(ico)
                            .build();

                    Intent callbackIntent = sm.createShortcutResultIntent(pinSI);
                    PendingIntent succCallback = PendingIntent.getBroadcast(MainActivity.this, 0, callbackIntent, 0);
                    sm.requestPinShortcut(pinSI, succCallback.getIntentSender());
                }



                return false;
            }
        });


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

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        handleIntent(intent);
//    }



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

    private void updateList(ArrayList<Contact> contacts) {
        if (contacts == null){
            Toast.makeText(MainActivity.this, "У вас нет контактов которые можно добавить на главный экран", Toast.LENGTH_LONG).show();
        }else {
            cNames = new String[contacts.size()];
            for (int i = 0; i< contacts.size(); i++) {
                cNames[i] = contacts.get(i).getName();
            }


            // заполняем коллекцию групп из массива с названиями групп
            groupData = new ArrayList<Map<String, String>>();
            for (String name : cNames) {
                // заполняем список атрибутов для каждой группы
                m = new HashMap<String, String>();
                m.put("groupName", name); // имя компании
                groupData.add(m);
            }

            // список атрибутов групп для чтения
            String[] groupFrom = new String[] {"groupName"};
            // список ID view-элементов, в которые будет помещены атрибуты групп
            int[] groupTo = new int[] {R.id.tvNames};


            // создаем коллекцию для коллекций элементов
            childData = new ArrayList<ArrayList<Map<String, String>>>();

            for (int i = 0; i< contacts.size(); i++) {
                childData.add(contacts.get(i).getPhonesMap("phones"));
            }

            // список атрибутов элементов для чтения
            String[] childFrom = new String[] {"phones"};
            // список ID view-элементов, в которые будет помещены атрибуты элементов
            int[] childTo = new int[] {R.id.tvPhones};

            SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                    this,
                    groupData,
                    R.layout.item_names,
                    groupFrom,
                    groupTo,
                    childData,
                    R.layout.item_phones,
                    childFrom,
                    childTo);

            elvMain.setAdapter(adapter);
        }
    }

    private  void updRecycler(ArrayList<Contact> contacts) {
        RVAdapter rvAdapter = new RVAdapter(this, contacts);
        rv.setAdapter(rvAdapter);
    }

    private void updTreeView(ArrayList<Contact> contacts) {
        TreeNode root = TreeNode.root();

        for (int nameId=0; nameId<contacts.size(); nameId++) {
            TreeNode group = new TreeNode(contacts.get(nameId)).setViewHolder(new NameViewHolder(this));
            for (int phoneId=0; phoneId<contacts.get(nameId).getPhones().size(); phoneId++) {
                TreeNode item = new TreeNode(contacts.get(nameId).getPhoneByIndex(phoneId)).setViewHolder(new PhoneViewHolder(this));
                group.addChild(item);
            }
            root.addChild(group);
        }

        AndroidTreeView tView = new AndroidTreeView(this, root);
        final ViewGroup containerView = (ViewGroup) findViewById(R.id.llContainer);
        containerView.removeAllViews();
        containerView.addView(tView.getView());
    }
}