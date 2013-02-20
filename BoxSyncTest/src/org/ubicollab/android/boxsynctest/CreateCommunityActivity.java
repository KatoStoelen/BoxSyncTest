package org.ubicollab.android.boxsynctest;

import java.util.List;

import org.ubicollab.android.boxsynctest.entitiy.Community;
import org.ubicollab.android.boxsynctest.entitiy.Membership;
import org.ubicollab.android.boxsynctest.entitiy.Person;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class CreateCommunityActivity extends Activity {
	
	public static final String EXTRA_COMMUNITY = "extra_community";
	
	private Community mCommunity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_community);
		
		try {
			List<Person> people = Person.getAllPeople(getContentResolver());
			ArrayAdapter<Person> adapter = new ArrayAdapter<Person>(this, R.id.community_owner_list, people);
			
			Spinner ownerSpinner = (Spinner) findViewById(R.id.community_owner_list);
			ownerSpinner.setAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Intent intent = getIntent();
		String serializedCommunity = intent.getStringExtra(EXTRA_COMMUNITY);
		Community community = Community.deserialize(serializedCommunity, Community.class);
		
		if (community != null)
			populateFields(community);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_community, menu);
		return true;
	}
	
	private void populateFields(Community community) {
		mCommunity = community;
		
		((EditText) findViewById(R.id.community_name_field)).setText(community.getName());
		((EditText) findViewById(R.id.community_type_field)).setText(community.getType());
		((EditText) findViewById(R.id.community_description_field)).setText(community.getDescription());
		
		setOwner(community.getOwnerId());
	}
	
	private void setOwner(long id) {
		Spinner ownerSpinner = (Spinner) findViewById(R.id.community_owner_list);
		SpinnerAdapter adapter = ownerSpinner.getAdapter();
		
		for (int i = 0; i < adapter.getCount(); i++) {
			Person person = (Person) adapter.getItem(i);
			
			if (person.getId() == id)
				ownerSpinner.setSelection(i);
		}
	}
	
	public void addCommunity(View view) {
		if (((EditText) findViewById(R.id.community_name_field)).getText().toString().trim().length() == 0 ||
				((Spinner) findViewById(R.id.community_owner_list)).getSelectedItem() == null) {
			Toast.makeText(this, "Community name and owner is required.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (mCommunity == null)
			mCommunity = new Community();
		
		mCommunity.setAccountName(Globals.ACCOUNT_NAME);
		mCommunity.setAccountType(Constants.ACCOUNT_TYPE);
		mCommunity.setDirty(1);
		mCommunity.setName(((EditText) findViewById(R.id.community_name_field)).getText().toString());
		mCommunity.setType(((EditText) findViewById(R.id.community_type_field)).getText().toString());
		mCommunity.setDescription(((EditText) findViewById(R.id.community_description_field)).getText().toString());
		
		Spinner ownerSpinner = (Spinner) findViewById(R.id.community_owner_list);
		Person owner = (Person) ownerSpinner.getSelectedItem();
		mCommunity.setOwnerId(owner.getId());
		
		Uri communityUri = mCommunity.insert(getContentResolver());
		long communityId = Long.parseLong(communityUri.getLastPathSegment());
		
		if (mCommunity == null)
			addMembership(communityId, owner);
		
		finish();
	}
	
	private void addMembership(long communityId, Person member) {
		Membership membership = new Membership();
		membership.setAccountName(Globals.ACCOUNT_NAME);
		membership.setAccountType(Constants.ACCOUNT_TYPE);
		membership.setDirty(1);
		membership.setCommunityId(communityId);
		membership.setMemberId(member.getId());
		membership.setType("owner");
		
		membership.insert(getContentResolver());
	}
	
	public void cancel(View view) {
		finish();
	}
}
