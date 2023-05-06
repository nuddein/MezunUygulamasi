package cerceve;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nuddein.mezunuygulamasi.R;

import java.util.ArrayList;
import java.util.List;

import Adapter.GonderiAdapter;
import Model.Gonderi;

public class HomeFragment extends Fragment {

    private RecyclerView  recyclerView;
    private GonderiAdapter gonderiAdapter;
    private List<Gonderi> gonderiListeleri;

    private List<String> takipListesi;





    public HomeFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_home, container, false);

       recyclerView = view.findViewById(R.id.recycler_view_HomeFragment);
       recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        gonderiListeleri = new ArrayList<>();

        gonderiAdapter =  new GonderiAdapter (getContext(),gonderiListeleri);

        recyclerView.setAdapter(gonderiAdapter);



        takipKontrolu();

       return view;
    }

  private void takipKontrolu ()
  {
        takipListesi = new ArrayList<>();

        DatabaseReference takipYolu = FirebaseDatabase.getInstance().getReference("Takip")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("takipEdilenler");

        takipYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                takipListesi.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren())
                {
                    takipListesi.add(snapshot1.getKey());
                }

                gonderileriOku();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

  }


    private void gonderileriOku (){
        DatabaseReference gonderiYolu = FirebaseDatabase.getInstance().getReference("Gonderiler");
        gonderiYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gonderiListeleri.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren())
                {
                     Gonderi gonderi = snapshot1.getValue(Gonderi.class);

                     for(String id: takipListesi)
                     {
                         if(gonderi.getGonderen().equals(id))
                         {
                              gonderiListeleri.add(gonderi);
                         }
                     }

                }

                gonderiAdapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}