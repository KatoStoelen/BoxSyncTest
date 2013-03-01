package org.ubicollab.android.boxsynctest;

import java.util.List;

import org.societies.android.api.cis.SocialContract;
import org.ubicollab.android.boxsynctest.entitiy.Community;
import org.ubicollab.android.boxsynctest.entitiy.CommunityActivity;
import org.ubicollab.android.boxsynctest.entitiy.Entity;
import org.ubicollab.android.boxsynctest.entitiy.Membership;
import org.ubicollab.android.boxsynctest.entitiy.Person;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CreateCommunityActivity extends Activity {
	
	public static final String TAG = "CreateCommunityActivity";
	public static final String EXTRA_COMMUNITY = "extra_community";
	
	public static final int RESULT_CODE_CANCELLED = 100;
	public static final int RESULT_CODE_EDITED = 110;
	public static final int RESULT_CODE_DELETED = 120;
	
	private Community mCommunity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_community);
		
		Spinner ownerSpinner = (Spinner) findViewById(R.id.community_owner_list);
		try {
			List<Person> people = Person.getAllPeople(getContentResolver());
			ArrayAdapter<Person> adapter = new ArrayAdapter<Person>(this, android.R.layout.simple_spinner_item, people);
			
			ownerSpinner.setAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Community community = null;
		Intent intent = getIntent();
		String serializedCommunity = intent.getStringExtra(EXTRA_COMMUNITY);
		if (serializedCommunity != null)
			community = Community.deserialize(serializedCommunity, Community.class);
		
		if (community != null) {
			populateFields(community);
			ownerSpinner.setEnabled(false);
			
			findViewById(R.id.community_delete_button).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.create_community_title)).setText(getString(R.string.title_activity_edit_community));
			((Button) findViewById(R.id.community_add_button)).setText(getString(R.string.community_save_button));
		}
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
		
		mCommunity.setAccountName(Globals.ME_ENTRY.getAccountName());
		mCommunity.setAccountType(Constants.ACCOUNT_TYPE);
		mCommunity.setDirty(1);
		mCommunity.setName(((EditText) findViewById(R.id.community_name_field)).getText().toString());
		mCommunity.setType(((EditText) findViewById(R.id.community_type_field)).getText().toString());
		mCommunity.setDescription(((EditText) findViewById(R.id.community_description_field)).getText().toString());
		
		Spinner ownerSpinner = (Spinner) findViewById(R.id.community_owner_list);
		Person owner = (Person) ownerSpinner.getSelectedItem();
		mCommunity.setOwnerId(owner.getId());
		
		if (mCommunity.getId() == Entity.ENTITY_DEFAULT_ID) {
			mCommunity.setGlobalId(SocialContract.GLOBAL_ID_PENDING);
			
			Uri communityUri = mCommunity.insert(getContentResolver());
			long communityId = Long.parseLong(communityUri.getLastPathSegment());
			
			addMembership(communityId, owner);
		} else {
			mCommunity.update(getContentResolver());
		}
		
		Intent data = new Intent();
		data.putExtra(EXTRA_COMMUNITY, mCommunity.serialize());
		setResult(RESULT_CODE_EDITED, data);
		
		finish();
	}
	
	private void addMembership(long communityId, Person member) {
		Membership membership = new Membership();
		membership.setGlobalId(SocialContract.GLOBAL_ID_PENDING);
		membership.setAccountName(Globals.ME_ENTRY.getAccountName());
		membership.setAccountType(Constants.ACCOUNT_TYPE);
		membership.setDirty(1);
		membership.setCommunityId(communityId);
		membership.setMemberId(member.getId());
		membership.setType("owner");
		
		membership.insert(getContentResolver());
	}
	
	public void deleteCommunity(View view) {
		if (mCommunity != null && mCommunity.getId() != Entity.ENTITY_DEFAULT_ID) {
			new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Delete")
				.setMessage("Are you sure you want to delete this community?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							// SET FORCE DELETE TO FALSE WHEN SYNCING
							Membership.deleteCommunityMemberships(mCommunity.getId(), true, getContentResolver());
							CommunityActivity.deleteCommunityFeed(mCommunity.getId(), true, getContentResolver());
							mCommunity.delete(getContentResolver());
							//mCommunity.markForDeletion(getContentResolver());
							
							setResult(RESULT_CODE_DELETED, new Intent());
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						finish();
					}
				})
				.setNegativeButton("No", null)
				.show();
		}
	}
	
	public void cancel(View view) {
		setResult(RESULT_CODE_CANCELLED, new Intent());
		finish();
	}
}
