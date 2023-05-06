package Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nuddein.mezunuygulamasi.R;

import java.util.List;

import Model.Duyuru;
import Model.Kullanici;

public class DuyuruAdapter extends RecyclerView.Adapter<DuyuruAdapter.ViewHolder> {
    private Context mContext;
    private List<Duyuru> duyuruList;

    public DuyuruAdapter(Context mContext, List<Duyuru> duyuruList) {
        this.mContext = mContext;
        this.duyuruList = duyuruList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.duyuru_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Duyuru duyuru = duyuruList.get(position);
        holder.title.setText(duyuru.getTitle());
        holder.content.setText(duyuru.getContent());
        holder.deadline.setText(duyuru.getDeadlineString());

        Log.d("DuyuruAdapter", "Duyuru userId: " + duyuru.getUserId());

        getGonderenKullanici(holder.gonderen, duyuru.getUserId());

        holder.deleteDuyuruButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDuyuru(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return duyuruList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;
        public TextView deadline;
        public TextView gonderen;

        ImageButton deleteDuyuruButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.duyuruTitle);
            content = itemView.findViewById(R.id.duyuruContent);
            deadline = itemView.findViewById(R.id.duyuruDeadline);
            gonderen = itemView.findViewById(R.id.duyuruGonderen);
            deleteDuyuruButton = itemView.findViewById(R.id.deleteDuyuruButton);
        }
    }

    private void getGonderenKullanici(final TextView gonderen, String userId) {
        if (userId != null && !userId.isEmpty()) {
            DatabaseReference kullaniciRef = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(userId);

            kullaniciRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                    if (kullanici != null) {
                        gonderen.setText(kullanici.getAd());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            gonderen.setText("Bilinmeyen Kullanıcı");
        }
    }

    private void deleteDuyuru(ViewHolder holder) {
        int position = holder.getAdapterPosition();

        if (position >= 0 && position < duyuruList.size()) {
            Duyuru duyuru = duyuruList.get(position);
            if (duyuru != null && duyuru.getDuyuruId() != null) {
                DatabaseReference duyuruRef = FirebaseDatabase.getInstance().getReference("Duyurular").child(duyuru.getDuyuruId());

                duyuruRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            try {
                                duyuruList.remove(position);
                                notifyDataSetChanged();
                            } catch (IndexOutOfBoundsException e) {
                                Toast.makeText(mContext, "Duyuru silinmesi", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, "Duyuru silinemedi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(mContext, "Geçersiz duyuru", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Geçersiz pozisyon", Toast.LENGTH_SHORT).show();
        }
    }
}

