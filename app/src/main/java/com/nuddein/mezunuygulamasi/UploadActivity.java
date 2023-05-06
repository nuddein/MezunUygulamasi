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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.nuddein.mezunuygulamasi.databinding.ActivityUploadBinding;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class UploadActivity extends AppCompatActivity {

    private ActivityUploadBinding binding;

    private FirebaseStorage firebaseStorage;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    String benimUrim = "";

    StorageTask yuklemeGorevi;
    StorageReference resimYukleYolu;

    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    EditText edt_Gonderi_Hakkinda;

    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        edt_Gonderi_Hakkinda = findViewById(R.id.edt_Gonderi_Hakkinda);
        firebaseFirestore = FirebaseFirestore.getInstance();
        resimYukleYolu = FirebaseStorage.getInstance().getReference("gonderiler");
    }

    public void uploadButtonClicked(View view) {

        if (imageData != null) {
            resimYukle();

        }

    }

    private String dosyaUzantisiAl(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void resimYukle() {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("gonderiliyor..");
        progressDialog.show();

        if (imageData != null) {
            StorageReference dosyaYolu = resimYukleYolu.child(System.currentTimeMillis()
                    + "." + dosyaUzantisiAl(imageData));

            yuklemeGorevi = dosyaYolu.putFile(imageData);
            yuklemeGorevi.continueWithTask(new Continuation() {

                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return dosyaYolu.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri indirmeUrisi = task.getResult();
                        benimUrim = indirmeUrisi.toString();

                        DatabaseReference veriYolu = FirebaseDatabase.getInstance().getReference("Gonderiler");

                        String gonderiId = veriYolu.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("gonderiId", gonderiId);
                        hashMap.put("gonderiResmi", benimUrim);
                        hashMap.put("gonderiHakkinda", edt_Gonderi_Hakkinda.getText().toString());
                        hashMap.put("gonderen", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        veriYolu.child(gonderiId).setValue(hashMap);
                        progressDialog.dismiss();

                        startActivity(new Intent(UploadActivity.this, AnaSayfaActivity.class));
                        finish();

                    } else {
                        Toast.makeText(UploadActivity.this, "Gonderme Basarisiz", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "secilen resim yok", Toast.LENGTH_SHORT).show();
        }
    }

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


    public void selectImageFromGallery(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 33 ve ustu READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {

                    Snackbar.make(view, "Permission needed", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                            //Request permission
                        }
                    }).show();

                } else {

                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
                //request
            } else {
                //gallery'e git
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                activityResultLauncher.launch(intentToGallery);
            }

        } else {

            // Android version 32 ve alti ise READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    Snackbar.make(view, "Permission needed", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            //Request permission

                        }
                    }).show();

                } else {

                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                }
                //request
            } else {
                //gallery'e git
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


                activityResultLauncher.launch(intentToGallery);

            }
        }
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

    public void showImageSourceSelectionDialog(View view) {
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

    private void registerLauncher() {

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();

                        if (imageData == null && intentFromResult.hasExtra("data")) {
                            selectedImage = (Bitmap) intentFromResult.getExtras().get("data");
                            imageData = getImageUri(getApplicationContext(), selectedImage);
                        }

                        // binding.imageView.setImageURI(imageData);
                        try {

                            if (Build.VERSION.SDK_INT > 28) {

                                // versiyon 28den buyuklerde createSource

                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            } else {
                                // Version 28den kucukse

                                selectedImage = MediaStore.Images.Media.getBitmap(UploadActivity.this.getContentResolver(), imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

                if (result) {
                    //izin verildi

                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    activityResultLauncher.launch(intentToGallery);

                } else {
                    //izin verilmedi

                    Toast.makeText(UploadActivity.this, "izin verilmedi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}


