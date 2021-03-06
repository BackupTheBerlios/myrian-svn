<?xml version="1.0"?>
<!--
 Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation; either version 2.1 of
 the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
-->
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="cell"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>Debug</title>
        <xsl:call-template name="css"/>
      </head>
      <body>
        <xsl:if test="not($cell='')">
          <p><xsl:value-of select="$cell"/> is highlighted.</p>
        </xsl:if>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>


  <xsl:template match="debug">
    <xsl:for-each select="queryDump">

      <p><xsl:value-of select="timestamp"/></p>
      <p style="font-style: italic"><xsl:value-of select="message"/></p>
      <pre class="programlisting">
        <xsl:value-of select="query"/>
      </pre>
      <table border="1" cellpadding="2" cellspacing="0">
        <tr>
          <xsl:for-each select="header/column">
            <th><xsl:value-of select="."/></th>
          </xsl:for-each>
        </tr>
        <xsl:for-each select="row">
          <xsl:apply-templates select="."/>
        </xsl:for-each>
      </table>
      <hr/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="row">
    <tr>
      <xsl:for-each select="column">
        <xsl:variable name="thisCell" select="."/>
        <td>
          <xsl:choose>
            <xsl:when test="$cell = $thisCell">
              <span class="highlight"><xsl:value-of select="$thisCell"/></span>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$thisCell"/>
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </xsl:for-each>
    </tr>
  </xsl:template>

  <xsl:template name="css">
    <style type="text/css">
      <xsl:comment>
.highlight {
    color: FireBrick;
    font-weight: bold;
}

body {
    background: #white;
    color:      black;
    margin-left: 3%;
    margin-right: 3%;
}

pre {
    display:        block;
    font-family:    monospace;
    white-space:    pre;
    margin:         0%;
    padding-top:    0.5ex;
    padding-bottom: 0.5ex;
    padding-left:   1ex;
    padding-right:  1ex;
    width:          100%;
}

pre.programlisting {
    background: WhiteSmoke;
    color:  black;
    border: 1px solid black;
}

table {
    margin-top: 1em;
}

td {
    font-size: 0.8em;
}

th {
    font-size: 0.8em;
}

p {
    font-size: 0.8em;
}

hr {
    border: 1px solid WhiteSmoke;
    color: white;
    background-color: white;
    height: 2px;
    margin-bottom: 3em;
    margin-right: 20%;
    margin-left: 20%;
}
      </xsl:comment>
    </style>
  </xsl:template>
</xsl:stylesheet>
