package cerceve;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Adapter.DuyuruAdapter;
import Model.Duyuru;
import com.nuddein.mezunuygulamasi.R;

public class BildirimFragment extends Fragment {
    private RecyclerView duyuruRecyclerView;
    private DuyuruAdapter duyuruAdapter;
    private List<Duyuru> duyuruList;

    private EditText duyuruTitleEditText;
    private EditText duyuruContentEditText;
    private Button duyuruEkleButton;
    private Button duyuruDeadlineButton;
    private TextView duyuruDeadlineTextView;
    private long deadline;

    public BildirimFragment() {
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bildirim, container, false);

        duyuruRecyclerView = view.findViewById(R.id.duyuruRecyclerView);
        duyuruRecyclerView.setHasFixedSize(true);
        duyuruRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        duyuruList = new ArrayList<>();
        duyuruAdapter = new DuyuruAdapter(getContext(), duyuruList);
        duyuruRecyclerView.setAdapter(duyuruAdapter);

        duyurulariOku();

        duyuruTitleEditText = view.findViewById(R.id.duyuruTitleEditText);
        duyuruContentEditText = view.findViewById(R.id.duyuruContentEditText);
        duyuruEkleButton = view.findViewById(R.id.duyuruEkleButton);

        duyuruDeadlineButton = view.findViewById(R.id.duyuruDeadlineButton);
        duyuruDeadlineTextView = view.findViewById(R.id.duyuruDeadlineTextView);

        duyuruDeadlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        duyuruEkleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = duyuruTitleEditText.getText().toString().trim();
                String content = duyuruContentEditText.getText().toString().trim();

                if (!title.isEmpty() && !content.isEmpty()) {
                    duyuruEkle(title, content, deadline);
                    duyuruTitleEditText.setText("");
                    duyuruContentEditText.setText("");
                    Toast.makeText(getContext(), "Duyuru başarıyla eklendi.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void duyurulariOku() {
        DatabaseReference duyuruRef = FirebaseDatabase.getInstance().getReference("Duyurular");
        duyuruRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                duyuruList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Duyuru duyuru = snapshot.getValue(Duyuru.class);
                    if (System.currentTimeMillis() > duyuru.getDeadline()) {
                        // Eğer deadline geçmişse, duyuruyu sil
                        duyuruRef.child(snapshot.getKey()).removeValue();
                    } else {
                        duyuruList.add(duyuru);
                    }
                }
                duyuruAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void duyuruEkle(String title, String content, long deadline) {
        DatabaseReference duyuruRef = FirebaseDatabase.getInstance().getReference("Duyurular");
        String duyuruId = duyuruRef.push().getKey();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        Duyuru duyuru = new Duyuru(duyuruId, title, content, deadline, userId);

        duyuruRef.child(duyuruId).setValue(duyuru);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    deadline = calendar.getTimeInMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    String formattedDate = sdf.format(calendar.getTime());
                    duyuruDeadlineTextView.setText(formattedDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
}

