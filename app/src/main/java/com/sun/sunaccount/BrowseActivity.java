package com.sun.sunaccount;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.constraint.solver.widgets.WidgetContainer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//ListActivity
public class BrowseActivity extends ListActivity {

    private DatabaseHelper dbhelper=null;
    private SQLiteDatabase db=null;
    ///private ListView lv_allresult=null;
    List<Map<String,Object>> allresult =null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);


//        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        if(actionBar != null)
//        {
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        ///lv_allresult=(ListView)findViewById(R.id.lv_allresult);


//        lv_allresult.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            public void onItemClick (AdapterView<?> parent, View view, int position, long id)
//            {
//                AlertDialog.Builder builder=new AlertDialog.Builder(BrowseActivity.this);
//                builder.setTitle((String)parent.getItemAtPosition(position));
//                builder.setMessage("id="+parent.getItemIdAtPosition(position));
//                builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                AlertDialog dialog=builder.create();
//                dialog.show();
//            }
//        });

        dbhelper=new DatabaseHelper(this,"myaccount");//第二个参数为数据库的名字
        db=dbhelper.getWritableDatabase();
        allresult=new ArrayList<Map<String, Object>>();
        ///List<String> allresult= new ArrayList<String>();

        Map<String, Object> map = null;

        //query(表名,列名,...)
        //Cursor cursor=db.query("myaccount",new String[]{"class"},null,null,null,null,null);
        Cursor cursor=db.rawQuery("select * from myaccount",null);
        while(cursor.moveToNext())
        {
            map=new HashMap<String, Object>();
            String classname=cursor.getString(cursor.getColumnIndex("class"));
            String accountvalue=cursor.getString(cursor.getColumnIndex("account"));
            String passwordvalue=cursor.getString(cursor.getColumnIndex("password"));
            map.put("class",classname);
            map.put("account",accountvalue);
            map.put("password",passwordvalue);
            allresult.add(map);
            ///allresult.add(classname);
        }
        ///lv_allresult.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,allresult));


        SimpleAdapter adapter = new SimpleAdapter(this,allresult,R.layout.mylist,
                new String[]{"class","account","password"},
                new int[]{R.id.tv_mylist_class,R.id.tv_mylist_account,R.id.tv_mylist_password});
        this.setListAdapter(adapter);
        cursor.close();
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId())
//        {
//            case android.R.id.home:
//                this.finish();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //super.onListItemClick(l, v, position, id);
        String classvalue=(String)allresult.get(position).get("class");
        String accountvalue=(String)allresult.get(position).get("account");
        String passwordvalue=(String)allresult.get(position).get("password");

        Intent intent=new Intent();
        intent.putExtra("class",classvalue);
        intent.putExtra("account",accountvalue);
        intent.putExtra("password",passwordvalue);
        intent.setClass(this, ChangeActivity.class);
        this.startActivity(intent);
        finish();
    }
    public void btn_back_func(View view)
    {
        finish();
    }
}
