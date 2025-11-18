# Remote shell
A java-based remote shell executor that allows clients connect to server and execute commands
___
## Overwiew
Program consist of 2 components: 
**Server** - server that allows multi-threaded connections, 
**Client** - interactive client for remote shell commands
___
## Instalation
Prerequisites: Java 8+, Internet connection between server and client
```
git clone https://github.com/Krutiwechka/remoteShell.git
cd remoteShell
javac -d out -cp src $(find src -name "*.java")
cd out
```
___


