# JSMod

JSMod further democratises the **Minecraft modding** world by bringing the superpowers of **JavaScript** to Minecraft. With JSMod lightweight mods can be written and created in **modern ES6 JS** and updated during play (hot reload). 

The JS code is then executed using the state of the art JavaScript runtime **V8**. It's also worth noting that **JSMod** itself is a **Forge mod**, which means that it can be used alongside other mods.



### Architecture

JSMod only provides the link between Minecraft's Java codebase and your JavaScript code, but also includes optional JavaScript files which make coding modules and interacting with the Java side a lot easier.

![](https://raw.githubusercontent.com/LavaAfterburner/JSMod/master/diagrams/architecture.png)

JSMod calls the event methods (such as onTick) and creates the basic namespaces required for communicating between Java and JS. The optional JS files aren't strictly necessary, but add more namespaces (e.g. `Engine` for modules) and constants (e.g. `KEY_A` for key codes, `Chat.RED` for chat colours and effects)



### Getting started

1. Make sure you have the correct Minecraft version and the corresponding forge version installed

2. **Download** the mod jar file (e.g `jsmod-0.0.1.jar`) and the js files (e.g. `js-0.0.1.zip`) from the [Releases](https://github.com/LavaAfterburner/JSMod/releases) page

3. Install the mod by **moving** the downloaded **jar** file to the forge **mods folder** (On Windows`C:\Users\...\AppData\Roaming\.minecraft\mods`)

4. Set up your coding environment
   
   1. **Find** (or create) **a folder** that you want to use to start coding your JS mods (e.g. `C:\Users\...\Documents\js`) 
   
   2. **Unzip** the JavaScript files zip and copy the contained files into the folder
   
   3. Now you can start coding in the `src` subdirectory

5. **Launch** Minecraft

6. In the main menu, click on `Mods`, scroll down until you find `JSMod`, click on it and select `Config`. Change the `Root file path` field to the file path of your coding environment for the JS mods (e.g. `C:\Users...\Documents\js`)



**And you're ready to go! **



Updating the code at runtime: JSMod supports a hot reload system which let's you make changes to your code and reload without having to restart Minecraft. This system even preserves the state of your modules between reloads.

To trigger a reload call the `/hotreload` command in Minecraft.
