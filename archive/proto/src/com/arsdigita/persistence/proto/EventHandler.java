package com.arsdigita.persistence.proto;

/**
 * EventHandler
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/10 $
 **/

public abstract class EventHandler {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/EventHandler.java#1 $ by $Author: rhs $, $DateTime: 2002/12/10 15:09:40 $";

    public abstract void onCreate(CreateEvent e);

    public abstract void onDelete(DeleteEvent e);

    public abstract void onSet(SetEvent e);

    public abstract void onAdd(AddEvent e);

    public abstract void onRemove(RemoveEvent e);

}
