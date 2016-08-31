package net.floodlightcontroller.iotfilter;

import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv6FlowLabel;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class IoTResource extends ServerResource {
    protected static Logger log = (Logger) LoggerFactory.getLogger(IoTResource.class);
    
    @Get("json")
    public String ListIoT() {   	 
        String param = (String) getRequestAttributes().get("switch");
        return "Hello world, param: " + param; 
        //IoTv6.coll.getCapturedPackets(dpid, match);
    	//return "Hello world " + IoTv6.coll.getCapturedPackets(DatapathId.of(param), null); 
    }   
}
