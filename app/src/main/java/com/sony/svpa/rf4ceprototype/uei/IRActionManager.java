/* 
 * Universal Electronics Inc. 
 * Copyright 1999-2014 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a 
 * retrieval system, or transmitted by any means without prior written 
 * Permission of Universal Electronics Inc. 
 */
package com.sony.svpa.rf4ceprototype.uei;

import android.os.RemoteException;
import android.util.Log;

import com.uei.control.ISetup;
import com.uei.control.ResultCode;
import com.uei.encryption.helpers.CallerHelper;
import com.uei.quickset.QuickSet;
import com.uei.quickset.QuickSetCompatManager;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * IR Action command manager
 */
public class IRActionManager {

	/** The Singleton. */
	private static IRActionManager _Singleton = null;

	/** The command queue. */
	private ArrayList<IRCommand> _commandQueue = new ArrayList<IRCommand>();

	/** The command queue lock. */
	final private Lock _commandQueueLock = new ReentrantLock();

	/** The authenticator. */
	private CallerHelper _authenticator = new CallerHelper();

	/** The thread flag. */
	private boolean _working = false;

	/** Command execution thread */
	private CommandThread _commandThread = null;

	/**
	 * Constructor
	 */
	public IRActionManager() {
		IRActionManager._Singleton = this;
	}

	/**
	 * Gets Singleton instance.
	 * 
	 * @return IRActionManager instance.
	 */
	public static IRActionManager getSingleton() {
		return IRActionManager._Singleton;
	}

	/**
	 * Get next IRCommand to be executed
	 * 
	 */
	private IRCommand getNextIRCommand() {
		IRCommand command = null;
		this._commandQueueLock.lock();
		try {
			if (this._commandQueue.size() > 0) {
				command = this._commandQueue.remove(0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			this._commandQueueLock.unlock();
		}
		
		return command;
	}

	/**
	 * Add new IRCommand to queue. Queue will only queue for one additional
	 * command.
	 * It will only queue additional one SendIR and StopIR commands. The rest
	 * commands will not be queued.
	 * 
	 */
	public boolean addIRCommand(IRCommand command) {
		boolean success = false;

		if (command != null) {
			
			this._commandQueueLock.lock();
			try {
				// get current command count in queue
				int count = this._commandQueue.size();
				boolean add = false;
				// if there at least one pending command
				if(count == 1) {					
					// if last command is not the same type of new command
					if ( command.isStopIR() != this._commandQueue.get(0).isStopIR()) {
						// allow to add StopIR command after a SendIR 
						// or SendIR command after a StopIR						
						add = true;					
					}						
				} else if(count == 2) {					
					// only add the stop IR command 
					// if last command is Send IR
					if ( command.isStopIR() 
							&& this._commandQueue.get(count-1).isStopIR() == false) {												
						add = true;					
					}						
				} else if(count == 0) {
					add =  true;
				} 
								
				if (add) {
					// add new IR command to queue
					this._commandQueue.add(command);
					success = true;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				this._commandQueueLock.unlock();
			}
		}
		return success;
	}

	/**
	 * Start command manager
	 */
	public void startManager() {
		startThread();
	}

	/**
	 * Stop command manager.
	 */
	public void stopManager() {
		stopThread();
	}

	/**
	 * Start execution thread.
	 */
	private void startThread() {
		try {
			stopThread();
			this._working = true;
			this._commandThread = new CommandThread();
			this._commandThread.start();
		} catch (Exception ex) {
			Log.e(QuicksetSampleApplication.LOGTAG,
					"startThread: " + ex.toString());
		}
	}

	/**
	 * Stop execution thread.
	 */
	private void stopThread() {
		try {
			if (this._commandThread != null) {
				this._working = false;
				this._commandThread = null;
			}
		} catch (Exception ex) {
			Log.e(QuicksetSampleApplication.LOGTAG,
					"stopThread: " + ex.toString());
		}
	}

	/** Generate authentication key to access Setup service. **/
	private String getAuthenticationKey() {
		final QuickSetCompatManager compatManager = (QuickSetCompatManager) QuickSet.getInstance().getManager(QuickSet.QUICKSET_COMPAT_MGR);
		return compatManager.getAuthenticationKey();
	}

	/**
	 * CommandThread class to execute IR commands.
	 * 
	 */
	private class CommandThread extends Thread {
		@Override
		public void run() {
			super.run();

			try {
				while (IRActionManager.this._working) {

					try {
						IRCommand command = getNextIRCommand();
						ISetup setup = QuicksetSampleApplication.getSetup();
						int result = 0;
						if (command != null && setup != null) {
							
								try {
									switch(command.Type) {
										case SendIR:
											Log.d(QuicksetSampleApplication.LOGTAG,
													" -- testStartIrFunction: Id = "
															+ command.KeyId);
											result = QuicksetSampleApplication
													.getSetup()
													.testFunctionStartIR(
															QuicksetSampleApplication
																	.getSession(),
															getAuthenticationKey(),
															command.KeyId);
											break;
										case StopIR: {
											Log.d(QuicksetSampleApplication.LOGTAG,
													" -- testStopIrFunction");
											result = QuicksetSampleApplication
													.getSetup()
													.testFunctionStopIR(
															QuicksetSampleApplication
																	.getSession(),
															getAuthenticationKey());
										}
										break;
									}
								} catch (RemoteException e) {
									e.printStackTrace();
									Log.d(QuicksetSampleApplication.LOGTAG,
											"Stop IR failed: " + e.getMessage());
								}

							if (result != ResultCode.SUCCESS) {
								Log.e(QuicksetSampleApplication.LOGTAG,
										"Result =  "
												+ ResultCode.getString(result));
								if (result == ResultCode.INVALID_SESSION) {
									// invalid session! renew a new one.
									QuicksetSampleApplication.renewSession();
								}
							}
						}
					} catch (Exception e) {
						Log.e(QuicksetSampleApplication.LOGTAG,
								"IR execution: " + e.toString());
					}

					try {
						Thread.sleep(10);
					} catch (Exception ex) {
						Log.e(QuicksetSampleApplication.LOGTAG, ex.toString());
					}
				}
			} catch (Exception ex) {
				Log.e(QuicksetSampleApplication.LOGTAG, ex.toString());
			}
		}
	}
}
