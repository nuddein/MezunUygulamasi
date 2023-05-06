package com.nuddein.mezunuygulamasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;

public class KaydolActivity extends AppCompatActivity {

    EditText edit_kullanicAdi, edit_Ad, edit_Email, edit_Sifre, edit_MezunYili, edit_GirisYili;

    Button btn_Kaydol;
    TextView txt_GirisSayfasinaGit;

    FirebaseAuth auth;
    DatabaseReference databaseReference; //YOL

    ProgressDialog progressDialog;  //PD


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaydol);

        edit_kullanicAdi = findViewById(R.id.edit_KullaniciAdi);
        edit_Ad = findViewById(R.id.edit_Isim);
        edit_Email = findViewById(R.id.edit_Email);
        edit_GirisYili = findViewById(R.id.edit_GirisYili);
        edit_MezunYili = findViewById(R.id.edit_MezunYili);
        edit_Sifre = findViewById(R.id.edit_Sifre);

        btn_Kaydol = findViewById(R.id.btn_Kaydol_activity);
        txt_GirisSayfasinaGit = findViewById(R.id.txt_girisSayfasi_git);


        auth = FirebaseAuth.getInstance();


        // Zaten hesabi varsa girise yonlendiriyoruz

        txt_GirisSayfasinaGit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(KaydolActivity.this, GirisActivity.class));
            }
        });

        btn_Kaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(KaydolActivity.this);
                progressDialog.setMessage("Lütfen Bekleyin..");
                progressDialog.show();


                String str_kullaniciAdi = edit_kullanicAdi.getText().toString();
                String str_Ad = edit_Ad.getText().toString();
                String str_email = edit_Email.getText().toString();
                String str_sifre = edit_Sifre.getText().toString();
                String str_girisYili = edit_GirisYili.getText().toString();
                String str_mezunYili = edit_MezunYili.getText().toString();

                if (TextUtils.isEmpty(str_kullaniciAdi) || TextUtils.isEmpty(str_Ad)
                        || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_sifre)
                        || TextUtils.isEmpty(str_mezunYili) || TextUtils.isEmpty(str_girisYili)) {

                    Toast.makeText(KaydolActivity.this, "Tüm alanları doldurunuz", Toast.LENGTH_SHORT).show();
                } else if (str_sifre.length() < 6) {
                    Toast.makeText(KaydolActivity.this, "Şifreniz en az 6 karakter olmalı", Toast.LENGTH_SHORT).show();

                } else {

                    // Yeni Kullanici kayit etme
                    kaydet(str_kullaniciAdi,str_Ad,str_email,str_sifre,str_girisYili,str_mezunYili);

                }

                // yeni kullanici kaydetme


            }
        });
    }

    private void kaydet (final String kullaniciAdi, final String ad, String email, String sifre, String girisYili, String mezunYili)
    {
        // yeni kullanici kaydetme icin gerekli kodlar
        auth.createUserWithEmailAndPassword(email,sifre)
                .addOnCompleteListener(KaydolActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {


                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            String userId = firebaseUser.getUid();
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(userId);


                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("id", userId);
                            hashMap.put("kullaniciadi", kullaniciAdi.toLowerCase());
                            hashMap.put("ad", ad);
                            hashMap.put("bio", "");
                            hashMap.put("resimurl", "https://firebasestorage.googleapis.com/v0/b/mezunapp-5c541.appspot.com/o/placeholder.jpeg?alt=media&token=4cb67d34-cbc6-45e0-a160-3eb768baad08");
                            hashMap.put("mezunYili", mezunYili);
                            hashMap.put("girisYili", girisYili);

                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        progressDialog.dismiss();

                                        Intent intent = new Intent( KaydolActivity.this, AnaSayfaActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                        // startActivity(new Intent(KaydolActivity.this,AnaActivity.class));
                                    }
                                }
                            });
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(KaydolActivity.this, "Bu mail veya Şifre ile kayıt başarısızdır", Toast.LENGTH_LONG).show();


                        }

                    }
                });

    }
}
