package com.android.tallyman;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class TallyActivity extends AppCompatActivity {

    GridView gridView;
    int currentCount = 0;
    SQLiteDatabase db;
    String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally);
        gridView = findViewById(R.id.SampleGridView);
        if(MenuActivity.Card == null)
            finish();
        id = MenuActivity.Card.getId();
        TextView textView = findViewById(R.id.label);
        textView.setText(MenuActivity.Card.getLabel());
        db = openOrCreateDatabase("tally_man", MODE_PRIVATE, null);
        //db.execSQL("DROP TABLE tallys");
        findViewById(R.id.fab).setOnClickListener(v -> add());
        getValues();
    }

    void add() {
        currentCount += 1;
        db.execSQL("UPDATE tallys SET count = "+ currentCount +" WHERE tally_id = '" + id + "';");
        getValues();
    }

    void getValues() {
        gridView.setAdapter(null);
        Cursor cursor = db.rawQuery("SELECT count from tallys WHERE tally_id = '" + id + "';", null);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            populateGrid(cursor.getInt(0));
        } else {
            currentCount = 0;
            System.out.println("no values");
        }
        if (!cursor.isClosed())
            cursor.close();
    }

    void populateGrid(int count) {
        currentCount = count;
        int units = count / 5;
        int singleLines = count % 5;
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < units; i++) {
            list.add(5);
        }
        list.add(singleLines); // add remaining count
        CustomAdapter adapter = new CustomAdapter(list, TallyActivity.this);
        gridView.setAdapter(adapter);
    }

    class CustomAdapter extends BaseAdapter {

        Context mContext;
        List<Integer> list;

        public CustomAdapter(List<Integer> list, Context context) {
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

            int type = list.get(position);

            if (convertView == null) {
                grid = layoutInflater.inflate(R.layout.unit, null);
                LinearLayout layout;
                switch (type) {
                    case 1:
                        layout = grid.findViewById(R.id.one);
                        layout.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        layout = grid.findViewById(R.id.two);
                        layout.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        layout = grid.findViewById(R.id.three);
                        layout.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        layout = grid.findViewById(R.id.four);
                        layout.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        layout = grid.findViewById(R.id.unit);
                        layout.setVisibility(View.VISIBLE);
                        break;
                }
            } else {
                grid = convertView;
            }
            return grid;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        finish();
    }
}