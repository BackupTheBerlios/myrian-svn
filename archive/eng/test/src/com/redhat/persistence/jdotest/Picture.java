package com.redhat.persistence.jdotest;

/**
 * Picture
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/22 $
 **/

public abstract class Picture {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdotest/Picture.java#1 $ by $Author: vadim $, $DateTime: 2004/06/22 13:25:03 $";

    public abstract int getId();

    public abstract String getCaption();

    public abstract void setCaption(String caption);

    public abstract byte[] getContent();

    public abstract void setContent(byte[] content);

    public String toString() {
        return "<picture #" + getId() + ">";
    }

}
