// SPDX-License-Identifier: Apache-2.0
package com.hedera.services.bdd.suites.contract.classiccalls.views;

import static com.hedera.services.bdd.suites.contract.classiccalls.ClassicFailureMode.INVALID_TOKEN_ID_FAILURE;
import static com.hedera.services.bdd.suites.contract.classiccalls.ClassicInventory.INVALID_TOKEN_ADDRESS;

import com.esaulpaugh.headlong.abi.Function;
import com.hedera.services.bdd.spec.HapiSpec;
import com.hedera.services.bdd.suites.contract.classiccalls.AbstractFailableStaticCall;
import com.hedera.services.bdd.suites.contract.classiccalls.ClassicFailureMode;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.math.BigInteger;
import java.util.EnumSet;

public class GetTokenKeyFailableCall extends AbstractFailableStaticCall {

    private static final Function SIGNATURE = new Function("getTokenKey(address,uint)");

    public GetTokenKeyFailableCall() {
        super(EnumSet.of(INVALID_TOKEN_ID_FAILURE));
    }

    @Override
    public String name() {
        return "getTokenKey";
    }

    @Override
    public byte[] encodedCall(@NonNull final ClassicFailureMode mode, @NonNull final HapiSpec spec) {
        throwIfUnsupported(mode);
        return SIGNATURE
                .encodeCallWithArgs(INVALID_TOKEN_ADDRESS, BigInteger.ONE)
                .array();
    }
}
