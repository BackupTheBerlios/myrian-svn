package com.redhat.persistence.jdo;

/**
 * Picture
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/29 $
 **/

public class Picture {

    private int id;
    private String caption;
    private byte[] content;

    public Picture() {}

    public Picture(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String toString() {
        return "<picture #" + getId() + ">";
    }
}
