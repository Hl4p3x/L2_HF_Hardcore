/*
 * Copyright (C) 2004-2016 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.communitybbs.BB;

import com.l2jserver.common.database.pool.impl.ConnectionFactory;
import com.l2jserver.gameserver.communitybbs.Manager.PostBBSManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Maktakien
 */
public class Post
{
	private static final Logger LOG = LoggerFactory.getLogger(Post.class);
	
	public static class CPost
	{
		public int postId;
		public String postOwner;
		public int postOwnerId;
		public long postDate;
		public int postTopicId;
		public int postForumId;
		public String postTxt;
	}
	
	private final List<CPost> _post = new ArrayList<>();
	
	/**
	 * @param _PostOwner
	 * @param _PostOwnerID
	 * @param date
	 * @param tid
	 * @param _PostForumID
	 * @param txt
	 */
	public Post(String _PostOwner, int _PostOwnerID, long date, int tid, int _PostForumID, String txt)
	{
		final CPost cp = new CPost();
		cp.postId = 0;
		cp.postOwner = _PostOwner;
		cp.postOwnerId = _PostOwnerID;
		cp.postDate = date;
		cp.postTopicId = tid;
		cp.postForumId = _PostForumID;
		cp.postTxt = txt;
		_post.add(cp);
		insertindb(cp);
		
	}
	
	public Post(Topic t)
	{
		load(t);
	}
	
	public void insertindb(CPost cp)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO posts (post_id,post_owner_name,post_ownerid,post_date,post_topic_id,post_forum_id,post_txt) values (?,?,?,?,?,?,?)"))
		{
			ps.setInt(1, cp.postId);
			ps.setString(2, cp.postOwner);
			ps.setInt(3, cp.postOwnerId);
			ps.setLong(4, cp.postDate);
			ps.setInt(5, cp.postTopicId);
			ps.setInt(6, cp.postForumId);
			ps.setString(7, cp.postTxt);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warn("Could not save post ID {} in database!", cp.postId, e);
		}
	}
	
	public CPost getCPost(int id)
	{
		return _post.get(id);
	}
	
	public void deleteme(Topic t)
	{
		PostBBSManager.getInstance().delPostByTopic(t);
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM posts WHERE post_forum_id=? AND post_topic_id=?"))
		{
			ps.setInt(1, t.getForumID());
			ps.setInt(2, t.getID());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warn("Unable to delete post for topic ID {} in forum ID {} from database!", t.getForumID(), t.getID(), e);
		}
	}
	
	/**
	 * @param t
	 */
	private void load(Topic t)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM posts WHERE post_forum_id=? AND post_topic_id=? ORDER BY post_id ASC"))
		{
			ps.setInt(1, t.getForumID());
			ps.setInt(2, t.getID());
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final CPost cp = new CPost();
					cp.postId = rs.getInt("post_id");
					cp.postOwner = rs.getString("post_owner_name");
					cp.postOwnerId = rs.getInt("post_ownerid");
					cp.postDate = rs.getLong("post_date");
					cp.postTopicId = rs.getInt("post_topic_id");
					cp.postForumId = rs.getInt("post_forum_id");
					cp.postTxt = rs.getString("post_txt");
					_post.add(cp);
				}
			}
		}
		catch (Exception e)
		{
			LOG.warn("Unable to get post from topic ID {} in forum ID {} from database!", t.getForumID(), t.getID(), e);
		}
	}
	
	/**
	 * @param i
	 */
	public void updatetxt(int i)
	{
		final CPost cp = getCPost(i);
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE posts SET post_txt=? WHERE post_id=? AND post_topic_id=? AND post_forum_id=?"))
		{
			ps.setString(1, cp.postTxt);
			ps.setInt(2, cp.postId);
			ps.setInt(3, cp.postTopicId);
			ps.setInt(4, cp.postForumId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warn("Unable to store post ID {} in database!", cp.postId, e);
		}
	}
}
