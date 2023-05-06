package cerceve;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nuddein.mezunuygulamasi.BaslangicActivity;
import com.nuddein.mezunuygulamasi.ProfilDuzenleActivity;
import com.nuddein.mezunuygulamasi.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import Adapter.FotografAdapter;
import Model.Gonderi;
import Model.Kullanici;


public class ProfilFragment extends Fragment {

    ImageView resimLogout, profil_resmi;
    TextView txt_gonderiler, txt_takipciler,
            txt_takipEdilenler, txt_ad, txt_bio,
            txt_kullaniciAdi, txt_girisYili, txt_telNo,
            txt_mezunYili,  txt_egitimBilgisi;


    TextView ulke, sehir, sirket;

    TextView mail;


    Button btn_profili_Duzenle;

    RecyclerView recyclerViewFotograflar;
    FotografAdapter fotografAdapter;
    List<Gonderi> gonderiList;

    FirebaseUser mevcutKullanici;


    private FirebaseAuth auth;

    String profilId;

    private CheckBox checkBoxLisans, checkBoxYuksekLisans, checkBoxDoktora;


    public ProfilFragment() {
        // Required empty public constructor
    }






    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profil, container, false);

        mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        profilId = prefs.getString("profileid", "none");


        resimLogout = view.findViewById(R.id.resimLogout_profilCercevesi);



        profil_resmi = view.findViewById(R.id.profil_resmi_profilCercevesi);

        txt_kullaniciAdi = view.findViewById(R.id.txt_kullaniciadi_profilcerceve);
        txt_ad = view.findViewById(R.id.txt_ad_profilCercevesi);
        txt_bio = view.findViewById(R.id.txt_bio_profilCercevesi);
        txt_gonderiler = view.findViewById(R.id.txt_gonderiler_profilCercevesi);
        txt_takipciler = view.findViewById(R.id.txt_takipciler_profilCercevesi);
        txt_takipEdilenler = view.findViewById(R.id.txt_takipEdilenler_profilCercevesi);

        txt_egitimBilgisi = view.findViewById(R.id.txt_egitimBilgisi_profilCercevesi);
        txt_mezunYili = view.findViewById(R.id.txt_mezunYili_profilCercevesi);
        txt_girisYili = view.findViewById(R.id.txt_girisYili_profilCercevesi);
        //txt_isBilgisi = view.findViewById(R.id.txt_isBilgisi_profilCercevesi);




        ulke = view.findViewById(R.id.txt_ulke);


        sehir = view.findViewById(R.id.txt_sehir);


        sirket = view.findViewById(R.id.txt_sirket);

        mail = view.findViewById(R.id.txt_mail_profilCercevesi);






        auth = FirebaseAuth.getInstance();


        //sil







        txt_telNo = view.findViewById(R.id.txt_telNo_profilCercevesi);


        btn_profili_Duzenle = view.findViewById(R.id.btn_profiliDuzenle_profilCercevesi);

        recyclerViewFotograflar = view.findViewById(R.id.recycler_view_profilcercevesi);

        checkBoxLisans = view.findViewById(R.id.checkbox_lisans_profilFragment);
        checkBoxYuksekLisans = view.findViewById(R.id.checkbox_yuksek_lisans_profilFragment);
        checkBoxDoktora = view.findViewById(R.id.checkbox_doktora_profilFragment);


        recyclerViewFotograflar.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerViewFotograflar.setLayoutManager(linearLayoutManager);
        gonderiList = new ArrayList<>();
        fotografAdapter = new FotografAdapter(getContext(), gonderiList);
        recyclerViewFotograflar.setAdapter(fotografAdapter);



        //Metodlaricagiriyoruz
        kullaniciBilgisi();
        takipcileriAl();
        gonderiSayisiAl();
        fotograflarim();


        if(profilId.equals(mevcutKullanici.getUid())) {

            btn_profili_Duzenle.setText("Profili Düzenle");
        }
        else{
            takipKontrolu();
        }



            resimLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    auth.signOut();


                    Intent intentToBaslangic = new Intent(getContext(), BaslangicActivity.class);
                    startActivity(intentToBaslangic);

                }
            });











        btn_profili_Duzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = btn_profili_Duzenle.getText().toString();
                if(btn.equals("Profili Düzenle")){

                    //profili duzenleme sayfasina gitmek

                    startActivity(new Intent(getContext(), ProfilDuzenleActivity.class));

                }
                else if(btn.equals("takip et"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                            .child("takipEdilenler").child(profilId).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                            .child("takipciler").child(mevcutKullanici.getUid()).setValue(true);
                }

                else if(btn.equals("takip ediliyor")){

                    FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                            .child("takipEdilenler").child(profilId).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                            .child("takipciler").child(mevcutKullanici.getUid()).removeValue();

                }

            }
        });

        return view;
    }

    private void kullaniciBilgisi() {
        DatabaseReference kullaniciYolu = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(profilId);

        kullaniciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }

                Kullanici kullanici = snapshot.getValue(Kullanici.class);

                if (kullanici != null) {
                    Glide.with(getContext()).load(kullanici.getResimurl()).into(profil_resmi);
                    txt_kullaniciAdi.setText(kullanici.getKullaniciadi());
                    txt_ad.setText(kullanici.getAd());
                    txt_bio.setText(kullanici.getBio());

                    txt_mezunYili.setText("mezun yılı: " + kullanici.getMezunYili());
                    txt_girisYili.setText("giriş yılı: " + kullanici.getGirisYili());
                    //txt_isBilgisi.setText("iş bilgileri " + kullanici.getIsBilgileri());
                    txt_telNo.setText("telefon numarası "+kullanici.getTelNo());


                    ulke.setText("ülke: "+kullanici.getUlke());
                    sehir.setText("şehir: "+ kullanici.getSehir());
                    sirket.setText("şirket: " + kullanici.getSirket());

                    mail.setText("mail: "+ kullanici.getMail());


                    // Eğitim durumunu alın ve CheckBox'ları güncelleyin

                    Map<String, Boolean> egitimDurumu = (Map<String, Boolean>) kullanici.getEgitimDurumu();

                    if (egitimDurumu != null) {
                        checkBoxLisans.setChecked(egitimDurumu.get("lisans"));
                        checkBoxYuksekLisans.setChecked(egitimDurumu.get("yuksek_lisans"));
                        checkBoxDoktora.setChecked(egitimDurumu.get("doktora"));
                    } else {
                        checkBoxLisans.setChecked(false);
                        checkBoxYuksekLisans.setChecked(false);
                        checkBoxDoktora.setChecked(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkBoxLisans.setEnabled(false);
        checkBoxYuksekLisans.setEnabled(false);
        checkBoxDoktora.setEnabled(false);
    }


    private void takipKontrolu (){
        DatabaseReference takipYolu = FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                .child("takipEdilenler");

        takipYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profilId).exists())
                {

                    btn_profili_Duzenle.setText("takip ediliyor");
                }

                else{
                    btn_profili_Duzenle.setText("takip et");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void takipcileriAl (){
        DatabaseReference takipciYolu = FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                .child("takipciler");

        takipciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txt_takipciler.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
// takip edilen sayisini almak
        DatabaseReference takipedilenYolu = FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                .child("takipEdilenler");

        takipedilenYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txt_takipEdilenler.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gonderiSayisiAl (){
        DatabaseReference gonderiYolu = FirebaseDatabase.getInstance().getReference("Gonderiler");

        gonderiYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot snapshot1: snapshot.getChildren())
                {
                    Gonderi gonderi = snapshot1.getValue(Gonderi.class);

                    if(gonderi.getGonderen().equals(profilId))
                    {
                        i++;
                    }
                }
                txt_gonderiler.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fotograflarim (){
        DatabaseReference fotografYolu = FirebaseDatabase.getInstance().getReference("Gonderiler");
        fotografYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gonderiList.clear();

                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    Gonderi gonderi = snapshot1.getValue(Gonderi.class);
                    if(gonderi.getGonderen().equals(profilId)){
                        gonderiList.add(gonderi);
                    }
                }

                Collections.reverse(gonderiList);
                fotografAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}