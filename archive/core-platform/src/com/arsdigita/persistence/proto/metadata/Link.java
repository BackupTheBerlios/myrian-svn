package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * Link
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/06/11 $
 **/

public class Link extends Property {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/metadata/Link.java#2 $ by $Author: rhs $, $DateTime: 2003/06/11 15:51:24 $";

    private Role m_from;
    private Role m_to;
    private boolean m_isCollection;
    private boolean m_isNullable;

    public Link(String name, Role from, Role to, boolean isCollection,
		boolean isNullable) {
        super(name);
        m_from = from;
	m_to = to;
	m_isCollection = isCollection;
	m_isNullable = isNullable;
    }

    public ObjectType getType() {
        return m_to.getType();
    }

    public boolean isCollection() {
        return m_isCollection;
    }

    public boolean isNullable() {
        return m_isNullable;
    }

    public boolean isComponent() {
        return m_to.isComponent();
    }

    public boolean isComposite() {
        return m_to.isComposite();
    }

    public Role getFrom() {
	return m_from;
    }

    public Role getTo() {
	return m_to;
    }

    public ObjectType getLinkType() {
	return m_from.getContainer();
    }

    public void dispatch(Switch sw) {
        sw.onLink(this);
    }

}
