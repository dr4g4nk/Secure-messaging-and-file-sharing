# Secure-messaging-and-file-sharing

System has three parts: user app, admin app and central server. All communication goes over central server.

User app is for secure messaging and file sharing between users, overview of application usage and receiving notifications from admin. Messages are sent via secure socket to central server. File sharing is implemented using RMI.

Admin has ability to create new user account and block active ones. Admin can monitor user without user knowledge. Admin can send notifications to users. User monitoring is implemented using sockets. All data are stored in Redis.
