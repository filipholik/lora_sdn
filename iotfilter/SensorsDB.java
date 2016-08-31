package net.floodlightcontroller.iotfilter;

import java.util.ArrayList;
import java.util.List;

import org.projectfloodlight.openflow.types.DatapathId;

class SensorsDB {
	//private PriorityQueue<SensorData> sensorsDB;
	private List<SensorData> sensorsDB; 
	
	
	
	public SensorsDB() {
		super();
		sensorsDB = new ArrayList<SensorData>(); 
	}

	public void insertSensorData(SensorData data){
		if(data.getGwDetected().compareTo(DatapathId.of("00:00:00:00:00:00:00:04")) == 0) return; //S4 - not a GW
		if(data.getSensorID() == 0) return; //Returning ping traffic - have to be allowed, testing only
		
		SensorData sensorDataExists = getSensorData(data); 
		if(sensorDataExists == null)
			sensorsDB.add(data);
		else
			increaseSensorCounter(data);
	}
	
	protected void increaseSensorCounter(SensorData data){
		for(int i = 0; i < sensorsDB.size(); i++){
			SensorData sensorD = sensorsDB.get(i); 
			if(sensorD.getSensorID() == data.getSensorID() && sensorD.getGwDetected() == data.getGwDetected()){
				sensorD.increasePacketsCaptured();
				return; 
			}
		}		
	}
	
	protected SensorData getSensorData(SensorData data){
		for(int i = 0; i < sensorsDB.size(); i++){
			SensorData sensorD = sensorsDB.get(i); 
			if(sensorD.getSensorID() == data.getSensorID() && sensorD.getGwDetected() == data.getGwDetected()){
				return sensorD;
			}
		}		
		return null; 		
	}
	
	public String printSensorDB(){
		String output = "\n ---SensorsDB--- \n";
		
		for(int i = 0; i < sensorsDB.size(); i++){
			SensorData sensorD = sensorsDB.get(i); 
			output += "ID: "+ sensorD.getSensorID(); 
			output += ", GW: " + sensorD.getGwDetected(); 
			output += ", packets: " + sensorD.getPacketsCaptured(); 
			if(sensorD.isForwardingEnabled())
				output += " ENABLED"; 
			else
				output += " DISABLED"; 
			output += "\n"; 
		}		
		output += "--- \n"; 
		return output; 
	}
	
	private void disableDuplicates(){
		List<Integer> checkedIDs = new ArrayList<Integer>();
		
		for(int i = 0; i < sensorsDB.size(); i++){
			SensorData sensorD = sensorsDB.get(i); 
			if(checkedIDs.contains(sensorD.getSensorID())) continue; //This sensor already checked
			int countMax = sensorD.getPacketsCaptured(); 
			int maxPosition = i; 
			for(int j = i; j < sensorsDB.size(); j++){
				if(sensorsDB.get(j).getSensorID() == sensorD.getSensorID() && 
						sensorsDB.get(j).getPacketsCaptured() > countMax){
					sensorD = sensorsDB.get(j);
					countMax = sensorsDB.get(j).getPacketsCaptured(); 
					maxPosition = j; 
				}
			}
			sensorsDB.get(maxPosition).setForwardingEnabled(true);
			//Max found, can disable all others 
			for(int j = 0; j < sensorsDB.size(); j++){
				if(sensorsDB.get(j).getSensorID() == sensorD.getSensorID() && 
						sensorsDB.get(j).getPacketsCaptured() < countMax){
					sensorsDB.get(j).setForwardingEnabled(false);
				}
			}
			checkedIDs.add(sensorD.getSensorID()); 
		}
	}
	
	public List<SensorData> getDisabledDuplicates(){
		List<SensorData> disabledDups = new ArrayList<SensorData>(); 
		disableDuplicates();
		for(int i = 0; i < sensorsDB.size(); i++){
			SensorData sensorD = sensorsDB.get(i);
			if(!sensorD.isForwardingEnabled()) 
				disabledDups.add(sensorD); 
		}		
		return disabledDups; 
	}
	
}
