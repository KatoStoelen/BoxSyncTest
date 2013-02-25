package org.ubicollab.android.boxsynctest;

import org.ubicollab.android.boxsynctest.entitiy.Entity;
import org.ubicollab.android.boxsynctest.entitiy.Person;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddPersonActivity extends Activity {
	
	public static final String EXTRA_PERSON = "extra_person";
	
	private Person mPerson = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_person);
		
		Person person = null;
		Intent intent = getIntent();
		String serializedPerson = intent.getStringExtra(EXTRA_PERSON);
		
		if (serializedPerson != null)
			person = Person.deserialize(serializedPerson, Person.class);
		
		if (person != null) {
			populateFields(person);
			
			findViewById(R.id.person_delete_button).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.add_person_title)).setText(getString(R.string.title_activity_edit_person));
			((Button) findViewById(R.id.person_add_button)).setText(getString(R.string.person_save_button));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_person, menu);
		return true;
	}
	
	private void populateFields(Person person) {
		mPerson = person;
		
		((EditText) findViewById(R.id.person_name_field)).setText(person.getName());
		((EditText) findViewById(R.id.person_email_field)).setText(person.getEmail());
		((EditText) findViewById(R.id.person_description_field)).setText(person.getDescription());
	}
	
	public void addPerson(View view) {
		String name = ((EditText) findViewById(R.id.person_name_field)).getText().toString().trim();
		String email = ((EditText) findViewById(R.id.person_email_field)).getText().toString().trim();
		String description = ((EditText) findViewById(R.id.person_description_field)).getText().toString().trim();
		
		if (name.length() == 0 || email.length() == 0) {
			Toast.makeText(this, "Name and email is required.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (mPerson == null)
			mPerson = new Person();
		
		mPerson.setAccountName(Globals.ACCOUNT_NAME);
		mPerson.setAccountType(Constants.ACCOUNT_TYPE);
		mPerson.setDirty(1);
		mPerson.setEmail(email);
		mPerson.setName(name);
		mPerson.setDescription(description);
		mPerson.setUserName(Globals.ACCOUNT_NAME);
		
		if (mPerson.getId() == Entity.ENTITY_DEFAULT_ID)
			mPerson.insert(getContentResolver());
		else
			mPerson.update(getContentResolver());
		
		finish();
	}
	
	public void deletePerson(View view) {
		if (mPerson != null && mPerson.getId() != Entity.ENTITY_DEFAULT_ID) {
			new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Delete")
				.setMessage("Are you sure you want to delete this person?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mPerson.delete(getContentResolver());
						finish();
					}
				})
				.setNegativeButton("No", null)
				.show();
		}
	}

	public void cancel(View view) {
		finish();
	}
}
