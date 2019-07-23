package com.orbital19.imabip.models.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orbital19.imabip.R;
import com.orbital19.imabip.edits.EditUserActivity;
import com.orbital19.imabip.models.User;

public class DisplayUser extends AppCompatActivity {
    private TextView userNameTV, userIDTV, userEmailTV, userPhoneTV;
    private Button editBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user);
        View view = findViewById(R.id.user_info_display);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle("Account Info");

        Intent intent = this.getIntent();
        final Bundle bundle = intent.getExtras();
        final User user = (User) bundle.getSerializable("toViewUser");

        userNameTV = view.findViewById(R.id.user_Name);
        userIDTV = view.findViewById(R.id.user_ID);
        userEmailTV = view.findViewById(R.id.user_Email);
        userPhoneTV = view.findViewById(R.id.user_Phone);
        editBtn = view.findViewById(R.id.edit_btn);

        String detail1 = "Name: " + user.getName();
        String detail2 = "ID: " + user.getID();
        String detail3 = "Email: " + user.getEmail();
        String detail4 = "Phone: " + user.getPhone();
        userNameTV.setText(detail1);
        userIDTV.setText(detail2);
        userEmailTV.setText(detail3);
        userPhoneTV.setText(detail4);

        if (user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
            editBtn.setVisibility(View.VISIBLE);
        else
            editBtn.setVisibility(View.GONE);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), EditUserActivity.class);
                intent1.putExtra("toEditUser", user);

                startActivity(intent1);
            }
        });
    }
}
