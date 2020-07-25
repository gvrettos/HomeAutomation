# HomeAutomation
A web-based project implemented as part of the Coding School (10 daily-sessions training) during March-April 2018.
It was implemented using Spring Boot.

# Purpose
HomeAutomation is a virtual platform that collects all home devices that can connect to the internet. It enables us to interact and control them remotely.

Sign-up and log-in functionalities ensure the security of the application.
The platform supports 2 different roles of users: Admins and simple Users. Each role is granted specific permissions.

The main functionalities provided are:
* Each user can view a list with all the devices they can control
* They can only interact with their assigned devices by changing a device metric (e.g. temperature) or turn it on/off
* Administrators can also do the following:
    * view a list of all users and update/remove users
    * view a list of all devices and add/remove/update devices
    * view a list of all rooms where devices reside and add/remove/update rooms
    * interact with all devices

# Screenshots
Displaying all rooms where a simple user has access:
![Alt text](01user-all_rooms.PNG "Displaying all rooms where a simple user has access")

Displaying devices accessible to a simple user for a specific room:
![Alt text](02user-specific_room.PNG "Displaying devices accessible to a simple user for a specific room")

Admin has access to the room details of the house:
![Alt text](03admin-rooms-list.PNG "Admin has access to the room details of the house")

Admin can control any device in any manner:
![Alt text](04admin-device-list.PNG "Admin can control any device in any manner")
