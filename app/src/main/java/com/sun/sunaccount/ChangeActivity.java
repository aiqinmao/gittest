package com.sun.sunaccount;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangeActivity extends AppCompatActivity {

    private EditText et_class=null;
    private EditText et_account=null;
    private EditText et_password=null;
    private String class_v;
    private String account_v;
    private String password_v;
    private DatabaseHelper dbhelper=null;
    private SQLiteDatabase db=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        dbhelper=new DatabaseHelper(this,"myaccount");//第二个参数为数据库的名字
        db=dbhelper.getWritableDatabase();

        et_class=(EditText)findViewById(R.id.et_ch_class);
        et_account=(EditText)findViewById(R.id.et_ch_account);
        et_password=(EditText)findViewById(R.id.et_ch_password);
        Intent intent=getIntent();
        class_v=intent.getStringExtra("class");
        account_v=intent.getStringExtra("account");
        password_v=intent.getStringExtra("password");

        et_class.setText(class_v);
        et_account.setText(account_v);
        et_password.setText(password_v);

    }
    public void affirm_ch_func(View view)
    {
        String class_temp=et_class.getText().toString();
        String account_temp=et_account.getText().toString();
        String password_temp=et_password.getText().toString();

        if(class_temp.isEmpty()||account_temp.isEmpty()||password_temp.isEmpty())
        {
            Toast.makeText(this,"不能出现空项!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if((!class_temp.equals(class_v))||(!account_temp.equals(account_v)))//类别和账号至少有一个被修改了
            {
                if(data_is_exist(class_temp,account_temp))
                {
                    Toast.makeText(this,"数据库中已经存在该数据!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //先删除旧的
                    db.delete("myaccount","class = ? and account = ?",new String[]{class_v,account_v});
                    //然后增加新的
                    ContentValues contentValues=new ContentValues();
                    contentValues.put("class",class_temp);
                    contentValues.put("account",account_temp);
                    contentValues.put("password",password_temp);
                    db.insert("myaccount",null,contentValues);
                    Toast.makeText(this,"修改成功!",Toast.LENGTH_SHORT).show();
                }
            }
            else if(!password_temp.equals(password_v))//仅仅密码被修改了
            {
                ContentValues cv = new ContentValues();
                cv.put("password",password_temp);
                db.update("myaccount",cv,"class = ? and account = ?", new String[]{class_v,account_v});
                Toast.makeText(this,"修改成功!",Toast.LENGTH_SHORT).show();
            }
            else//啥都没有改
            {

            }
        }
        finish();
    }
    public void delete_ch_func(View view)
    {
        db.delete("myaccount","class = ? and account = ?",new String[]{class_v,account_v});
        Toast.makeText(this,"删除成功!",Toast.LENGTH_SHORT).show();
        finish();
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
}
