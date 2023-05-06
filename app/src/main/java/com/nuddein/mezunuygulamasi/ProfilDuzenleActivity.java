package com.nuddein.mezunuygulamasi;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.nuddein.mezunuygulamasi.databinding.ActivityProfilDuzenleBinding;
import java.util.HashMap;
import Model.Kullanici;

import java.io.ByteArrayOutputStream;
public class ProfilDuzenleActivity extends AppCompatActivity {

     private ActivityProfilDuzenleBinding binding;

    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    FirebaseUser mevcutKullanici;

    ImageView resim_kapatma;
    TextView txt_kaydet, txt_fotograf_degistir;

    EditText edit_Ad, edit_kullaniciAdi, edit_biografi, edit_girisYili, edit_mezunYili,
            edit_mail, edit_telNo;

   EditText ulke1, sehir1, sirket1;



    CheckBox chechbox_lisans, chechbox_yuksek_lisans, chechbox_doktora;

   // private StorageReference storageReference;
    String benimUrim = "";

    StorageTask yuklemeGorevi;

    StorageReference depolamaYolu;


    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String>  permissionLauncher;

    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfilDuzenleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        resim_kapatma = findViewById(R.id.kapat_resmi_profilDuzenleActivity);
        //resim_profil = findViewById(R.id.profil_resmi_profilDuzenleActivity);
        //Textviewler

        txt_kaydet = findViewById(R.id.txt_kaydet_profilDuzenleActivity);
        txt_fotograf_degistir = findViewById(R.id.txt_degistir);

        //EditTextler

        edit_Ad = findViewById(R.id.edit_text_Ad_profilDuzenleActivity);
        edit_kullaniciAdi = findViewById(R.id.edit_text_KullaniciAdi_profilDuzenleActivity);

        edit_biografi = findViewById(R.id.edit_text_Biyografi_profilDuzenleActivity);

        edit_girisYili = findViewById(R.id.edit_text_girisYili_profilDuzenleActivity);
        edit_mezunYili = findViewById(R.id.edit_text_mezunYili_profilDuzenleActivity);

        //edit_isBilgileri = findViewById(R.id.edit_text_isBilgileri_profilDuzenleActivity);

        ulke1 = findViewById(R.id.edit_ulke);
        sehir1 = findViewById(R.id.edit_sehir);
        sirket1 = findViewById(R.id.edit_sirket);

        edit_mail = findViewById(R.id.edit_text_mailAdresi_profilDuzenleActivity);

        edit_telNo = findViewById(R.id.edit_text_telNo_profilDuzenleActivity);

        chechbox_lisans = findViewById(R.id.checkbox_lisans_profilDuzenleActivity);
        chechbox_yuksek_lisans = findViewById(R.id.checkbox_yuksek_lisans_profilDuzenleActivity);
        chechbox_doktora = findViewById(R.id.checkbox_doktora_profilDuzenleActivity);


        //silsilsil




        mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();

        depolamaYolu = FirebaseStorage.getInstance().getReference("yuklemeler");


