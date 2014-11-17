package org.intermine.web.task;

/*
 * Copyright (C) 2002-2014 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.intermine.webservice.server.JWTVerifier;
import org.intermine.webservice.server.JWTVerifier.Verification;
import org.intermine.webservice.server.JWTVerifier.VerificationError;

/**
 * A task to check that a token can be verified. Users who plan on using the mechanisms
 * for delegated authentication should check that their tokens will work and that they have
 * configured the system correctly by running this task on a sample of tokens.
 *
 * @author Alex Kalderimis
 *
 */
public class VerifyTokenTask extends KeyStoreTask {

   private String token;

    
    /**
     * Bean-style setter for token, as per ant spec.
     * @param token The token.
     */
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void execute() {
        Properties options = getOptions();
        JWTVerifier verifier = new JWTVerifier(createKeyStore(), options);

        try {
            Verification result = verifier.verify(token);
            logMessage("IDENTITY = " + result.getIdentity());
        } catch (VerificationError e) {
            throw new BuildException("Token failed to verify: " + e.getMessage());
        }
    }

}
