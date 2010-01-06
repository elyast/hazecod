package org.robotframework.jdiameter.mapper;

import java.util.Set;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;

/**
 * @author Eliot
 *
 */
public class AvpPrinter {
    
    AvpTypeResolver typeResolver;
    AvpCodeResolver codeResolver;
    /**
     * @param typeResolver
     *            TypeResolver
     */
    public void setTypeResolver(AvpTypeResolver typeResolver) {
	this.typeResolver = typeResolver;
    }

    /**
     * @param codeResolver
     *            Code resolver
     */
    public void setCodeResolver(AvpCodeResolver codeResolver) {
	this.codeResolver = codeResolver;
    }

    /**
     * @param actual Message to print
     * @return String representation
     * @throws AvpDataException when invalid type
     */
    public String prettyPrint(Message actual) throws AvpDataException {
	StringBuffer printed = new StringBuffer();
	printed.append("Msg{appId=").append(actual.getApplicationId());
	printed.append(",command=").append(actual.getCommandCode());
	printed.append(",e2e=").append(actual.getEndToEndIdentifier());
	printed.append(",hbh=").append(actual.getHopByHopIdentifier());
	printed.append(",isError=").append(actual.isError());
	printed.append(",isProxy=").append(actual.isProxiable());
	printed.append(",isReTransmit=").append(actual.isReTransmitted());
	printed.append(",isRequest=").append(actual.isRequest());
	printed.append(",sessionId=").append(actual.getSessionId());
	printed.append("}\n");
	prettyPrint(printed, actual.getApplicationIdAvps());
	prettyPrint(printed, "", actual.getAvps());
	return printed.toString();
    }

    void prettyPrint(StringBuffer printed, Set<ApplicationId> appIds) {
	printed.append("Application AVPs[");
	for (ApplicationId applicationId : appIds) {
	    printed.append("appId.vendorId=").append(
		    applicationId.getVendorId()).append(",");
	    printed.append("appId.acctId=")
		    .append(applicationId.getAcctAppId()).append(",");
	    printed.append("appId.authId=")
		    .append(applicationId.getAuthAppId()).append("\n");
	}
	printed.append("]\n");
    }

    void prettyPrint(StringBuffer printed, String prefix, AvpSet avps)
	    throws AvpDataException {
	if (avps == null) {
	    return;
	}
	for (Avp avp : avps) {
	    prettyPrint(printed, prefix, avp);
	}
    }

    void prettyPrint(StringBuffer printed, String prefix, Avp avp)
	    throws AvpDataException {
	String avpname = codeResolver.getName(avp.getCode());
	printed.append(prefix).append("avp.name=").append(avpname).append("(")
		.append(avp.getCode()).append(")").append(",");
	printed.append("avp.vendorId=").append(avp.getVendorId()).append(",");
	printed.append("avp.value=").append(typeResolver.getValue(avp)).append(
		"\n");
	DataType avpType = typeResolver.getType(avp.getCode());
	if (DataType.GROUPED.equals(avpType)) {
	    prettyPrint(printed, prefix + "\t", avp.getGrouped());
	}
    }
    
    /**
     * @param avp Avp to be printed
     * @return String representation
     * @throws AvpDataException Sth goes wrong
     */
    public String prettyPrint(Avp avp) throws AvpDataException {
	StringBuffer printed = new StringBuffer();
	prettyPrint(printed, "", avp);
	return printed.toString();
    }
}
