package com.sun.sunaccount;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.M)
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class LoginActivity extends AppCompatActivity {

    FingerprintManager manager;
    KeyguardManager mKeyManager;
    ImageView iv_fingerprint=null;
    private final static int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        iv_fingerprint=(ImageView)findViewById(R.id.iv_fingerprint);
        manager = (FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);

        if(isFinger())
        {
            Toast.makeText(this,"请进行指纹识别",Toast.LENGTH_LONG).show();
            startListening(null);
        }
        else
        {
            showAuthenticationScreen();//使用屏幕手势解锁
        }
    }

    public boolean isFinger()
    {
        //android studio上,没有这个会报错
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)!=PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,"没有指纹识别权限!",Toast.LENGTH_SHORT).show();
            return false;
        }
        //判断是否开启锁屏密码
        if(!mKeyManager.isKeyguardSecure())
        {
            Toast.makeText(this,"没有开启锁屏密码!",Toast.LENGTH_SHORT).show();
            return false;
        }
        //判断硬件是否支持指纹识别
        if(!manager.isHardwareDetected())
        {
            Toast.makeText(this,"没有指纹识别模块!",Toast.LENGTH_SHORT).show();
            return false;
        }
        //判断是否有指纹录入
        if(!manager.hasEnrolledFingerprints())
        {
            Toast.makeText(this,"没有录入指纹!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    CancellationSignal mCancellationSignal = new CancellationSignal();
    //回调方法
    FingerprintManager.AuthenticationCallback mSelfCancelled = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString)
        {
            //多次指纹密码验证错误后进入此方法,并且不能短时间内调用指纹验证
            Toast.makeText(LoginActivity.this, errString, Toast.LENGTH_SHORT).show();
            showAuthenticationScreen();//启用屏幕密码解锁
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString)
        {
            Toast.makeText(LoginActivity.this, helpString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result)
        {
            iv_fingerprint.setBackground(getDrawable(R.mipmap.fingerprint_pass));
            Toast.makeText(LoginActivity.this,"指纹识别成功!",Toast.LENGTH_SHORT).show();
            //通过
            loginForPass();
        }

        @Override
        public void onAuthenticationFailed()
        {
            iv_fingerprint.setBackground(getDrawable(R.mipmap.fingerprint_error));
            Toast.makeText(LoginActivity.this,"指纹识别失败!",Toast.LENGTH_SHORT).show();
        }
    };
    public void startListening(FingerprintManager.CryptoObject cryptoObject)
    {
        //android studio上,没有这个会报错
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)!= PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,"没有指纹识别权限!",Toast.LENGTH_SHORT).show();
            return;
        }
        manager.authenticate(cryptoObject, mCancellationSignal, 0, mSelfCancelled, null);
    }
    private void showAuthenticationScreen()
    {
        Intent intent = mKeyManager.createConfirmDeviceCredentialIntent("finger","开启锁屏密码");
        if(intent != null)
        {
            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS)
        {
            if(resultCode == RESULT_OK)
            {
                loginForPass();
            }
        }
    }
    private void loginForPass()
    {
        Intent intent=new Intent();
        intent.setClass(this, MainActivity.class);
        this.startActivity(intent);
        finish();
    }
}
