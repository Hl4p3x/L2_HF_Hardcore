package com.l2jserver.gameserver.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.interfaces.ILocational;
import com.l2jserver.gameserver.model.interfaces.IPositionable;

import java.util.Objects;
import java.util.StringJoiner;

public class NpcLocation implements ILocational {

    private int npcId;
    private Location location;

    public NpcLocation() {
    }

    public NpcLocation(int npcId, Location location) {
        this.npcId = npcId;
        this.location = location;
    }

    public int getNpcId() {
        return npcId;
    }

    @Override
    public int getX() {
        return location.getX();
    }

    public void setX(int x) {
        location.setX(x);
    }

    @Override
    public int getY() {
        return location.getY();
    }

    public void setY(int y) {
        location.setY(y);
    }

    @Override
    public int getZ() {
        return location.getZ();
    }

    public void setZ(int z) {
        location.setZ(z);
    }

    public void setXYZ(int x, int y, int z) {
        location.setXYZ(x, y, z);
    }

    public void setXYZ(ILocational loc) {
        location.setXYZ(loc);
    }

    @Override
    public int getHeading() {
        return location.getHeading();
    }

    public void setHeading(int heading) {
        location.setHeading(heading);
    }

    public void setInstanceId(int instanceId) {
        location.setInstanceId(instanceId);
    }

    public int getInstanceId() {
        return location.getInstanceId();
    }

    public void setLocation(Location loc) {
        location.setLocation(loc);
    }

    @Override
    public IPositionable getLocation() {
        return location.getLocation();
    }

    @JsonCreator
    public static NpcLocation from(@JsonProperty int npcId,
                                   @JsonProperty("location") Location location) {
        return new NpcLocation(npcId, location);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NpcLocation that = (NpcLocation) o;
        return npcId == that.npcId &&
                Objects.equals(location, that.location);
    }


    @Override
    public int hashCode() {
        return Objects.hash(npcId, location);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NpcLocation.class.getSimpleName() + "[", "]")
                .add(Objects.toString(npcId))
                .add(Objects.toString(location))
                .toString();
    }

}
