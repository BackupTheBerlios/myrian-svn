package com.redhat.persistence.pdl.adapters;

/**
 * ByteArrayAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public class ByteArrayAd extends BlobAd {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/adapters/ByteArrayAd.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    public ByteArrayAd() {
        super("global.byte[]");
    }

}
