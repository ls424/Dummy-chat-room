##Introduction
This is a dummy java program which implements socket.io and multithreading to realize multi-user communication.

###Compilation:
#####1.When compiling the server part, in your cmd, you should type in "javac Server.java"(no "" when doing real operation) 
#####2.To run your sever, type in "java Server"(no "" when doing real operation)
#####3.For the user part, type in "javac User.java"
#####4.To run your user, type in "java User" 
#####5.To run different user, open several terminal to run the class file like step 4.
#####6.The default  portNumber is 8000 and hostname is "localhost".

##Usage

####Boradcast: Simply type in some words and send it out. If it starts with @, it might be send out as unicast.

####Unicast: The message should start with "@"+username+ " "+ message(for example, @123 message), or if might be send out as broadcast. 

###Special Format
#####Send friend request: #friendme @Username, it should start with"#friendme @" and spaces between "@" and username are not allowed, spaces after the username is allowed.
#####Friendship confirmation: @username #friends, there must be and only be one " " bwtween"@username" and "#friends", here @username refers to the one who want to make friend with you. If not following the rule, the message will be sent out according its format or it will just be an notification to user. It depends.
#####Unfriend: @username #unfriend, there must be and only be one " " bwtween"@username" and "#unfriend", here @username refers to the one who want to unfriend with.If not following the rule, the message will be sent out according its format or it will just be an notification to user. It depends.

##Special situation
You could make friends with yourself.
When one user exits, the server concole will indicate "java.net.SocketException: socket closed", it won't impact the server anyway, so I don't handle it.
When there are no open seats for new coming user, the user side will get NullPointerException. Since it need to rerun the program to enter the chat room anyway, I don't handle it.
