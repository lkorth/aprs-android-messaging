package com.lukekorth.aprs_messaging;

public interface PacketCallback {
	public void received(byte[] packet);
}
