package Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nuddein.mezunuygulamasi.R;

import java.util.List;

import Model.Gonderi;
import Model.Kullanici;
import cerceve.GonderiDetayiFragment;
import cerceve.ProfilFragment;

public class GonderiAdapter  extends  RecyclerView.Adapter<GonderiAdapter.ViewHolder>{

    public Context mContext;
    public List<Gonderi> mGonderi;

    private FirebaseUser mevcutFirebaseUser;

    public GonderiAdapter(Context mContext, List<Gonderi>mGonderi) {
        this.mGonderi = mGonderi;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.gonderi_ogesi, parent, false );

        return new GonderiAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        mevcutFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Gonderi gonderi = mGonderi.get(position);






        if (gonderi == null) {
            // Hata mesajı göster
            Toast.makeText(mContext, "Gönderi silindi.", Toast.LENGTH_SHORT).show();
            return;
        }

        //hata alirsan bu kismi sil
        if (mevcutFirebaseUser.getUid().equals(gonderi.getGonderen())) {
            holder.silme_dugmesi.setVisibility(View.VISIBLE);
        } else {
            holder.silme_dugmesi.setVisibility(View.GONE);
        }

        Glide.with(mContext).load(gonderi.getGonderiResmi()).into(holder.gonderi_resmi);

        // gonderi hakkinda olan kisim bos birakilmissa gonderi hakkinda txt ni gostermemesi icin
        if(gonderi.getGonderiHakkinda().equals(""))
        {
            holder.txt_gonderiHakkinda.setVisibility(View.GONE);
        }
        else
        {
            holder.txt_gonderiHakkinda.setVisibility(View.VISIBLE);
            holder.txt_gonderiHakkinda.setText(gonderi.getGonderiHakkinda());
        }
        // bu islemese silerem // //
        if(gonderi.getGonderiResmi().equals("")){
            holder.gonderi_resmi.setVisibility(View.GONE);
        }

        gonderenBilgileri(holder.profil_resmi, holder.txt_kullanici_adi, holder.txt_gonderen,gonderi.getGonderen());

        begenildi(gonderi.getGonderiId(), holder.begeni_resmi);
        begeniSayisi(holder.txt_begeni, gonderi.getGonderiId());


            // Profil resmine tiklama
        holder.profil_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new ProfilFragment()).commit();

            }
        });


        holder.silme_dugmesi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteGonderi(position);
            }
        });










        //KullaniciADina tiklama


        holder.txt_kullanici_adi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new ProfilFragment()).commit();

            }
        });


                //Gonderene tiklama


        holder.txt_gonderen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new ProfilFragment()).commit();

            }
        });



            // resme tiklama

        holder.gonderi_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", gonderi.getGonderiId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new GonderiDetayiFragment()  ).commit();

            }
        });











        holder.begeni_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.begeni_resmi.getTag().equals("beğen"))
                {

                    FirebaseDatabase.getInstance().getReference().child("Begeniler").child(gonderi.getGonderiId())
                            .child(mevcutFirebaseUser.getUid()).setValue(true);
                }

                else //begenildi yaziyorsa
                {

                    FirebaseDatabase.getInstance().getReference().child("Begeniler").child(gonderi.getGonderiId())
                            .child(mevcutFirebaseUser.getUid()).removeValue();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mGonderi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        public ImageView profil_resmi, gonderi_resmi, begeni_resmi,  silme_dugmesi;

        public TextView txt_kullanici_adi, txt_begeni, txt_gonderen, txt_gonderiHakkinda;





        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profil_resmi = itemView.findViewById(R.id.profil_resmi_Gonderi_ogesi);
            gonderi_resmi = itemView.findViewById(R.id.gonder_resmi_gonderi_ogesi);
            begeni_resmi = itemView.findViewById(R.id.begeni_gonderi_ogesi);
            silme_dugmesi = itemView.findViewById(R.id.silme_dugmesi_gonderi_ogesi);


            txt_kullanici_adi = itemView.findViewById(R.id.txt_kullaniciadi_gonderi_ogesi);
            txt_gonderen = itemView.findViewById(R.id.txt_gonderen_gonderi_ogesi);
            txt_begeni = itemView.findViewById(R.id.txt_begeniler_gonderi_ogesi);
            txt_gonderiHakkinda = itemView.findViewById(R.id.txt_gonderihakkinda_gonderi_ogesi);



        }
    }

    private void deleteGonderi(int position) {
        if (position >= 0 && position < mGonderi.size()) {
            DatabaseReference gonderiRef = FirebaseDatabase.getInstance().getReference("Gonderiler").child(mGonderi.get(position).getGonderiId());

            gonderiRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mGonderi.remove(position);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "Gönderi silinemedi", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(mContext, "Geçersiz pozisyon", Toast.LENGTH_SHORT).show();
        }
    }






    private void begenildi (String gonderiId, ImageView imageView){
        FirebaseUser mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference begeniVeritabaniYolu = FirebaseDatabase.getInstance().getReference()
                .child("Begeniler")
                .child(gonderiId);

        begeniVeritabaniYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(mevcutKullanici.getUid()).exists())
                {

                    imageView.setImageResource(R.drawable.ic_begenildi);
                    imageView.setTag("beğenildi");
                }

                else
                {
                    imageView.setImageResource(R.drawable.ic_begeni);
                    imageView.setTag("beğen");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void begeniSayisi (TextView begeniler, String gonderiId){
        DatabaseReference begeniSayisiVeritabaniYolu = FirebaseDatabase.getInstance().getReference()
                .child("Begeniler")
                .child(gonderiId);

        begeniSayisiVeritabaniYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                begeniler.setText(snapshot.getChildrenCount()+ "beğeni");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gonderenBilgileri (ImageView profil_resmi, TextView kullaniciadi, TextView gonderen, String kullaniciId)
    {
        DatabaseReference veriYolu = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(kullaniciId);

        veriYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                Kullanici kullanici = snapshot.getValue(Kullanici.class);

                Glide.with(mContext).load(kullanici.getResimurl()).into(profil_resmi);
                kullaniciadi.setText(kullanici.getKullaniciadi());
                gonderen.setText(kullanici.getKullaniciadi());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
