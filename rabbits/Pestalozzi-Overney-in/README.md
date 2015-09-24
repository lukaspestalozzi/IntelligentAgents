The Rabbits Grass simulation

The Rabbits Grass simulation is a simulation of an ecosystem: rabbits wander around randomly on a discrete grid 
environment on which grass is growing randomly. When a rabbit bumps into some grass, it eats the grass and gains energy. 
If a rabbit gains enough energy, it reproduces. The reproduction takes some energy so the rabbit can not reproduce twice 
within the same simulation step. If the rabbit doesn't gain enough energy, it dies. The grass can be adjusted to grow at 
different rates and give the rabbits differing amounts of energy. It has to be possible to fully control the total amount 
of grass being grown at each simulation step. The model can be used to explore the competitive advantages of these 
variables.

This model has been described at http://ccl.northwestern.edu/netlogo/models/RabbitsGrassWeeds for the NetLogo simulation 
toolkit. You final application should look like the following applet: 
http://ccl.northwestern.edu/netlogo/models/run.cgi?RabbitsGrassWeeds.824.567, without the weeds.

You have to program the Rabbits Grass Simulation in RePast, using the following requirements:

Grid : the size of the world should be changeable. The default is a 20x20 grid. The world has no borders on the edges 
(thus, it is a torus).
Collisions : different rabbits cannot stay on the same cell.
Legal moves : only one-step moves to adjacent cells (north, south, east and west) are allowed.
Eat condition : a rabbit can eat grass when it occupies the same cell.
Communication : we assume that agents can not communicate with one another.
Visible range and directions : all rabbits are blind and move randomly.
Creation: at their births, rabbits are created at random places
Implement sliders for the following variables of the simulation:

-> Grid size
-> The number of rabbits defines the initial number of rabbits
-> The birth threshold of rabbits defines the energy level at which the rabbit reproduces.
-> The grass growth rate controls the rate at which grass grows (total amount of grass added to the whole world within one 
simulation tick).
Optional: Create a population plot to observe the evolution of the rabbits and the grass.

=====================

Please under no circumstances change the file Manifest.txt. If you do then
I will not be able to run your program without additional tweaking. If
your directory structure is not the same as mine then you will not be
able to run the jar file. This is perfectly normal behaviour.

The most straightforward approach is to implement code in the
subdirectory of the src directory. Please note there are some skeleton
files already there. 

If you want to run your program on your system which has a different
directory structure then classpath variable should point to repast.jar file in 
a directory RepastJ.

Compilation Example :

javac -sourcepath src -cp ../../../../Repast-3.1/RepastJ/repast.jar src/RabbitsGrassSimulationModel.java

javac -sourcepath src: -cp ../../../../Repast-3.1/RepastJ/repast.jar MainRabbit.java

Note : repast.jar uses internally jar files from RepastJ directory,
copying repast to other directory (.e.g. LogistPlatform/lib/) and
attempting to set the classpath to the file there will cause problems
as it will not be able to find some of its own external jar files.

Run Example :

java -cp ../../../../Repast-3.1/RepastJ/repast.jar:.:src MainRabbit


Note : Please notice that CLASSPATH (cp) is also augmented with path
"." as it needs to find precompiled MainReactive.class file.


Directory structure to make your jar file work (given fixed! manifest) :

CourseIntelligentAgents/Repast-3.1 - with Repast

CourseIntelligentAgents/LogistPlatform/LogistPlatform - with LogistPlatform, 
which will be made available for the second exercise.

CourseIntelligentAgents/LogistPlatform/Assignments/rabbits/lastname/
- with the files you have received for rabbits exercise plus
your own code.

In order to submit the solution, simply run the script pack.sh (or
execute the command as given in a script) with your lastnames
(e.g. Boi-Szymanek) while being in the same directory as script
pack.sh .

MainRabbit :

This is an entry point for the jar file. Please check the content
of this file and change accordingly to make sure it executes the
main function of your implementation.

Tips for Eclipse users : 

Choose path to Assignments/rabbits as your workspace. Create a project
named lastname. Add all neccessary jar files, repast.jar, colt.jar,
trove.jar, logist.jar, jdom.jar, commons-math-1.1.jar, and
log4j-1.2.15.jar to your build path as external jar files. Now, you
should be able change skeleton files in src directory and run your
code by executing the MainRabbit.java program.

