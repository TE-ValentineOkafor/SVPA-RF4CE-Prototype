/*
 * Universal Electronics Inc.
 * Copyright 1999-2018 by Universal Electronics Inc.
 * All right reserved. No part of this work may be reproduced, stored in a
 * retrieval system, or transmitted by any means without prior written
 * Permission of Universal Electronics Inc.
 */
package com.sony.svpa.rf4ceprototype.uei;

/**
 *
 */
public interface PairingUserPromptCallback {
    public void proceedWithInitialization();
    public void proceedWithPairing(final String deviceId);
    public void stopPairing();

}
