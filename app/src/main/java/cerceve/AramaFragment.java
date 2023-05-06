package cerceve;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.service.autofill.Dataset;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nuddein.mezunuygulamasi.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Adapter.KullaniciAdapter;
import Model.Kullanici;


public class AramaFragment extends Fragment {

   private RecyclerView recyclerView;
   private KullaniciAdapter kullaniciAdapter;
   private List<Kullanici> mKullaniciler;
   EditText arama_bar;



    public AramaFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_arama, container, false);


        recyclerView = view.findViewById(R.id.recycler_view_Arama);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        arama_bar = view.findViewById(R.id.edit_arama_bar);

        mKullaniciler = new ArrayList<>();
        kullaniciAdapter = new KullaniciAdapter(getContext(), mKullaniciler);

        recyclerView.setAdapter(kullaniciAdapter);

        kullanicilariOku();

        arama_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        kullaniciAra(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        // sil

        Button buttonSiralama = view.findViewById(R.id.button_siralama);
        buttonSiralama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = arama_bar.getText().toString().toLowerCase();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Sıralama Seçenekleri");
                builder.setItems(new CharSequence[]{"Giriş Yılına Göre Sırala", "Mezun Yılına Göre Sırala"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // Giriş yılına göre sıralama seçildi
                            Query girisYiliSorgu = FirebaseDatabase.getInstance().getReference("Kullanicilar")
                                    .orderByChild("girisYili")
                                    .startAt(s)
                                    .endAt(s + "\uf8ff");

                            girisYiliSorgu.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    mKullaniciler.clear();
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                                        mKullaniciler.add(kullanici);
                                    }
                                    kullaniciAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else if (which == 1) {
                            // Mezun yılına göre sıralama seçildi
                            Query mezunYiliSorgu = FirebaseDatabase.getInstance().getReference("Kullanicilar")
                                    .orderByChild("mezunYili")
                                    .startAt(s)
                                    .endAt(s + "\uf8ff");

                            mezunYiliSorgu.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    mKullaniciler.clear();
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                                        mKullaniciler.add(kullanici);
                                    }
                                    kullaniciAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });

                builder.create().show();
            }
        });









        return view;
    }

    private void kullaniciAra (String s){
        Query sorgu = FirebaseDatabase.getInstance().getReference("Kullanicilar").orderByChild("kullaniciadi")
                .startAt(s)
                .endAt(s+"\uf8ff");

        sorgu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mKullaniciler.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {

                    Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                    mKullaniciler.add(kullanici);
                }

                kullaniciAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void kullanicilariOku ()

    {

        DatabaseReference kullanicilerYolu = FirebaseDatabase.getInstance().getReference("Kullanicilar");


        kullanicilerYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if( arama_bar.getText().toString().equals(""))
                {

                    mKullaniciler.clear();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren())
                    {
                        Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                        mKullaniciler.add(kullanici);
                    }

                    kullaniciAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}