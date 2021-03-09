package com.android.tallyman;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    GridView gridView;
    int currentCount = 0;
    SQLiteDatabase db;
    static TallyCard Card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        gridView = findViewById(R.id.SampleGridView);
        db = openOrCreateDatabase("tally_man", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS tallys(tally_id VARCHAR, tally_label VARCHAR, date_created VARCHAR, count int);");
        findViewById(R.id.fab).setOnClickListener(v -> add());
        getCards();
    }

    class CustomAdapter extends BaseAdapter {

        Context mContext;
        List<TallyCard> list;

        public CustomAdapter(List<TallyCard> list, Context context) {
            this.list = list;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View grid;
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TallyCard tc = list.get(position);
            if (convertView == null) {
                grid = layoutInflater.inflate(R.layout.menu_item, null);
                TextView label = grid.findViewById(R.id.tally_label);
                label.setText(tc.getLabel());
                TextView count = grid.findViewById(R.id.tally_count);
                count.setText(tc.getCount() + "");
                TextView from = grid.findViewById(R.id.tally_date_created);
                from.setText("created on : " + tc.getDateCreated());
                TextView remove = grid.findViewById(R.id.remove_btn);
                remove.setOnClickListener(v -> remove(list.get(position).getId()));
                RelativeLayout relativeLayout = grid.findViewById(R.id.item);
                relativeLayout.setOnClickListener(v -> {
                    Card = list.get(position);
                    startActivity(new Intent(getApplicationContext(), TallyActivity.class));
                    finish();
                });
            } else {
                grid = convertView;
            }
            return grid;
        }
    }

    private void add() {
        Dialog dialog = new Dialog(MenuActivity.this);
        dialog.setContentView(R.layout.label_getter);
        dialog.show();
        dialog.findViewById(R.id.add_btn).setOnClickListener(v -> {
            EditText editText = dialog.findViewById(R.id.tally_label);
            String label = editText.getText().toString().trim();
            String id = System.currentTimeMillis() + "";
            String date = getDate();
            if (!label.trim().toLowerCase().contains("drop") && label.trim().length() != 0) {
                db.execSQL("INSERT INTO tallys VALUES('" + id + "','" + label + "','" + date + "'," + currentCount + ");");
                Toast.makeText(this, "Added with id : " + id, Toast.LENGTH_SHORT).show();
                getCards();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Try different name", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getCards() {
        Cursor cursor = db.rawQuery("SELECT * from tallys;", null);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            List<TallyCard> list = new ArrayList<>();
            for(int i = 0; i < cursor.getCount(); i++){
                String id = cursor.getString(0);
                String label = cursor.getString(1);
                String date = cursor.getString(2);
                int currentCount = cursor.getInt(3);
                TallyCard tc = new TallyCard(id, label, date, currentCount);
                list.add(tc);
                cursor.moveToNext();
            }
            CustomAdapter adapter = new CustomAdapter(list, MenuActivity.this);
            gridView.setAdapter(adapter);

        } else {
            gridView.setAdapter(null);
            Toast.makeText(this, "No items!", Toast.LENGTH_SHORT).show();
            System.out.println("no cards added");
        }
        if (!cursor.isClosed())
            cursor.close();
    }

    private String getDate() {
        String date = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date obj = new Date();
        date = formatter.format(obj);
        return date;
    }

    private void remove(String id) {
        db.execSQL("DELETE FROM tallys WHERE tally_id = '" + id + "';");
        getCards();
    }
}