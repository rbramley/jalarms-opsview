The code in this repository provides [Opsview](http://www.opsview.com) support for [Jalarms](http://jalarms.sourceforge.net/).
This relies on the [JSend NSCA](http://jsendnsca.googlecode.com/) Open Source library - version 2.0.1 at time of writing.

This is intended to be contributed to Jalarms so it is provided under the same LGPL 2.1 license (and their package).
I've also created a patch for the jalarms Ant build so that the _build-base_ target will skip this channel (as it has an external dependency).

**NagiosPassiveCheckChannel** requires the following settings:
- settings - com.googlecode.jsendnsca.NagiosSettings
- hostname - this is the hostname as configured in Opsview
- sources - a map of Jalarms source names to Opsview service check names (to allow for multiple jalarm channels).

**Runtime dependencies**
- jsendnsca-2.0.1.jar
- as per Jalarms (see <https://jalarms.svn.sourceforge.net/svnroot/jalarms/eclipse-project/lib/jarlist.txt>)

**Example usage** from _Groovy_:
    import com.solab.alarms.*
    import com.solab.alarms.channels.*
    import com.googlecode.jsendnsca.*
    import com.googlecode.jsendnsca.encryption.Encryption;
    
    def settings = new NagiosSettings()
    settings.nagiosHost = '192.168.0.127'
    settings.password = 'changeme'
    settings.encryption = Encryption.TRIPLE_DES
    
    def channel = new NagiosPassiveCheckChannel()
    channel.settings = settings
    channel.hostname = '192.168.0.7'
    channel.sources = [Test:'Java Application Health']
    
    def sender = new AlarmSender()
    sender.alarmChannels = [channel]
    
    sender.sendAlarm('Application alert', 'Test')
    
    // allow a little time for the runnable to execute
    Thread.sleep(5000)
    
    sender.shutdown()


Watch out for a forthcoming blog article on <http://leanjavaengineering.wordpress.com>.
