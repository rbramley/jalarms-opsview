package com.solab.alarms.channels;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

import com.solab.alarms.*;
import com.googlecode.jsendnsca.*;
import com.googlecode.jsendnsca.builders.MessagePayloadBuilder;

/**
 * A channel to send passive check data to Nagios.
 *
 * @author Robin Bramley
 */
class NagiosPassiveCheckChannel extends AbstractAlarmChannel {    

	private String hostname;
	
    private NagiosSettings settings;

	private Map<String, String> sources;

	public void setSettings(NagiosSettings settings) {
		this.settings = settings;
	}
	
	/**
	 * You must set source-servicecheck mappings for sources that should be sent to Nagios (as it needs a 
	 * corresponding servicecheck). 
	 */
	public void setSources(Map<String,String> value) {
		sources = value;
	}

	/** 
	 * Set a hostname.
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
    @Override
    protected Runnable createSendTask(String msg, String source) {
		if (!hasSource(source)) {
			return null;
		}
        return new NscaTask(settings, hostname, msg, sources.get(source));
    }

    @Override
    protected boolean hasSource(String alarmSource) {
		return sources != null && sources.containsKey(alarmSource);
    }
    
	/**
	 * Runnable task to invoke NSCA transmission.
	 */
    private class NscaTask implements Runnable {
        private final NagiosSettings settings;
        private final String msg;
        private final String src;
		private final String host;

        private NscaTask(NagiosSettings settings, String hostname, String message, String src) {
            this.settings = settings;
			this.host = hostname;
            this.msg = message;
            this.src = src;
        }

        public void run() {
            try {
				MessagePayload payload = new MessagePayloadBuilder()
					// alternatively use .withLocalHostname() or withCanonicalHostname
					.withHostname(host)
					.withLevel(Level.CRITICAL)
					.withServiceName(src)
					.withMessage(msg)
					.create();

				NagiosPassiveCheckSender sender = new NagiosPassiveCheckSender(settings);

				log.debug("Sending: " + payload.toString());
                sender.send(payload);
            } catch (UnknownHostException uhe) {
				log.error("Sending alarm to Nagios", uhe);
            } catch (NagiosException ne) {
				log.error("Sending alarm to Nagios", ne);
            } catch (IOException ioe) {
				log.error("Sending alarm to Nagios", ioe);
            }
        }
    }
}