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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nuddein.mezunuygulamasi.R;


public class GirisActivity extends AppCompatActivity {

    EditText edt_email_giris, edt_sifre_giris;

    Button btn_girisYap;

    TextView txt_KayitSayfasinaGit;

    FirebaseAuth girisYetkisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        edt_email_giris = findViewById(R.id.edit_Email_giris);
        edt_sifre_giris = findViewById(R.id.edit_Sifre_giris);
        btn_girisYap = findViewById(R.id.btn_Giris_activity);
        txt_KayitSayfasinaGit = findViewById(R.id.txt_kayitSayfasi_git);

        girisYetkisi = FirebaseAuth.getInstance();

        txt_KayitSayfasinaGit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GirisActivity.this, KaydolActivity.class));
            }
        });
        btn_girisYap. setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog pdGiris = new ProgressDialog(GirisActivity.this);
                pdGiris.setMessage("giriş yapılıyor..");
                pdGiris.show();



                String str_emailGiris = edt_email_giris.getText().toString();
                String str_sifreGiris = edt_sifre_giris.getText().toString();

                if(TextUtils.isEmpty(str_emailGiris) || TextUtils.isEmpty(str_sifreGiris)){

                    Toast.makeText(GirisActivity.this, "Tüm alanları doldurunuz", Toast.LENGTH_LONG).show();
                }
                else{
                    // Giris yapma

                    girisYetkisi.signInWithEmailAndPassword(str_emailGiris,str_sifreGiris)
                            .addOnCompleteListener(GirisActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        DatabaseReference yol_giris = FirebaseDatabase.getInstance().getReference().
                                                child("Kullanicilar").child(girisYetkisi.getCurrentUser().getUid());

                                        yol_giris.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                pdGiris.dismiss();

                                                Intent intent =   new Intent(GirisActivity.this,AnaSayfaActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                                pdGiris.dismiss();
                                            }
                                        });
                                    }
                                    else{
                                        pdGiris.dismiss();
                                        Toast.makeText(GirisActivity.this, "Giriş başarısız", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }








            }
        });
    }
}