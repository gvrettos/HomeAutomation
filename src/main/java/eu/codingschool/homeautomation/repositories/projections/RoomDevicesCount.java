package eu.codingschool.homeautomation.repositories.projections;

import eu.codingschool.homeautomation.model.Room;

public class RoomDevicesCount {

    private Room room;
    private long userDevicesCount;

    public RoomDevicesCount(Room room, long userDevicesCount) {
        this.room = room;
        this.userDevicesCount = userDevicesCount;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public long getUserDevicesCount() {
        return userDevicesCount;
    }

    public void setUserDevicesCount(long userDevicesCount) {
        this.userDevicesCount = userDevicesCount;
    }
}
