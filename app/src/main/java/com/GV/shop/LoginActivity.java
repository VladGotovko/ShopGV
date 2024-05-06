package com.GV.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.GV.shop.Model.Users;
import com.GV.shop.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog loadingBar;
    private Button loginBtn;
    private EditText phoneInput,passwordInput;
    private String parentDbName="Users";
    private CheckBox checkBoxRememberMe;
    private TextView AdminLink, NotAdminLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn=(Button) findViewById(R.id.login_btn);
        phoneInput=(EditText) findViewById(R.id.login_phone_input);
        passwordInput=(EditText) findViewById(R.id.login_password_input);
        loadingBar=new ProgressDialog(this);
        checkBoxRememberMe=(CheckBox)findViewById(R.id.login_checkbox);
        Paper.init(this);
        AdminLink=(TextView)findViewById(R.id.admin_panel_link);
        NotAdminLink=(TextView)findViewById(R.id.not_admin_panel_link);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                loginBtn.setText("Вход для администратора");
                parentDbName="Admins";
            }
        });
        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                loginBtn.setText("Войти");
                parentDbName="Users";
            }
        });
    }

    private void loginUser() {
        String phone=phoneInput.getText().toString();
        String password=passwordInput.getText().toString();
        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this,"Введите номер телефона",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Введите пароль",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Вход в приложение");
            loadingBar.setMessage("Пожалуйста, подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUser(phone,password);
        }
    }

    private void ValidateUser(String phone, String password) {
        if (checkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotnapshot) {
                if(dataSnapshotnapshot.child(parentDbName).child(phone).exists()){
Users usersData=dataSnapshotnapshot.child(parentDbName).child(phone).getValue(Users.class);
if(usersData.getPhone().equals(phone)){
    if(usersData.getPassword().equals(password)){
        if(parentDbName.equals("Users")){
            loadingBar.dismiss();
            Toast.makeText(LoginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
            Intent homeIntent=new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(homeIntent);
        }
        else if(parentDbName.equals("Admins")){
            loadingBar.dismiss();
            Toast.makeText(LoginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
            Intent homeIntent=new Intent(LoginActivity.this,AdminAddNewProductActivity.class);
            startActivity(homeIntent);
        }
    }
    else{
        loadingBar.dismiss();
        Toast.makeText(LoginActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
    }

}
                }
                else{
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Аккаунт с номером"+" "+phone+" "+"не существует ", Toast.LENGTH_SHORT).show();
                    Intent registrIntent=new Intent(LoginActivity.this,RegistrActivity.class);
                    startActivity(registrIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseErrorrror) {

            }
        });
    }
}