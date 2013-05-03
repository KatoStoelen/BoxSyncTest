package org.ubicollab.android.boxsynctest;

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.cis.SocialContract;
import org.ubicollab.android.boxsynctest.entitiy.Entity;
import org.ubicollab.android.boxsynctest.entitiy.Membership;
import org.ubicollab.android.boxsynctest.entitiy.Person;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class ShowMembersActivity extends Activity {
	
	public static final String TAG = "ShowMembersActivity";
	public static final String EXTRA_COMMUNITY_ID = "extra_community_id";
	
	private long mCommunityId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_members);
		
		Intent intent = getIntent();
		mCommunityId = intent.getLongExtra(EXTRA_COMMUNITY_ID, Entity.ENTITY_DEFAULT_ID);
		
		populate();
	}
	
	private void populate() {
		if (mCommunityId != Entity.ENTITY_DEFAULT_ID) {
			try {
				List<Membership> memberships = Membership.getCommunityMembers(mCommunityId, getContentResolver());
				List<Person> members = new ArrayList<Person>();
				
				for (Membership membership : memberships)
					members.add(Person.getEntity(Person.class, membership.getMemberId(), getContentResolver()));
				
				ArrayAdapter<Person> adapter = new ArrayAdapter<Person>(this, android.R.layout.simple_list_item_1, members);
				
				((ListView) findViewById(R.id.members_list_view)).setAdapter(adapter);
				
				List<Person> nonMembers = getNonMembers(Person.getAllPeople(getContentResolver()), members);
				ArrayAdapter<Person> spinnerAdapter = new ArrayAdapter<Person>(this, android.R.layout.simple_spinner_item, nonMembers);
				
				((Spinner) findViewById(R.id.membership_person_list)).setAdapter(spinnerAdapter);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	private List<Person> getNonMembers(List<Person> allPeople, List<Person> members) {
		List<Person> nonMembers = new ArrayList<Person>();
		
		for (Person person : allPeople) {
			boolean isMember = false;
			for (Person member : members) {
				if (person.getId() == member.getId()) {
					isMember = true;
					break;
				}
			}
			
			if (!isMember)
				nonMembers.add(person);
		}
		
		return nonMembers;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_members, menu);
		return true;
	}
	
	public void addMember(View view) {
		Person person = (Person) ((Spinner) findViewById(R.id.membership_person_list)).getSelectedItem();
		
		if (person != null) {
			Membership membership = new Membership();
			membership.setGlobalId(SocialContract.GLOBAL_ID_PENDING);
			membership.setAccountName(Globals.ME_ENTRY.getAccountName());
			membership.setAccountType(Constants.ACCOUNT_TYPE);
			membership.setDirty(1);
			membership.setCommunityId(mCommunityId);
			membership.setMemberId(person.getId());
			membership.setType("member");
			
			membership.insert(getContentResolver());
			
			populate();
		} else {
			Toast.makeText(this, "No person selected.", Toast.LENGTH_SHORT).show();
		}
	}

	public void back(View view) {
		finish();
	}
}
