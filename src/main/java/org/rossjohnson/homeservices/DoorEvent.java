package org.rossjohnson.homeservices;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class DoorEvent {

    @Id
    @GeneratedValue
    private Long id;

    private String doorName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss a z", timezone = "CDT")
    private Date eventDate;

    private Type type;

    private String homeName;

    // JPA-only
    DoorEvent() {
    }

    public DoorEvent(String homeName) {
        this.homeName = homeName;
    }
    // for Examples only
    public DoorEvent(String homeName, String doorName, Type type) {
        this.doorName = doorName;
        this.homeName = homeName;
        this.type = type;
    }

    // for Examples only
    public DoorEvent(String homeName, String doorName) {
        this.doorName = doorName;
        this.homeName = homeName;
    }

    public DoorEvent(String homeName, String doorName, Type type, Date eventDate) {
        this.doorName = doorName;
        this.homeName = homeName;
        this.type = type;
        this.eventDate = eventDate;
    }

    public DoorEvent(String doorName, Type type, Date eventDate) {
        this.homeName = "kerriell";
        this.doorName = doorName;
        this.type = type;
        this.eventDate = eventDate;
    }


    public Long getId() {
        return id;
    }

    public String getDoorName() {
        return doorName;
    }

    public void setDoorName(String doorName) {
        this.doorName = doorName;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public enum Type {
        opened, closed
    }

    @Override
    public String toString() {
        return "DoorEvent{" +
                "id=" + id +
                ", doorName='" + doorName + '\'' +
                ", eventDate=" + eventDate +
                ", type=" + type +
                ", homeName='" + homeName + '\'' +
                '}';
    }
}
