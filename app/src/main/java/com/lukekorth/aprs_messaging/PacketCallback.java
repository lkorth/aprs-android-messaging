package com.lukekorth.aprs_messaging;

public interface PacketCallback {

	void received(byte[] packet);
}
