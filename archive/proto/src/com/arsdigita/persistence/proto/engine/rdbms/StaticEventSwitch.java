package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;
import java.sql.*;

/**
 * StaticEventSwitch
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/19 $
 **/

class StaticEventSwitch extends Event.Switch {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/StaticEventSwitch.java#1 $ by $Author: rhs $, $DateTime: 2003/02/19 22:58:51 $";

    private RDBMSEngine m_engine;

    StaticEventSwitch(RDBMSEngine engine) {
        m_engine = engine;
    }

    private void addOperations(Object obj, Collection blocks) {
        for (Iterator it = blocks.iterator(); it.hasNext(); ) {
            SQLBlock block = (SQLBlock) it.next();
            StaticOperation op = new StaticOperation(block);
            // bind id values from obj
            m_engine.addOperation(obj, op);
        }
    }

    public void onCreate(CreateEvent e) {
        addOperations(e.getObject(), e.getObjectMap().getInserts());
    }

    public void onDelete(DeleteEvent e) {
        addOperations(e.getObject(), e.getObjectMap().getUpdates());
    }

    public void onSet(SetEvent e) {
        Object obj = e.getObject();
        Collection ops = m_engine.getOperations(obj);
        if (ops == null) {
            addOperations(obj, e.getObjectMap().getUpdates());
            ops = m_engine.getOperations(obj);
        }

        Property prop = e.getProperty();
        Path path = Path.get(prop.getName());

        for (Iterator it = ops.iterator(); it.hasNext(); ) {
            StaticOperation op = (StaticOperation) it.next();
            SQLBlock block = op.getSQLBlock();
            int type = Types.INTEGER;
            if (block.hasBinding(path)) {
                type = block.getBinding(path);
            }
            op.set(path, e.getArgument(), type);
        }
    }

    private void addOperations(Object from, Property prop, Object to,
                               Collection blocks) {
        for (Iterator it = blocks.iterator(); it.hasNext(); ) {
            SQLBlock block = (SQLBlock) it.next();
            StaticOperation op = new StaticOperation(block);
            // bind id values from from and to
            m_engine.addOperation(op);
        }
    }

    public void onAdd(AddEvent e) {
        Property prop = e.getProperty();
        Mapping m = e.getObjectMap().getMapping(Path.get(prop.getName()));
        addOperations(e.getObject(), prop, e.getArgument(), m.getAdds());
    }

    public void onRemove(RemoveEvent e) {
        Property prop = e.getProperty();
        Mapping m = e.getObjectMap().getMapping(Path.get(prop.getName()));
        addOperations(e.getObject(), prop, e.getArgument(), m.getRemoves());
    }

}
