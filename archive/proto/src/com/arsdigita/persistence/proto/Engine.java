package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;

/**
 * Engine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2003/02/17 $
 **/

public abstract class Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Engine.java#8 $ by $Author: rhs $, $DateTime: 2003/02/17 13:30:53 $";

    protected abstract void commit();

    protected abstract void rollback();

    protected abstract RecordSet execute(Query query);

    protected abstract void write(Event ev);

    protected abstract void flush();

}
