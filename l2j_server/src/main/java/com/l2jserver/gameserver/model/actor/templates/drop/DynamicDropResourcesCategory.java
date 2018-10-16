package com.l2jserver.gameserver.model.actor.templates.drop;

import com.l2jserver.gameserver.model.items.craft.ResourceGrade;

import java.util.Objects;
import java.util.StringJoiner;

public class DynamicDropResourcesCategory {

    private ResourceGrade resourceGrade;
    private DynamicDropCategory dynamicDropCategory;

    public DynamicDropResourcesCategory(ResourceGrade resourceGrade, DynamicDropCategory dynamicDropCategory) {
        this.resourceGrade = resourceGrade;
        this.dynamicDropCategory = dynamicDropCategory;
    }

    public ResourceGrade getResourceGrade() {
        return resourceGrade;
    }

    public DynamicDropCategory getDynamicDropCategory() {
        return dynamicDropCategory;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DynamicDropResourcesCategory.class.getSimpleName() + "[", "]")
                .add(Objects.toString(resourceGrade))
                .add(Objects.toString(dynamicDropCategory))
                .toString();
    }

}
