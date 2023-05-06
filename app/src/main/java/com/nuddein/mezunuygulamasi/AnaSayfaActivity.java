package com.nuddein.mezunuygulamasi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.nuddein.mezunuygulamasi.databinding.ActivityAnaSayfaBinding;

import cerceve.AramaFragment;
import cerceve.BildirimFragment;
import cerceve.HomeFragment;
import cerceve.ProfilFragment;

public class AnaSayfaActivity extends AppCompatActivity {

    //BottomNavigationView bottomNavigationView;
    Fragment seciliCerceve = null;
    ActivityAnaSayfaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_ana_sayfa);
        getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici, new HomeFragment()).commit();

        binding = ActivityAnaSayfaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){


                case R.id.nav_home:

                    seciliCerceve = new HomeFragment();



                   break;




                case R.id.nav_arama:

                    seciliCerceve = new AramaFragment();


                    break;




                case R.id.nav_ekle:

                    // cerceve bos olsun baska aktiviteye gondersin

                    seciliCerceve = null;
                    startActivity(new Intent(AnaSayfaActivity.this, UploadActivity.class));


                    break;



                case R.id.nav_kalp:

                    seciliCerceve = new BildirimFragment();

                    break;






                case R.id.nav_profil:
                    SharedPreferences.Editor editor = getSharedPreferences("PREFERS", MODE_PRIVATE).edit();
                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();

                    seciliCerceve = new ProfilFragment();

                    break;




            }
            if(seciliCerceve!= null){
                getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici, seciliCerceve).commit();
            }



            return true;
        });



    }

}




