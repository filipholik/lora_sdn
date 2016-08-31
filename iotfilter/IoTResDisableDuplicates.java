package net.floodlightcontroller.iotfilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.util.AppCookie;
import net.floodlightcontroller.statistics.StatisticsCollector;
import net.floodlightcontroller.util.OFMessageDamper;

import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv6FlowLabel;
import org.projectfloodlight.openflow.types.TableId;
import org.projectfloodlight.openflow.types.U64;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.util.JSONPObject;

import ch.qos.logback.classic.Logger;

public class IoTResDisableDuplicates extends ServerResource {
	
	
	
protected static Logger log = (Logger) LoggerFactory.getLogger(IoTResDisableDuplicates.class);
    
    @Get("json")
    public String disableDuplicates() {   	
    	List<SensorData> flows = StatisticsCollector.getFlowStats(); 
    	flows = markDuplicates(flows); 
    	U64 cookie = IoTv6.DEFAULT_FORWARDING_COOKIE;     
    	
    	Set<OFType> set = new HashSet<OFType>(); 
		set.add(OFType.FLOW_MOD);    		
    	OFMessageDamper md = new OFMessageDamper(100,set,1);  
    	
    	int en = 0;
    	int dis = 0;
    	
    	for(int i = 0; i < flows.size(); i++){
    		SensorData sensorD = flows.get(i);
    		OFFlowMod.Builder fmb;
    		IOFSwitch sw = flows.get(i).getGwDetectedSW(); //.getSwitch(sensorD.getGwDetected());
    		Match.Builder mb = sw.getOFFactory().buildMatch();
    		mb.setExact(MatchField.ETH_TYPE, EthType.IPv6);
    		mb.setExact(MatchField.IPV6_FLABEL, IPv6FlowLabel.of(sensorD.getSensorID()));
    		Match m = mb.build();
    		
    		if(sensorD.isForwardingEnabled()){
    			//Not eligible to disabling
    			//Already disabled flows should be removed!!! 
    			en++;
    			fmb = sw.getOFFactory().buildFlowDelete(); //Strict 
    			fmb.setMatch(m); 
    			fmb.setPriority(10); 
    		}else{
    			//Disabling on a specific GW
    			dis++;         		
        		fmb = sw.getOFFactory().buildFlowAdd();
        		fmb.setActions(new ArrayList<OFAction>()); //DROP
        		fmb.setCookie(cookie); 
        		fmb.setIdleTimeout(30);
        		fmb.setMatch(m); 
        		fmb.setTableId(TableId.ZERO); 
        		fmb.setPriority(10); 
    		}
    		//WRITE
    		md.write(sw, fmb.build());
    	}    		
    	/*JsonFactory factory = new JsonFactory();
    	JsonGenerator jGen = factory.createGenerator(
    		    new File("data/output.json"), JsonEncoding.UTF8);
    	try {
			jGen.writeStartObject();
			jGen.writeStringField("result", "ok");
			jGen.writeString("Duplicates disabled... <br/>Skipped: " + en + " flows, disabled: " + dis + " flows. ");
	        jGen.writeEndObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	jGen.close();*/
    	//String param = (String) getRequestAttributes().get("switch");
    	return "{\"result\" : \"1\", \"text\" : \"Skipped: " + en + " flows, disabled: " + dis + " flows. \"}";
        //return "Duplicates disabled... <br/>Skipped: " + en + " flows, disabled: " + dis + " flows. ";         
    }   
    
    private List<SensorData> markDuplicates(List<SensorData> flows){
    	List<Integer> checkedIDs = new ArrayList<Integer>();
		
		for(int i = 0; i < flows.size(); i++){
			SensorData sensorD = flows.get(i); 
			if(sensorD.getGwDetected().compareTo(DatapathId.of("00:00:00:00:00:00:00:04")) == 0) continue; //Main Switch
			if(sensorD.getSensorID() == 0) continue; //ICMP replies
			if(checkedIDs.contains(sensorD.getSensorID())) continue; //This sensor already checked
			int countMax = sensorD.getPacketsCaptured(); 
			int maxPosition = i; 
			for(int j = i; j < flows.size(); j++){
				if(flows.get(j).getSensorID() == sensorD.getSensorID() && 
						flows.get(j).getGwDetected().compareTo(DatapathId.of("00:00:00:00:00:00:00:04")) != 0 &&
						flows.get(j).getSensorID() != 0 &&
						flows.get(j).getPacketsCaptured() > countMax){
					sensorD = flows.get(j);
					countMax = flows.get(j).getPacketsCaptured(); 
					maxPosition = j; 
				}
			}
			flows.get(maxPosition).setForwardingEnabled(true);
			//Max found, can disable all others 
			for(int j = 0; j < flows.size(); j++){
				if(flows.get(j).getSensorID() == sensorD.getSensorID() && 
						flows.get(j).getGwDetected().compareTo(DatapathId.of("00:00:00:00:00:00:00:04")) != 0 &&
						flows.get(j).getSensorID() != 0 &&
						flows.get(j).getPacketsCaptured() < countMax){
					flows.get(j).setForwardingEnabled(false);
				}
			}
			checkedIDs.add(sensorD.getSensorID()); 
		}
		return flows; 
    }

}
