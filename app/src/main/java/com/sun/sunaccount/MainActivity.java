package com.sun.sunaccount;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    static final String des_key="qazwsxedcrfvtgbyhnujmik,";//24位密钥
    private Button btn_browse=null;
    private Button btn_inquire=null;
    private Button btn_add=null;
    private Button btn_import=null,btn_export=null;
    private DatabaseHelper dbhelper=null;
    private SQLiteDatabase db=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_browse=(Button)findViewById(R.id.btn_browse);
        btn_inquire=(Button)findViewById(R.id.btn_inquire);
        btn_add=(Button)findViewById(R.id.btn_add);
        btn_import=(Button)findViewById(R.id.btn_import);
        btn_export=(Button)findViewById(R.id.btn_export);
        dbhelper=new DatabaseHelper(this,"myaccount");//第二个参数为数据库的名字
        db=dbhelper.getWritableDatabase();
    }
    public void browse_func(View view)
    {
        Intent intent=new Intent();
        intent.setClass(this, BrowseActivity.class);
        MainActivity.this.startActivity(intent);
    }
    public void inquire_func(View view)
    {
        Intent intent=new Intent();
        intent.setClass(this, InquireActivity.class);
        MainActivity.this.startActivity(intent);
    }
    public void add_func(View view)
    {
        Intent intent=new Intent();
        intent.setClass(this, AddActivity.class);
        MainActivity.this.startActivity(intent);
    }
    public void import_func(View view)
    {
        //弹出警告对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("警告");
        builder.setMessage("导入外部数据将清空当前数据库!");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                import_from_XML();//开始导入数据
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);//false按返回键不能退出 true(默认)按返回键会退出
        dialog.show();
    }
    public void export_func(View view)
    {
        Cursor cursor=db.rawQuery("select * from myaccount",null);

        try
        {
            String enter = System.getProperty("line.separator");//换行
            File file=new File(Environment.getExternalStorageDirectory(),"sunAccount.xml");
            FileOutputStream fos=new FileOutputStream(file/*,false*/);//true追加模式 false覆盖模式(默认)
            //获取一个序列化工具
            XmlSerializer serializer= Xml.newSerializer();
            serializer.setOutput(fos,"utf-8");
            //设置文件头
            serializer.startDocument("utf-8",true);

            serializer.text(enter);//插入换行符

            while(cursor.moveToNext())
            {
                String classname=cursor.getString(cursor.getColumnIndex("class"));
                String accountvalue=cursor.getString(cursor.getColumnIndex("account"));
                String passwordvalue=cursor.getString(cursor.getColumnIndex("password"));

                try
                {
                    classname=DESede.encrypt3DES(classname.getBytes(),des_key.getBytes());
                    accountvalue=DESede.encrypt3DES(accountvalue.getBytes(),des_key.getBytes());
                    passwordvalue=DESede.encrypt3DES(passwordvalue.getBytes(),des_key.getBytes());
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(this,"加密失败",Toast.LENGTH_LONG).show();
                }

                serializer.startTag(null,"Account");

                serializer.startTag(null,"class");
                serializer.text(classname);
                serializer.endTag(null,"class");

                serializer.startTag(null,"account");
                serializer.text(accountvalue);
                serializer.endTag(null,"account");

                serializer.startTag(null,"password");
                serializer.text(passwordvalue);
                serializer.endTag(null,"password");

                serializer.endTag(null,"Account");
                serializer.text(enter);//插入换行符

            }
            serializer.endDocument();
            fos.close();
            Toast.makeText(this,"导出成功",Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this,"导出失败",Toast.LENGTH_LONG).show();
        }
        cursor.close();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
    public void import_from_XML()
    {
        db.delete("myaccount",null,null);//导入前先清空表中所有数据
        try
        {
            get_choose_path();
            File path=new File(Environment.getExternalStorageDirectory(),"sunAccount.xml");
            FileInputStream fis=new FileInputStream(path);
            //获得pull解析器对象
            XmlPullParser parser=Xml.newPullParser();
            //指定解析的文件和编码格式
            parser.setInput(fis,"utf-8");

            int eventType=parser.getEventType();//获得事件类型

            String class_s=null,account=null,password=null;
            while(eventType!=XmlPullParser.END_DOCUMENT)
            {
                String tagname=parser.getName();//获取当前节点的名称

                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                        if("class".equals(tagname))
                            class_s=parser.nextText();
                        else if ("account".equals(tagname))
                            account=parser.nextText();
                        else if("password".equals(tagname))
                            password=parser.nextText();
                        break;
                    case XmlPullParser.END_TAG:
                        if("Account".equals(tagname))
                        {
                            try
                            {
                                class_s=DESede.decrypt3DES(class_s,des_key.getBytes());
                                account=DESede.decrypt3DES(account,des_key.getBytes());
                                password=DESede.decrypt3DES(password,des_key.getBytes());
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                                Toast.makeText(this,"解密失败",Toast.LENGTH_LONG).show();
                            }

                            ContentValues contentValues=new ContentValues();
                            contentValues.put("class",class_s);
                            contentValues.put("account",account);
                            contentValues.put("password",password);
                            db.insert("myaccount",null,contentValues);//"myaccount"表名

//                            System.out.println("class="+class_s);
//                            System.out.println("account="+account);
//                            System.out.println("password="+password);
                        }
                        break;
                    default:break;
                }
                eventType=parser.next();//获取下一个事件类型
            }
            fis.close();
            Toast.makeText(this,"导入成功",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this,"导入失败",Toast.LENGTH_SHORT).show();
        }
    }
    public void get_choose_path()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try
        {
            startActivityForResult(intent.createChooser(intent,"Choose File"),1);
        }catch (ActivityNotFoundException e)
        {
            Toast.makeText(MainActivity.this,"err!", Toast.LENGTH_SHORT).show();
        }


        //Toast.makeText(this,filestring,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Toast.makeText(MainActivity.this,"onActivityResult", Toast.LENGTH_SHORT).show();
        if (resultCode == Activity.RESULT_OK)//是否选择,没选择就不会继续
        {
            Toast.makeText(MainActivity.this,"", Toast.LENGTH_SHORT).show();
            Uri uri = data.getData();//得到uri,后面就是将uri转化成file的过程。
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = getContentResolver().query(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor.getString(actual_image_column_index);
            File file = new File(img_path);
            Toast.makeText(MainActivity.this, file.toString(), Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
