1 - create user: ./bin/add-user.sh -a -u user -p W3lcome! -g guest
2 - apply server.cli to server
3 - deploy server.jar to server
4 - run jondruse.HiClient8080 - uses plain and shoula authenticate user as "user"
5 - run jondruse.Test - anonymous should fail (as it is sisabled in server.cli), user should work