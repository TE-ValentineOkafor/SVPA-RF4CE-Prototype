/* 
 * Universal Electronics Inc. 
 * Copyright 1999-2014 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a 
 * retrieval system, or transmitted by any means without prior written 
 * Permission of Universal Electronics Inc. 
 */
package com.sony.svpa.rf4ceprototype.uei;

public class IRCommand {

	public enum IRType
	{
		SendIR,
		StopIR,
	}
	
	/** Device ID */
	public int DeviceId = 0;

	/** Key ID */
	public int KeyId = 0;

	/** IR Type */
	public IRType Type = IRType.SendIR;
	
	/** timestamp */
	public long Timestamp = 0;


	/**
	 * Instantiates a new IR command.
	 * 
	 * @param deviceId
	 *            the Device Id
	 * @param key
	 *            Function key Id
	 * @param type
	 *            Flag for StopIR
	 */
	public IRCommand(int deviceId, int key, IRType type) {
		this.DeviceId = deviceId;
		this.KeyId = key;
		this.Type = type;
	}
	
	/**
	 * Checks if is stop ir.
	 *
	 * @return true, if is stop ir
	 */
	public boolean isStopIR()
	{
		return (this.Type == IRType.StopIR);
	}
}