package com.l2jserver.gameserver.datatables.categorized;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.l2jserver.gameserver.model.items.scrolls.CategorizedScrolls;
import com.l2jserver.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ScrollDropDataTable {


    private static final Logger LOG = LoggerFactory.getLogger(ScrollDropDataTable.class);

    private Set<Integer> scrollIds;
    private CategorizedScrolls categorizedScrolls;

    public ScrollDropDataTable() {
        load();
    }

    public void load() {
        try {
            File itemPartsFile = new File("data/stats/categorized/scrolls.json");
            categorizedScrolls = new ObjectMapper().readValue(itemPartsFile, CategorizedScrolls.class);

            scrollIds = new HashSet<>();
            scrollIds.addAll(CollectionUtil.extractIds(categorizedScrolls.getNormalWeaponScrolls()));
            scrollIds.addAll(CollectionUtil.extractIds(categorizedScrolls.getBlessedWeaponScrolls()));
            scrollIds.addAll(CollectionUtil.extractIds(categorizedScrolls.getNormalArmorScrolls()));
            scrollIds.addAll(CollectionUtil.extractIds(categorizedScrolls.getBlessedArmorScrolls()));
            scrollIds.addAll(CollectionUtil.extractIds(categorizedScrolls.getNormalMiscScrolls()));
            scrollIds.addAll(CollectionUtil.extractIds(categorizedScrolls.getBlessedMiscScrolls()));

            LOG.info("Loaded {} scrolls", scrollIds.size());
        } catch (IOException e) {
            throw new IllegalStateException("Could not read scroll drop data: " + e.getMessage());
        }
    }

    public CategorizedScrolls getCategorizedScrolls() {
        return categorizedScrolls;
    }

    public Set<Integer> getScrollIds() {
        return scrollIds;
    }

    public static ScrollDropDataTable getInstance() {
        return ScrollDropDataTable.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final ScrollDropDataTable _instance = new ScrollDropDataTable();
    }

}
