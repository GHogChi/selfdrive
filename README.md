#Self-driving Automotive System

##Issues
###Speeding?
According to the requirements, a valid Speed Limit Sign sensor event should
cause the speed to be raised to the value indicated
for the drive mode. The value for SPORT mode
is "X+5". This appears to mean that the system is
being told to drive five miles above the limit. 
This is the way it is implemented, because no other
interpretation suggested itself. 