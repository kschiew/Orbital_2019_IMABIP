package com.orbital19.imabip.edits;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbital19.imabip.MainActivity;
import com.orbital19.imabip.R;
import com.orbital19.imabip.models.User;

public class EditUserActivity extends AppCompatActivity {
    private TextView userNameTV, userIDTV, userEmailTV;
    private EditText userPhoneTV;
    private Button doneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        View view = findViewById(R.id.user_info_edit);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Info update");

        Intent intent = this.getIntent();
        final Bundle bundle = intent.getExtras();
        final User user = (User) bundle.getSerializable("toEditUser");

        userNameTV = view.findViewById(R.id.user_Name_edit);
        userIDTV = view.findViewById(R.id.user_ID_edit);
        userEmailTV = view.findViewById(R.id.user_Email_edit);
        userPhoneTV = view.findViewById(R.id.user_Phone_edit);
        doneBtn = view.findViewById(R.id.done_edit_btn);

        String detail1 = "Name: " + user.getName();
        String detail2 = "ID: " + user.getID();
        String detail3 = "Email: " + user.getEmail();
        String detail4 = "Phone: " + user.getPhone();
        userNameTV.setText(detail1);
        userIDTV.setText(detail2);
        userEmailTV.setText(detail3);
        userPhoneTV.setText(detail4);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection(User.usersCollection)
                        .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .update(User.phoneKey, userPhoneTV.getText().toString());

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
}
