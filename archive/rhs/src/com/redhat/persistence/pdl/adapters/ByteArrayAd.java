package com.redhat.persistence.pdl.adapters;

/**
 * ByteArrayAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/05/02 $
 **/

public class ByteArrayAd extends BlobAd {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/pdl/adapters/ByteArrayAd.java#1 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    public ByteArrayAd() {
        super("global.byte[]");
    }

}