        Button changePasswordButton = findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });





        DatabaseReference kullaniciYolu = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(mevcutKullanici.getUid());

        kullaniciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanici kullanici = snapshot.getValue(Kullanici.class);

                edit_Ad.setText(kullanici.getAd());
                edit_kullaniciAdi.setText(kullanici.getKullaniciadi());

                edit_girisYili.setText(kullanici.getGirisYili());
                edit_mezunYili.setText(kullanici.getMezunYili());

                edit_biografi.setText(kullanici.getBio());
                edit_mail.setText(kullanici.getMail());




                edit_telNo.setText(kullanici.getTelNo());





                Glide.with(getApplicationContext()).load(kullanici.getResimurl()).into(binding.imageView);


                resim_kapatma.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });


                txt_fotograf_degistir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                txt_kaydet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadImage();
                        profiliGuncelle(edit_Ad.getText().toString(), edit_kullaniciAdi.getText().toString(),
                                edit_biografi.getText().toString(),
                                edit_mail.getText().toString(), edit_telNo.getText().toString(),
                                edit_girisYili.getText().toString(), edit_mezunYili.getText().toString(),
                                chechbox_lisans, chechbox_yuksek_lisans, chechbox_doktora, ulke1.getText().toString(),
                                sehir1.getText().toString(), sirket1.getText().toString()) ;
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        txt_fotograf_degistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });
    }


    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_password, null);

        final EditText newPasswordEditText = view.findViewById(R.id.new_password);
        final EditText confirmNewPasswordEditText = view.findViewById(R.id.confirm_new_password);

        builder.setView(view)
                .setTitle("Şifre Değiştir")
                .setPositiveButton("Değiştir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPassword = newPasswordEditText.getText().toString();
                        String confirmNewPassword = confirmNewPasswordEditText.getText().toString();
                        changePassword(newPassword, confirmNewPassword);
                    }
                })
                .setNegativeButton("İptal", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void changePassword(String newPassword, String confirmNewPassword) {
        // Şifre doğrulama ve hata kontrolleri
        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmNewPassword)) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
        } else if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "Yeni şifreler eşleşmiyor.", Toast.LENGTH_SHORT).show();
        } else {
            // Firebase kullanıcısını alın
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                // Kullanıcının şifresini değiştir
                user.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfilDuzenleActivity.this, "Şifre başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProfilDuzenleActivity.this, "Şifre güncelleme.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "Kullanıcı oturumu açık değil.", Toast.LENGTH_SHORT).show();
            }
        }
    }












    //foto ekleme
    private void uploadImage() {
        if (imageData != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Resim yükleniyor...");
            progressDialog.show();

            final StorageReference imageReference = depolamaYolu.child(System.currentTimeMillis() + "." + dosyaUzantisiAl(imageData));

            yuklemeGorevi = imageReference.putFile(imageData);
            yuklemeGorevi.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        benimUrim = downloadUri.toString();

                        DatabaseReference guncellemeYolu = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(mevcutKullanici.getUid());
                        HashMap<String, Object> kullaniciGuncellemeHahsmap = new HashMap<>();
                        kullaniciGuncellemeHahsmap.put("resimurl", benimUrim);

                        guncellemeYolu.updateChildren(kullaniciGuncellemeHahsmap);
                        progressDialog.dismiss();

                    } else {
                        Toast.makeText(ProfilDuzenleActivity.this, "Yükleme başarısız oldu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfilDuzenleActivity.this, "Yükleme başarısız oldu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(this, "Resim seçilmedi", Toast.LENGTH_SHORT).show();
        }
    }
    private void profiliGuncelle(String ad, String kullaniciAdi,
                                 String biografi,
                                 String mail, String telNo,
                                 String girisYili, String mezunYili,
                                 CheckBox ch_lisans, CheckBox ch_yuksek_lisans,
                                 CheckBox ch_doktora, String ulke1, String sehir1, String sirket1) {

        DatabaseReference guncellemeYolu = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(mevcutKullanici.getUid());

        HashMap<String, Boolean> checkBoxEgitimDurumu = new HashMap<>();


        checkBoxEgitimDurumu.put("lisans", ch_lisans.isChecked());
        checkBoxEgitimDurumu.put("yuksek_lisans", ch_yuksek_lisans.isChecked());
        checkBoxEgitimDurumu.put("doktora", ch_doktora.isChecked());


        HashMap<String, Object> kullaniciGuncellemeHahsmap = new HashMap<>();
        kullaniciGuncellemeHahsmap.put("ad", ad);
        kullaniciGuncellemeHahsmap.put("kullaniciadi", kullaniciAdi);
        kullaniciGuncellemeHahsmap.put("bio", biografi);


        kullaniciGuncellemeHahsmap.put("ulke", ulke1);
        kullaniciGuncellemeHahsmap.put("sehir", sehir1);
        kullaniciGuncellemeHahsmap.put("sirket", sirket1);



        kullaniciGuncellemeHahsmap.put("mail", mail);
        kullaniciGuncellemeHahsmap.put("telNo", telNo);
        kullaniciGuncellemeHahsmap.put("girisYili", girisYili);
        kullaniciGuncellemeHahsmap.put("mezunYili", mezunYili);
        kullaniciGuncellemeHahsmap.put("egitimDurumu", checkBoxEgitimDurumu);

        guncellemeYolu.updateChildren(kullaniciGuncellemeHahsmap);
    }
    private String dosyaUzantisiAl (Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    public void selectImageFromCamera(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Snackbar.make(view, "Permission needed", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionLauncher.launch(Manifest.permission.CAMERA);
                    }
                }).show();
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA);
            }
        } else {
            Intent intentToCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(intentToCamera);
        }
    }







    //buraya kadar sil


    public void selectImage(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image From");
        String[] options = {"Gallery", "Camera"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    selectImageFromGallery(view);
                } else {
                    selectImageFromCamera(view);
                }
            }
        });
        builder.show();
    }






    public void selectImageFromGallery (View view){

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            // Android 33 ve ustu READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){

                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)){


                    Snackbar.make(view, "Permission needed", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                            //Request permission
                        }
                    }).show();
                }else{
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
                //request
            }
            else {
                //gallery'e git
                Intent intentToGallery  = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );

                activityResultLauncher.launch(intentToGallery);
            }

        }
        else {

            // Android version 32 ve alti ise READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){


                    Snackbar.make(view, "Permission needed", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            //Request permission

                        }
                    }).show();

                }else{

                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                }
                //request
            }
            else {
                //gallery'e git
                Intent intentToGallery  = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );

                activityResultLauncher.launch(intentToGallery);

            }
        }
    }


    private void registerLauncher (){

        activityResultLauncher  = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode()== RESULT_OK){
                    Intent intentFromResult =  result.getData();
                    if(intentFromResult!=null){
                        imageData = intentFromResult.getData();
                      // binding.imageView.setImageURI(imageData);

                        try {

                            if (Build.VERSION.SDK_INT > 28) {

                                // versiyon 28den buyuklerde createSource

                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                          else  {
                              // Version 28den kucukse

                                selectedImage = MediaStore.Images.Media.getBitmap(ProfilDuzenleActivity.this.getContentResolver(), imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

                if (result){
                    //izin verildi

                    Intent intentToGallery  = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    activityResultLauncher.launch(intentToGallery);
                }
                else {
                    //izin verilmedi
                    Toast.makeText(ProfilDuzenleActivity.this, "izin verilmedi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}