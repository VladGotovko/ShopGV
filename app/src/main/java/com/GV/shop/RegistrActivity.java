package com.GV.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.net.InternetDomainName;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class RegistrActivity extends AppCompatActivity {
    private Button registrBtn;
    private EditText usernameInput, phoneInput, PasswordInput;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registr);
        registrBtn= (Button) findViewById(R.id.registr_btn);
        usernameInput= (EditText) findViewById(R.id.registr_username_input);
        phoneInput= (EditText) findViewById(R.id.registr_phone_input);
        PasswordInput= (EditText) findViewById(R.id.registr_password_input);
        loadingBar=new ProgressDialog(this);
        registrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String username=usernameInput.getText().toString();
        String phone=phoneInput.getText().toString();
        String password=PasswordInput.getText().toString();
        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Введите имя",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this,"Введите номер телефона",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Введите пароль",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Создание аккаунта");
            loadingBar.setMessage("Пожалуйста, подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhone(username,phone,password);
        }
    }

    private void ValidatePhone(String username, String phone, String password) {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String, Object> userDataMap=new HashMap<>();
                    userDataMap.put("phone",phone);
                    userDataMap.put("username",username);
                    userDataMap.put("password",password);
                    RootRef.child("Users").child(phone).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                loadingBar.dismiss();
                                Toast.makeText(RegistrActivity.this, "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show();
                                Intent loginIntent=new Intent(RegistrActivity.this,LoginActivity.class);
                                startActivity(loginIntent);
                            }
                            else {
                                loadingBar.dismiss();
                                Toast.makeText(RegistrActivity.this, "Ошибка при регистрации", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    loadingBar.dismiss();
                    Toast.makeText(RegistrActivity.this,"Номер"+ " " + phone +" "+" уже зарегистрирован",Toast.LENGTH_SHORT).show();
                    Intent loginIntent=new Intent(RegistrActivity.this,LoginActivity.class);
                    startActivity(loginIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}