package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Versioned keyword.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Revision: #1 $ $Date: 2003/02/17 $
 **/
// FIXME: Do I really need this? Maybe I should just add two methods to
// ObjectTypeNd along the lines of
//   public boolean isVersioned();
//   public void setVersioned(boolean versioned);
// -- vadimn@redhat.com, 2003-02-17
public class VersionedNd extends Node {
    public String toString() {
        return "versioned";
    }
}
