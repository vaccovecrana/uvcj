# uvcj

JNI wrapper for `libuvc`.

Requires `libuvc` available at `LD_LIBRARY_PATH`. 

---

To use `zig` for c/c++ compilation:

```
sudo update-alternatives --install /usr/bin/g++ cc ~/Applications/zig-linux-x86_64-0.10.1/zig 50
sudo update-alternatives --query cc
sudo update-alternatives --query cc
sudo update-alternatives --remove cc ~/Applications/zig-linux-x86_64-0.10.1/cc
```

---

Usage: java -jar javacpp.jar [options] [class or package (suffixed with .* or .**)] [commands]

where options include:

    -classpath <path>      Load user classes from path
    -encoding <name>       Character encoding used for input and output files
    -d <directory>         Output all generated files to directory
    -o <name>              Output everything in a file named after given name
    -clean                 Delete the output directory before generating anything in it
    -nogenerate            Do not try to generate C++ source files, only try to parse header files
    -nocompile             Do not compile or delete the generated C++ source files
    -nodelete              Do not delete generated C++ JNI files after compilation
    -header                Generate header file with declarations of callbacks functions
    -copylibs              Copy to output directory dependent libraries (link and preload)
    -copyresources         Copy to output directory resources listed in properties
    -configdir <directory> Also create config files for GraalVM native-image in directory
    -jarprefix <prefix>    Also create a JAR file named "<prefix>-<platform>.jar"
    -properties <resource> Load all platform properties from resource
    -propertyfile <file>   Load all platform properties from file
    -D<property>=<value>   Set platform property to value
    -Xcompiler <option>    Pass option directly to compiler

and where optional commands include:

    -clear                 Before doing anything else, delete all files from the cache
    -mod <file>            Output a module-info.java file for native JAR where module name is the package of the first class
    -exec [args...]        After build, call java command on the first class
    -print <property>      Print the given platform property, for example, "platform.includepath", and exit
                           "platform.includepath" has jni.h, jni_md.h, etc, and "platform.linkpath", the jvm library
