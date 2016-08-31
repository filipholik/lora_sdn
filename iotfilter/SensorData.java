package net.floodlightcontroller.iotfilter;

import net.floodlightcontroller.core.IOFSwitch;

import org.projectfloodlight.openflow.types.DatapathId;

public class SensorData {
	private int sensorID; 
	private DatapathId gwDetected; 
	private int packetsCaptured;
	private boolean forwardingEnabled; 
	
	private IOFSwitch gwDetectedSW; 
	
	public SensorData(int sensorID, DatapathId gwDetected) {
		super();
		this.sensorID = sensorID;
		this.gwDetected = gwDetected;
		this.packetsCaptured = 1; 
		this.forwardingEnabled = true; 
	} 
	
	
	
	public SensorData(int sensorID, DatapathId gwDetected, int packetsCaptured, boolean forwardingEnabled, IOFSwitch gwDetectedSW) {
		super();
		this.sensorID = sensorID;
		this.gwDetected = gwDetected;
		this.packetsCaptured = packetsCaptured;
		this.forwardingEnabled = forwardingEnabled;
		this.gwDetectedSW = gwDetectedSW; 
	}



	public void increasePacketsCaptured(){
		packetsCaptured++; 
	}

	public int getSensorID() {
		return sensorID;
	}

	public void setSensorID(int sensorID) {
		this.sensorID = sensorID;
	}

	public DatapathId getGwDetected() {
		return gwDetected;
	}

	public void setGwDetected(DatapathId gwDetected) {
		this.gwDetected = gwDetected;
	}

	public int getPacketsCaptured() {
		return packetsCaptured;
	}

	public void setPacketsCaptured(int packetsCaptured) {
		this.packetsCaptured = packetsCaptured;
	}

	public boolean isForwardingEnabled() {
		return forwardingEnabled;
	}

	public void setForwardingEnabled(boolean forwardingEnabled) {
		this.forwardingEnabled = forwardingEnabled;
	}



	public IOFSwitch getGwDetectedSW() {
		return gwDetectedSW;
	}



	public void setGwDetectedSW(IOFSwitch gwDetectedSW) {
		this.gwDetectedSW = gwDetectedSW;
	}
	
	
}
