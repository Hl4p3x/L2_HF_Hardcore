package com.l2jserver.gameserver.datatables.categorized;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.l2jserver.gameserver.model.items.craft.CraftResource;
import com.l2jserver.gameserver.model.items.craft.ResourceGrade;
import com.l2jserver.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CraftResourcesData {

    private static final Logger LOG = LoggerFactory.getLogger(CraftResourcesData.class);

    private List<CraftResource> resources = new ArrayList<>();
    private Set<Integer> resourceIds = new HashSet<>();
    private Map<ResourceGrade, Collection<CraftResource>> resourceMap = new HashMap<>();

    public CraftResourcesData() {
        load();
    }

    private void load() {
        try {
            File craftResourcesFile = new File("data/stats/categorized/craft_resources.json");
            resources = new ObjectMapper().readValue(craftResourcesFile, new TypeReference<List<CraftResource>>() {
            });

            Multimap<ResourceGrade, CraftResource> resourcesMultimap = HashMultimap.create();
            resources.forEach(craftResource -> resourcesMultimap.put(craftResource.getResourceGrade(), craftResource));
            resourceMap = resourcesMultimap.asMap();
            resourceIds = CollectionUtil.extractIds(resources);
            LOG.info("Loaded {} craft resources parts", resourceIds.size());
        } catch (IOException e) {
            throw new IllegalStateException("Could not read craft resources data: " + e.getMessage());
        }
    }

    public List<CraftResource> getResources() {
        return resources;
    }

    public Collection<CraftResource> getResourcesByGrade(ResourceGrade resourceGrade) {
        return Optional.ofNullable(resourceMap.get(resourceGrade)).orElse(Collections.emptyList());
    }

    public List<CraftResource> getResourcesByGrades(Set<ResourceGrade> resourceGrade) {
        return resources.stream().filter(resource -> resourceGrade.contains(resource.getResourceGrade())).collect(Collectors.toList());
    }

    public static CraftResourcesData getInstance() {
        return CraftResourcesData.SingletonHolder._instance;
    }

    public Set<Integer> getResourceIds() {
        return resourceIds;
    }

    private static class SingletonHolder {
        protected static final CraftResourcesData _instance = new CraftResourcesData();
    }

}
