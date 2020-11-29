package com.sun.sunaccount;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InquireActivity extends ListActivity {

    private EditText et_classfind=null;
    private DatabaseHelper dbhelper=null;
    private SQLiteDatabase db=null;
    List<Map<String,Object>> findresult =null ;
    Map<String, Object> map = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire);

//        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        if(actionBar != null)
//        {
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
        et_classfind=(EditText)findViewById(R.id.et_classfind);
        dbhelper=new DatabaseHelper(this,"myaccount");//第二个参数为数据库的名字
        db=dbhelper.getWritableDatabase();
        findresult=new ArrayList<Map<String, Object>>();

        et_classfind.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!et_classfind.getText().toString().isEmpty())
                    classfind_func(null);//实时查询 add by sun 2018-11-11
            }
        });
    }
    public void classfind_func(View view)
    {
        String stringtofind=et_classfind.getText().toString();
        if(stringtofind.isEmpty())
            Toast.makeText(this,"查找的内容不能为空!",Toast.LENGTH_SHORT).show();
        else
        {
            //查找前要清空之前查找出的显示结果
            findresult.clear();//add by sun 2018-11-11
            //query(表名,列名,...)
            //Cursor cursor=db.query("myaccount",new String[]{"class"},"class like ?",new String[]{"%"+stringtofind+"%"},null,null,null);
            Cursor cursor=db.rawQuery("select * from myaccount where class like ?",new String[]{"%"+stringtofind+"%"});

            if(view!=null)
            {
                if(!cursor.moveToNext())
                    Toast.makeText(this,"无匹配结果!",Toast.LENGTH_SHORT).show();
            }


            while(cursor.moveToNext())
            {
                map=new HashMap<String, Object>();
                String classname=cursor.getString(cursor.getColumnIndex("class"));
                String accountvalue=cursor.getString(cursor.getColumnIndex("account"));
                String passwordvalue=cursor.getString(cursor.getColumnIndex("password"));
                map.put("class",classname);
                map.put("account",accountvalue);
                map.put("password",passwordvalue);
                findresult.add(map);
            }
            SimpleAdapter adapter = new SimpleAdapter(this,findresult,R.layout.mylist,
                    new String[]{"class","account","password"},
                    new int[]{R.id.tv_mylist_class,R.id.tv_mylist_account,R.id.tv_mylist_password});
            this.setListAdapter(adapter);
            cursor.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
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
        String classvalue=(String)findresult.get(position).get("class");
        String accountvalue=(String)findresult.get(position).get("account");
        String passwordvalue=(String)findresult.get(position).get("password");

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
