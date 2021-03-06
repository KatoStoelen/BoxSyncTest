/**
 * Copyright 2012 UbiCollab.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ubicollab.android.boxsynctest.entitiy;

import java.util.List;

import org.societies.android.api.cis.SocialContract.Communities;
import org.societies.android.api.cis.SocialContract.People;
import org.societies.android.api.cis.SocialContract.Services;

import com.google.renamedgson.annotations.Expose;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import static org.societies.android.api.cis.SocialContract.Sharing.*;

/**
 * A sharing entity.
 * 
 * @author Kato
 */
public class Sharing extends Entity {

	private long id = ENTITY_DEFAULT_ID;
	
	@Expose private String globalId;
	private long serviceId;
	private long ownerId;
	private long communityId;
	@Expose private String type;
	@Expose private long creationDate = System.currentTimeMillis() / 1000;
	@Expose private long lastModifiedDate = System.currentTimeMillis() / 1000;
	
	@Expose private String globalIdService;
	@Expose private String globalIdOwner;
	@Expose private String globalIdCommunity;
	
	private Service service;
	
	/**
	 * Gets a list of all the "dirty" sharings.
	 * @param resolver The content resolver.
	 * @return A list of updated sharings.
	 * @throws Exception If an error occurs while fetching.
	 */
	public static List<Sharing> getUpdatedSharings(
			ContentResolver resolver) throws Exception {
		List<Sharing> sharings = Entity.getEntities(
				Sharing.class,
				resolver,
				CONTENT_URI,
				null,
				DIRTY + " = 1",
				null,
				null);
		
		for (Sharing sharing : sharings)
			sharing.fetchGlobalIds(resolver);
		
		return sharings;
	}
	
	public static List<Sharing> getSharingOfCommunity(
			long communityId, ContentResolver resolver) throws Exception {
		List<Sharing> sharings = Entity.getEntities(
				Sharing.class,
				resolver,
				CONTENT_URI,
				null,
				_ID_COMMUNITY + " = " + communityId,
				null,
				CREATION_DATE + " DESC");
		
		for (Sharing sharing : sharings) {
			sharing.fetchGlobalIds(resolver);
			sharing.fetchService(resolver);
		}
		
		return sharings;
	}
	
	public static int deleteCommunitySharings(
			long communityId,
			boolean forceDelete,
			ContentResolver resolver
	) throws Exception {
		String where = _ID_COMMUNITY + " = " + communityId;
		if (forceDelete)
			return resolver.delete(CONTENT_URI, where, null);
		else
			return Entity.markEntitiesForDeletion(CONTENT_URI, where, null, resolver);
	}
	
	@Override
	protected void populate(Cursor cursor) {
		super.populate(cursor);
		
		setId(				Entity.getLong(cursor, _ID));
		setGlobalId(		Entity.getString(cursor, GLOBAL_ID));
		setServiceId(		Entity.getLong(cursor, _ID_SERVICE));
		setOwnerId(			Entity.getLong(cursor, _ID_OWNER));
		setCommunityId(		Entity.getLong(cursor, _ID_COMMUNITY));
		setType(			Entity.getString(cursor, TYPE));
		setCreationDate(	Entity.getLong(cursor, CREATION_DATE));
		setLastModifiedDate(Entity.getLong(cursor, LAST_MODIFIED_DATE));
	}

	@Override
	protected ContentValues getEntityValues() {
		ContentValues values = super.getEntityValues();
		
		values.put(GLOBAL_ID, globalId);
		values.put(_ID_SERVICE, serviceId);
		values.put(_ID_OWNER, ownerId);
		values.put(_ID_COMMUNITY, communityId);
		values.put(TYPE, type);
		values.put(CREATION_DATE, creationDate);
		values.put(LAST_MODIFIED_DATE, lastModifiedDate);
		
		return values;
	}
	
	@Override
	protected Uri getContentUri() {
		return CONTENT_URI;
	}
	
	@Override
	protected void fetchGlobalIds(ContentResolver resolver) {
		setGlobalIdCommunity(
				Entity.getGlobalId(
						Communities.CONTENT_URI,
						communityId,
						Communities.GLOBAL_ID,
						resolver));
		setGlobalIdOwner(
				Entity.getGlobalId(
						People.CONTENT_URI,
						ownerId,
						People.GLOBAL_ID,
						resolver));
		setGlobalIdService(
				Entity.getGlobalId(
						Services.CONTENT_URI,
						serviceId,
						Services.GLOBAL_ID,
						resolver));
	}
	
	@Override
	public void fetchLocalId(ContentResolver resolver) {
		setId(Entity.getLocalId(CONTENT_URI, _ID, GLOBAL_ID, globalId, resolver));
		setServiceId(
				Entity.getLocalId(
						Services.CONTENT_URI,
						Services._ID,
						Services.GLOBAL_ID,
						globalIdService,
						resolver));
		setOwnerId(
				Entity.getLocalId(
						People.CONTENT_URI,
						People._ID,
						People.GLOBAL_ID,
						globalIdOwner,
						resolver));
		setCommunityId(
				Entity.getLocalId(
						Communities.CONTENT_URI,
						Communities._ID,
						Communities.GLOBAL_ID,
						globalIdCommunity,
						resolver));
	}
	
	public void fetchService(ContentResolver resolver) {
		try {
			service = Entity.getEntity(Service.class, serviceId, resolver);
		} catch (Exception e) {
			Log.e("Sharing", e.getMessage(), e);
		}
	}
	
	@Override
	public long getId() {
		return id;
	}
	
	@Override
	protected void setId(long id) {
		this.id = id;
	}
	
	@Override
	public String getGlobalId() {
		return globalId;
	}
	
	@Override
	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}
	
	public long getServiceId() {
		return serviceId;
	}
	
	public void setServiceId(long serviceId) {
		this.serviceId = serviceId;
	}
	
	public long getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	
	public long getCommunityId() {
		return communityId;
	}
	
	public void setCommunityId(long communityId) {
		this.communityId = communityId;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public long getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
	
	public long getLastModifiedDate() {
		return lastModifiedDate;
	}
	
	public void setLastModifiedDate(long lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getGlobalIdService() {
		return globalIdService;
	}

	public void setGlobalIdService(String globalIdService) {
		this.globalIdService = globalIdService;
	}

	public String getGlobalIdOwner() {
		return globalIdOwner;
	}

	public void setGlobalIdOwner(String globalIdOwner) {
		this.globalIdOwner = globalIdOwner;
	}

	public String getGlobalIdCommunity() {
		return globalIdCommunity;
	}

	public void setGlobalIdCommunity(String globalIdCommunity) {
		this.globalIdCommunity = globalIdCommunity;
	}
	
	@Override
	public String toString() {
		if (service != null)
			return service.toString();
		else
			return "UNKNOWN SERVICE";
	}
}
