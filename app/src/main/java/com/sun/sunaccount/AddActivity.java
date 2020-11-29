package com.sun.sunaccount;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {

    private EditText et_class=null;
    private EditText et_account=null;
    private EditText et_password=null;
    private DatabaseHelper dbhelper=null;
    private SQLiteDatabase db=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        et_class=(EditText)findViewById(R.id.et_class);
        et_account=(EditText)findViewById(R.id.et_account);
        et_password=(EditText)findViewById(R.id.et_password);
        dbhelper=new DatabaseHelper(this,"myaccount");//第二个参数为数据库的名字
        db=dbhelper.getWritableDatabase();
    }
    public void affirm_func(View view)
    {
        String newclass=et_class.getText().toString();
        String newaccount=et_account.getText().toString();
        String newpassword=et_password.getText().toString();
        if(newclass.isEmpty()||newaccount.isEmpty()||newpassword.isEmpty())
        {
            Toast.makeText(this,"不能出现空项!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(data_is_exist(newclass,newaccount))
            {
                Toast.makeText(this,"数据库中已经存在该数据!",Toast.LENGTH_SHORT).show();
            }
            else
            {
                ContentValues contentValues=new ContentValues();
                contentValues.put("class",newclass);
                contentValues.put("account",newaccount);
                contentValues.put("password",newpassword);
                db.insert("myaccount",null,contentValues);//"myaccount"表名
                Toast.makeText(this,"保存成功!",Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }
    private boolean data_is_exist(String newclass,String newaccount)
    {
        boolean trueorfalse=true;
        Cursor cursor=db.rawQuery("select * from myaccount where class like ? and account like ?",new String[]{newclass,newaccount});

        if(cursor.moveToNext())//存在
            trueorfalse=true;
        else
            trueorfalse=false;

        cursor.close();
        return trueorfalse;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
