package org.myrian.persistence.jdo;

import org.myrian.persistence.metadata.*;
import org.myrian.persistence.pdl.*;

import java.io.*;
import java.util.*;
import javax.jdo.*;
import javax.jdo.spi.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import org.apache.log4j.Logger;


/**
 * PDLGenerator
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/10/04 $
 **/

class PDLGenerator implements RegisterClassListener {


    private static final Logger s_log = Logger.getLogger(PDLGenerator.class);

    private Root m_root;
    private Set m_loaded = new HashSet();

    PDLGenerator(Root root) {
        m_root = root;
    }

    PDLGenerator() {
        this(new Root());
    }

    Root getRoot() {
        return m_root;
    }

    public void registerClass(RegisterClassEvent ev) {
        Class klass = ev.getRegisteredClass();
        if (s_log.isInfoEnabled()) {
            s_log.info("Loading metadata for " + klass);
        }
        ClassLoader loader = klass.getClassLoader();
        PDL pdl = new PDL();
        List resources = getResources(klass);
        for (int i = 0; i < resources.size(); i++) {
            String resource = (String) resources.get(i);
            if (m_loaded.contains(resource)) {
                continue;
            }
            m_loaded.add(resource);

            String jdoResource = resource + ".jdo";
            InputStream is = loader.getResourceAsStream(jdoResource);
            if (is != null) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Found " + jdoResource);
                }
                JDOHandler handler = new JDOHandler(loader, jdoResource);
                parse(is, handler);
                String str = handler.getPDL();
                if (s_log.isInfoEnabled()) {
                    s_log.info("PDL for " + jdoResource);
                    s_log.info(str);
                }
                pdl.load(new StringReader(str), jdoResource);
                try { is.close(); } catch (IOException e) {
                    throw new Error(e);
                }
            }

            String pdlResource = resource + ".pdl";
            is = loader.getResourceAsStream(pdlResource);
            if (is != null) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Found " + pdlResource);
                }
                Reader isr = new InputStreamReader(is);
                pdl.load(isr, pdlResource);
                try { isr.close(); } catch(IOException e) {
                    throw new Error(e);
                }
            }
        }
        pdl.emit(m_root);
    }

    // JDO Metadata search order (as defined in chapter 18, page 128
    // of the 1.0.1 spec), metadata defined earlier in the search
    // order takes precedence, so if there is a class definition in
    // any of these package.jdo files for "Class", then Class.jdo gets
    // ignored.
    //
    //   META-INF/package.jdo, WEB-INF/package.jdo, package.jdo,
    //   com/package.jdo, com/.../package.jdo, com/foo/bar/package.jdo,
    //   com/foo/bar/Class.jdo

    private List getResources(Class klass) {
        List result = new ArrayList();

        result.add("META-INF/package");
        result.add("WEB-INF/package");
        result.add("package");

        String name = klass.getName();
        int idx = -1;
        while (true) {
            idx = name.indexOf('.', idx + 1);
            if (idx >= 0) {
                String pkg = name.substring(0, idx).replace('.', '/');
                result.add(pkg + "/package");
            } else {
                break;
            }
        }

        result.add(name.replace('.', '/'));

        return result;
    }

    private void parse(InputStream is, DefaultHandler dh) {
        SAXParserFactory fact = SAXParserFactory.newInstance();
        try {
            SAXParser p = fact.newSAXParser();
            p.parse(is, dh);
        } catch (ParserConfigurationException e) {
            throw new Error(e);
        } catch (SAXException e) {
            throw new Error(e);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

}
