package org.ubicollab.android.boxsynctest;

import java.util.List;

import org.ubicollab.android.boxsynctest.entitiy.Person;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ListPeopleActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_people);
		
		final ListView peopleList = (ListView) findViewById(R.id.people_list_view);
		
		try {
			List<Person> people = Person.getAllPeople(getContentResolver());
			ArrayAdapter<Person> adapter = new ArrayAdapter<Person>(this, android.R.layout.simple_list_item_1, people);
			
			peopleList.setAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		final Context context = this;
		peopleList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				Person person = (Person) peopleList.getItemAtPosition(pos);
				
				Intent intent = new Intent(context, AddPersonActivity.class);
				intent.putExtra(AddPersonActivity.EXTRA_PERSON, person.serialize());
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_people, menu);
		return true;
	}

	public void back(View view) {
		finish();
	}
}
