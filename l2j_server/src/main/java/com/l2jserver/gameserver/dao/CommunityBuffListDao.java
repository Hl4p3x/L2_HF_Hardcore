package com.l2jserver.gameserver.dao;

import com.l2jserver.common.database.pool.impl.ConnectionFactory;
import com.l2jserver.gameserver.data.sql.impl.CommunityBuffList;
import com.l2jserver.gameserver.data.sql.impl.CommunityBuffListMapper;
import com.l2jserver.gameserver.data.sql.impl.SkillHolderMapper;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunityBuffListDao {

    private static final Logger LOG = LoggerFactory.getLogger(CommunityBuffListDao.class);

    private static final String CREATE_BUFF_LIST = "INSERT INTO community_buff_lists (owner_id, list_name) VALUES (:owner_id, :list_name)";
    private static final String ADD_BUFF_TO_LIST = "INSERT INTO community_buff_items (list_id, skill_id, skill_level) VALUES (:list_id, :skill_id, :skill_level)";

    private static final String DELETE_BUFF_LIST = "DELETE FROM community_buff_lists WHERE owner_id=:owner_id AND list_name=:list_name";
    private static final String DELETE_ALL_BUFF_ITEMS = "DELETE FROM community_buff_items WHERE list_id=:list_id";
    private static final String DELETE_BUFF_ITEM_SKILL = "DELETE FROM community_buff_items WHERE list_id=:list_id AND skill_id=:skill_id";

    private static final String SELECT_BUFF_LISTS = "SELECT list_id, owner_id, list_name FROM community_buff_lists";
    private static final String SELECT_BUFF_LISTS_BY_OWNER = SELECT_BUFF_LISTS + " WHERE owner_id=:owner_id";
    private static final String SELECT_BUFF_LIST_BY_OWNER_AND_NAME = SELECT_BUFF_LISTS + " WHERE owner_id=:owner_id AND list_name=:list_name";
    private static final String SELECT_BUFF_LIST_BY_OWNER_AND_ID = SELECT_BUFF_LISTS + " WHERE owner_id=:owner_id AND list_id=:list_id";

    private static final String SELECT_BUFF_ITEMS = "SELECT list_id, skill_id, skill_level FROM community_buff_items WHERE list_id=:list_id";

    public void createCommunityBuffList(CommunityBuffList communityBuffList) {
        Jdbi jdbi = Jdbi.create(ConnectionFactory.getInstance().getDataSource());
        jdbi.useTransaction(h -> {
            h.createUpdate(CREATE_BUFF_LIST)
                    .bind("owner_id", communityBuffList.getOwnerId())
                    .bind("list_name", communityBuffList.getName())
                    .execute();

            int listId = h.createQuery("SELECT LAST_INSERT_ID() id")
                    .map((rs, context) -> rs.getInt("id"))
                    .findOnly();

            if (!communityBuffList.getSkills().isEmpty()) {
                PreparedBatch batch = h.prepareBatch(ADD_BUFF_TO_LIST);
                communityBuffList.getSkills().forEach(skillHolder -> batch
                        .bind("list_id", listId)
                        .bind("skill_id", skillHolder.getSkillId())
                        .bind("skill_level", skillHolder.getSkillLvl())
                        .add());

                batch.execute();
            }
        });
    }

    public Optional<CommunityBuffList> findPlayerBuffListById(int listId, int ownerId) {
        Jdbi jdbi = Jdbi.create(ConnectionFactory.getInstance().getDataSource());
        jdbi.registerRowMapper(new CommunityBuffListMapper());

        return jdbi.withHandle(h -> h.createQuery(SELECT_BUFF_LIST_BY_OWNER_AND_ID)
                .bind("owner_id", ownerId)
                .bind("list_id", listId)
                .mapTo(CommunityBuffList.class)
                .stream().map(buffList ->
                        new CommunityBuffList(
                                buffList.getId(),
                                buffList.getOwnerId(),
                                buffList.getName(),
                                Collections.emptyList())
                )
                .findFirst()
        );
    }

    public List<SkillHolder> findAllBuffListItems(int listId) {
        Jdbi jdbi = Jdbi.create(ConnectionFactory.getInstance().getDataSource());
        jdbi.registerRowMapper(new SkillHolderMapper());
        return jdbi.withHandle(h -> h.createQuery(SELECT_BUFF_ITEMS).bind("list_id", listId).mapTo(SkillHolder.class).list());
    }

    public List<CommunityBuffList> findAllCommunityBuffSets(int ownerId) {
        Jdbi jdbi = Jdbi.create(ConnectionFactory.getInstance().getDataSource());
        jdbi.registerRowMapper(new CommunityBuffListMapper());

        return jdbi.withHandle(h -> h.createQuery(SELECT_BUFF_LISTS_BY_OWNER)
                .bind("owner_id", ownerId)
                .mapTo(CommunityBuffList.class)
                .stream().map(buffList ->
                        new CommunityBuffList(
                                buffList.getId(),
                                buffList.getOwnerId(),
                                buffList.getName(),
                                findAllBuffListItems(buffList.getId()))
                )
                .collect(Collectors.toList())
        );
    }

    public Optional<CommunityBuffList> findSingleCommunityBuffSet(int ownerId, String listName) {
        Jdbi jdbi = Jdbi.create(ConnectionFactory.getInstance().getDataSource());
        jdbi.registerRowMapper(new CommunityBuffListMapper());

        return jdbi.withHandle(h -> h.createQuery(SELECT_BUFF_LIST_BY_OWNER_AND_NAME)
                .bind("owner_id", ownerId)
                .bind("list_name", listName)
                .mapTo(CommunityBuffList.class)
                .stream().map(buffList ->
                        new CommunityBuffList(
                                buffList.getId(),
                                buffList.getOwnerId(),
                                buffList.getName(),
                                findAllBuffListItems(buffList.getId()))
                )
                .findFirst());
    }

    public boolean removeCommunityBuffList(int ownerId, String listName) {
        Jdbi jdbi = Jdbi.create(ConnectionFactory.getInstance().getDataSource());
        return jdbi.inTransaction(h -> {
            Optional<CommunityBuffList> communityBuffList = findSingleCommunityBuffSet(ownerId, listName);
            if (communityBuffList.isEmpty()) {
                return false;
            }

            h.createUpdate(DELETE_ALL_BUFF_ITEMS)
                    .bind("list_id", communityBuffList.get().getId())
                    .execute();
            return h.createUpdate(DELETE_BUFF_LIST)
                    .bind("owner_id", ownerId)
                    .bind("list_name", listName)
                    .execute() > 0;
        });
    }

    public boolean addToCommunityBuffList(int listId, SkillHolder skillHolder) {
        Jdbi jdbi = Jdbi.create(ConnectionFactory.getInstance().getDataSource());
        try {
            return jdbi.withHandle(h -> h.createUpdate(ADD_BUFF_TO_LIST)
                    .bind("list_id", listId)
                    .bind("skill_id", skillHolder.getSkillId())
                    .bind("skill_level", skillHolder.getSkillLvl())
                    .execute()) > 0;
        } catch (RuntimeException e) {
            LOG.error("Failed to add community buff", e);
            return false;
        }
    }

    public boolean removeFromCommunityBuffList(int listId, int skillId) {
        Jdbi jdbi = Jdbi.create(ConnectionFactory.getInstance().getDataSource());
        try {
            return jdbi.withHandle(h -> h.createUpdate(DELETE_BUFF_ITEM_SKILL)
                    .bind("list_id", listId)
                    .bind("skill_id", skillId)
                    .execute()) > 0;
        } catch (RuntimeException e) {
            LOG.error("Failed to remove community buff", e);
            return false;
        }
    }

}

