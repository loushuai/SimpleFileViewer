package com.example.loushuai.simpleplayer;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class MainActivity extends ListActivity {
    public static final String COLUMN_NAME_NAME = "name";

    private SimpleAdapter adapter = null;
    private List<Map<String, Object>> itemList;
    private Stack<String> pathHistory = null;
    private String curPath;

    String[] from = { COLUMN_NAME_NAME } ;
    int[] to = {R.id.myView1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pathHistory = new Stack<String>();

        String sDStateString = Environment.getExternalStorageState();
        if(sDStateString.equals(Environment.MEDIA_MOUNTED)) {
            File SDFile = Environment.getExternalStorageDirectory();
            curPath = SDFile.getAbsolutePath() + "/";
            itemList = getData(curPath);
            adapter = new SimpleAdapter(this, itemList, R.layout.list_item, from, to);
            setListAdapter(adapter);
        }
    }

    private List<Map<String, Object>> getData(String path) {
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(COLUMN_NAME_NAME, "..");
        list.add(map);

        File file = new File(path);
        if (file.listFiles().length > 0) {
            for (File f : file.listFiles()) {
                map = new HashMap<String, Object>();
                String name = f.getName();
                if (f.isDirectory()) {
                    name += "/";
                }
                map.put(COLUMN_NAME_NAME, name);
                list.add(map);

                Log.d("Scan", " path " + path + f.getName());
            }
        }

        return list;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String path;
        if (position > 0) {
            pathHistory.push(curPath);
            path = curPath + itemList.get(position).get(COLUMN_NAME_NAME);
        } else { // uplevel
            if (!pathHistory.empty())
                path = pathHistory.pop();
            else // root
                path = curPath;
        }
        Log.d("List View Click", " position: " + position + " name: " + path);

        File file = new File(path);
        if (file.isDirectory()) {
            updateList(path);
            curPath = path;
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), itemList.get(position).get(COLUMN_NAME_NAME) + " is a file", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void updateList(String path) {
        itemList.clear();
        itemList = getData(path);
        adapter = new SimpleAdapter(this, itemList, R.layout.list_item, from, to);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
