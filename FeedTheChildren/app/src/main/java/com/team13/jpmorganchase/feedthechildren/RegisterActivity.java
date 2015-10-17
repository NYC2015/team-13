package com.team13.jpmorganchase.feedthechildren;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * A placeholder fragment containing a simple view.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText firstN, lastN, userN, email, pass, ConfirmPass;
    private CheckBox agreement;
    private String role;
    private EditText snapID;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);

        setContentView(R.layout.activity_register);
        Button cancelButton = (Button) findViewById(R.id.cancelRegistrationButton);
        Button confirmRegButton = (Button) findViewById(R.id.confirmRegistrationButton);
        Button uploadButton = (Button) findViewById(R.id.uploadPic);
        firstN = (EditText) findViewById(R.id.firstName);
        lastN = (EditText) findViewById(R.id.lastName);
        userN = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.emailAddress);
        pass = (EditText) findViewById(R.id.password);
        ConfirmPass = (EditText) findViewById(R.id.confirmPassword);
        TextView termCondition = (TextView) findViewById(R.id.conditionTerm);
        agreement = (CheckBox) findViewById(R.id.agreeCheckBox);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Register");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //Function of Cancel Button
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        snapID = (EditText) findViewById(R.id.snapID);


        role = "Consumer";
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioConsumer) {
                    role = "Consumer";

                } else {

                    role = "Supplier";
                }
            }
        });

        //Function of Confirm Registration Buttton
        confirmRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pass.getText().toString().equals(ConfirmPass.getText().toString())
                        && firstN.getText().toString().length() > 0 && lastN.getText().toString().length() >0
                        && snapID.getText().toString().length() > 0){
                    if(agreement.isChecked()) {
                        final ParseUser user = new ParseUser();
                        user.setUsername(userN.getText().toString());
                        user.setPassword(pass.getText().toString());
                        user.setEmail(email.getText().toString());
                        user.put("Last_Name", lastN.getText().toString());
                        user.put("First_Name", firstN.getText().toString());
                        user.put("Role", role);
                        user.put("SNAP_ID", snapID.getText().toString());
                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    //Successful
                                    ParseObject tempA = new ParseObject("Friend_update");
                                    tempA.put("newEntry", false);
                                    tempA.saveInBackground();
                                    user.put("newEntry", tempA);
                                    user.saveInBackground();

                                    Toast.makeText(getApplicationContext(), "Registration Success. A verification email was sent to"
                                            + email.getText().toString(), Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }
                                //Fail
                                Toast.makeText(getApplicationContext(), "Failed to register: "
                                        + e.getMessage(), Toast.LENGTH_SHORT).show();
                                ConfirmPass.setText("");
                                agreement.setChecked(false);
                            }
                        });
                        return;
                    }
                    //No check on agreement
                    Toast.makeText(getApplicationContext(), "Please read and agree " +
                            "the 'Term and Condition'", Toast.LENGTH_SHORT).show();
                    ConfirmPass.setText("");
                    return;
                }
                //Missing info or Not match password
                Toast.makeText(getApplicationContext(), "Please double check " +
                        "all information", Toast.LENGTH_SHORT).show();
                ConfirmPass.setText("");
            }
        });

        //Function of uploading an image
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
            }
        });

        //The "Term and Condition"
        termCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //display something
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
