SoYouStart Notifier
===================


Description
-----------
This program is a simple tracker for the availability of the popular and cheap SoYouStart dedicated servers from OVH.  
The program allows you to track multiple different server types in multiple zones for availability.  

Features
--------
The program is pretty barren of features as it was purpose built for one task.  
As such all it does it track the configured servers and fire off an email alert once one opens up.  

Usage
-----
Using the program is pretty simple.  
I use Maven to ensure nothing goes wacky during compile.  

Once you've obtained a working version of the program you'll need to setup the config.  

The basic config will look something like this.  

> SMTPPort=587  
> SMTPHostname=smtp.gmail.com  
> SMTPPassword=hackme  
> TargetEmail=change@me.now  
> WatchedServerID=143sys4:bhs, 143sys13:bhs, 143sys1:bhs, 143sys10:bhs  
> SMTPUsername=change@me.too  
> SMTPUseAuthentication=true  


You may have to do some digging to find the server IDs and zone IDs, they're all somewhere on the purchase page, just use Inspect.  

The program checks every minute as to not piss off SoYouStart, change this at if you wish but I don't know what will happen.  

Licence
-------

This program is licenced under the MIT licence and has to warranty.  
<a href="http://opensource.org/licenses/MIT">http://opensource.org/licenses/MIT</a>