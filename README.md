# Simple-Chat-Room

###Programming Assignment 1: Socket Programming

 This chat program is based on a client server model consisting of one chat server and multiple chat clients over TCP connections.

##### a. Description of my Code
- Authentication: Use for identity validation, read login information from user_pass.txt  
- ChatState: Enumeration type. Use for identifying wheather it is group chat or private chat  
- Message: Wrapper class for message. Use for sending message entity  
- MessageType: Enumeration type. Use for identifying the type of message  
- UserInfo Socket: Wrapper class for user information. Use for identifying users  
- Client: Client class. Supports multiple clients  
- Server: Server class   

##### b. Development Environment
- Programming Language: Java   
- Language Version: 1.6  
- Operating System:	OS X  	
- Software: eclipse 1.53.12  

##### c. Instructions on how to run the code
- Compile all .java files  
- In one terminal, use "java Server port" to start the server  
- In another terminal, use "java Client ip port" to start the client  
- Commands:  
           whoelse: Displays name of other connected users  
           wholast \<number\>: Displays name of those users connected within the last <number> minutes. Let 0 < number < 60  
           broadcast message <message>: Broadcasts <message> to all connected users   
           broadcast user <user> <user>… <user> message <message>: Broadcasts <message> to the list of users   
           message <user> <message>: Private <message> to a <user>   
           logout: Log out this user   


