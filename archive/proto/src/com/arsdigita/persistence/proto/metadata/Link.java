package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * Link
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/03/27 $
 **/

public class Link extends Property {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Link.java#5 $ by $Author: rhs $, $DateTime: 2003/03/27 15:13:02 $";

    private Property m_from;
    private Property m_to;
    private boolean m_isCollection;
    private boolean m_isNullable;

    public Link(String name, Property from, Property to,
		boolean isCollection, boolean isNullable) {
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

    public Property getFrom() {
	return m_from;
    }

    public Property getTo() {
	return m_to;
    }

    public ObjectType getLinkType() {
	return m_from.getContainer();
    }

    public void dispatch(Switch sw) {
        sw.onLink(this);
    }

}
