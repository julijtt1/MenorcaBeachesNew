package org.android.menorcabeaches;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText userName, name, email, password;
    Button register;
    TextView txt_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.username);
        name = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);

        auth = FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                String str_userName = userName.getText().toString();
                String str_name = name.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_userName) || TextUtils.isEmpty(str_name) || TextUtils.isEmpty(str_email)|| TextUtils.isEmpty(str_password)){
                    Toast.makeText(RegisterActivity.this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
                } else if (str_password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "The password is too short", Toast.LENGTH_SHORT).show();
                } else {
                    register(str_userName, str_name, str_email, str_password);
                }

            }
        });
    }
    private void  register(final String user, final String name, String email, String password){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener( RegisterActivity.this, new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String iduser = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(iduser);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", iduser);
                    hashMap.put("user", user.toLowerCase());
                    hashMap.put("name", name);
                    hashMap.put("description", "");
                    hashMap.put("img_path", "gs://platjesmenorca.appspot.com/user.png");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        }
                    });
                } else {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}