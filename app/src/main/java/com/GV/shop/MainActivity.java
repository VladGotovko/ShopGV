package com.GV.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.GV.shop.Model.Users;
import com.GV.shop.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button joinButton;
    private Button loginButton;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
joinButton=(Button) findViewById(R.id.main_join_btn);
loginButton=(Button) findViewById(R.id.main_login_btn);
        loadingBar=new ProgressDialog(this);


        Paper.init(this);

loginButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }
});
joinButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent registrIntent=new Intent(MainActivity.this,RegistrActivity.class);
        startActivity(registrIntent);
    }
});
String UserPhoneKey=Paper.book().read(Prevalent.UserPhoneKey);
String UserPasswordKey=Paper.book().read(Prevalent.UserPasswordKey);
if(UserPhoneKey!=""&& UserPasswordKey !=""){
    if(!TextUtils.isEmpty(UserPhoneKey)&&!TextUtils.isEmpty(UserPasswordKey)){
        ValidateUser(UserPhoneKey,UserPasswordKey);
        loadingBar.setTitle("Вход в приложение");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
    }
}
    }

    private void ValidateUser(final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotnapshot) {
                if(dataSnapshotnapshot.child("Users").child(phone).exists()){
                    Users usersData=dataSnapshotnapshot.child("Users").child(phone).getValue(Users.class);
                    if(usersData.getPhone().equals(phone)){
                        if(usersData.getPassword().equals(password)){
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                            Intent homeIntent=new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(homeIntent);
                        }
                        else{
                            loadingBar.dismiss();
                        }

                    }
                }
                else{
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Аккаунт с номером"+" "+phone+" "+"не существует ", Toast.LENGTH_SHORT).show();
                    Intent registrIntent=new Intent(MainActivity.this,RegistrActivity.class);
                    startActivity(registrIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseErrorrror) {

            }
        });
    }
}