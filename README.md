# PingMe #

![Pingwin the Penguin - our cute mascot](http://www.fileden.com/files/2011/3/1/3089909//pingvin.png)


*Now all your transit services are just one big button away!*

**PingMe** is a light-weight Android application that allows users to call transit agents like taxis, rickshaws, etc by pressing just a single button. In short, this application can be called a *location relaying service* based on the principles of Instant Messaging. 

## Primary objective ##


These objectives are what define the application, and will not be compromised on. All these objectives will get full priority during the development phase of this project.

The main objectives of this app are:

* Allow users to notify agents of their location
* Allow agents to handle requests with ease
* Route calculation
* Provide emergency facilities for unforeseen difficulties
* Image relaying to send photographs of nearby location

## Secondary objective ##


Secondary objectives, though not very important, would still be good to have. Therefore, if time permits, following features will be incorporated into the system.

The secondary objectives of this app are:

* Providing bus time tables using APIs
* Parental lock to avoid accidental summons
* User feedback to rate transit agents.
* Allow clients to set time-based or location-based reminders
* Agent-specific location relaying
* and many more to come...

## Privacy and security ##


Unlike other location-based services like Foursquare and Latitude, this service *does not* reveal the true identity of the user. This means that the transit agent has no clue of who is summoning him. As such, there is no danger of stalking or harassment as a side-effect of using this app.

Apart from this, the messages sent to server from your phones are encrypted using RSA and further serialized so that no intruder can capture these packets and decode the information.

## Approach ##

The entire PingMe project actually consists of four smaller projects.

#### PingMe Server Project ####
This is the server that will be constantly running on the server machine. Coded in Java, it will perform functions like pushing messages from users to agents and vice versa. The server will not only manage incoming connections but also access the database containing various user profiles.


#### PingMe User Project ####
This is the user-side front end. It will provide a simple and intuitive GUI to the users so that they can summon the transit agents with ease. It will communicate directly with the server but will not communicate directly with the agent-side interface. Location relaying will be done purely by the server by pushing messages.


#### PingMe Agent Project ####
This is the agent-side front end. It will provide a simple, easy to understand GUI along with localization. Like the User end, this will also communicate directly only with the server and not with the User. Messages will be pushed to server which will in turn push messages to user.

#### Generic Classes Project ####
This project is purely dedicated to building a powerful infrastructure for the project to ensure safe and secure information exchange. Features such as Message serialization and data encryption are implemented using classes designed in this project.


## Contributors ##

This is an open source project. Contributors could be anyone with knowledge in Java and/or graphics designing.

The main contributors and initiators of this project are:

* Girish Kamath
* Jamie John
* Pushkar Kulkarni
* Krishna Kothari

Special thanks to 

* Prof. S. N. Gujar for project guidance.
* Android team for making this brilliant SDK available to us for free.
* GitHub for this awesome social coding site.