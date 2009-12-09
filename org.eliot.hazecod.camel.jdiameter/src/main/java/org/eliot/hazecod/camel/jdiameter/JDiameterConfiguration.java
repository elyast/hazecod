package org.eliot.hazecod.camel.jdiameter;

import org.jdiameter.server.impl.helpers.EmptyConfiguration;
import static org.jdiameter.server.impl.helpers.Parameters.*;

/**
 * @author Eliot
 *
 */
public class JDiameterConfiguration extends EmptyConfiguration implements
	Cloneable {
    
    static final long SECOND = 1000L;
    static final int ACC_APP_ID = 19302;
    static final int VENDOR_ID = 193;
    static final int TEN = 10;

    /**
     * @param vendorId VendorId
     * @param peerOne PeerOne
     * @param peerTwo PeerTwo
     * @param port Port
     * @param realm Realm
     * @param authAppId AuthAppId
     * @param acctAppId AcctAppId
     */
    public JDiameterConfiguration(int vendorId, String peerOne, String peerTwo, 
	    int port, String realm, int authAppId, int acctAppId) {
	add(Assembler, Assembler.defValue());
	add(OwnDiameterURI, "aaa://" + peerOne + ":" + port);
	add(OwnRealm, realm);
	add(OwnVendorID, vendorId);
	// Set Applications
	add(ApplicationId,
	// AppId 1
		getInstance().add(VendorId, vendorId).
		add(AuthApplId, authAppId).add(AcctApplId, authAppId));
	add(DuplicateProtection, false);
	add(AcceptUndefinedPeer, true);
	// Set peer table
	add(PeerTable,
	// Peer 1
		getInstance().add(PeerRating, 1).add(PeerAttemptConnection,
			true).add(PeerName, "aaa://" 
				+ peerOne + ":" + (port + 1)),
		// Peer 2
		getInstance().add(PeerRating, 1).add(PeerAttemptConnection,
			true).add(PeerName, "aaa://" 
				+ peerTwo + ":" + (port + 2)));
	// Set realm table
	add(RealmTable,
	// Realm 1
		getInstance().add(
			RealmEntry,
			getInstance().add(RealmName, realm).add(
				ApplicationId,
				getInstance().
				add(VendorId, vendorId).
				add(AuthApplId, authAppId).
				add(AcctApplId, acctAppId)).add(RealmHosts,
				peerOne + "," + peerTwo).add(RealmLocalAction,
				"LOCAL").add(RealmEntryIsDynamic, false).add(
				RealmEntryExpTime, SECOND)));
    }

}
