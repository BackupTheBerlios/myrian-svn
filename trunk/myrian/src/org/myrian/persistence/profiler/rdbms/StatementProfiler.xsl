<?xml version="1.0" encoding="UTF-8"?>
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:template match="/profile">
    <html>
      <head>
        <title>Profile</title></head>
        <style>
          page {
            font-size: smaller;
          }

          table {
            border-spacing: 0;
            border-collapse: collapse;
          }

          th, td {
            border: 1px solid gray;
            padding: 3px;
            vertical-align: top;
          }
        </style>
      <body>
        <h1>Publishing profile</h1>

        <h2>Elapsed time by JDBC statement lifecycle phase</h2>

        <table>
          <tr>
            <th>Prepare</th>
            <th>Set</th>
            <th>Execute</th>
            <th>Next</th>
            <th>Get</th>
            <th>Close</th>
            <th>Total</th>
          </tr>

          <tr>
            <td><xsl:value-of select="sum(//prepare/millis)"/></td>
            <td><xsl:value-of select="sum(//set/millis)"/></td>
            <td><xsl:value-of select="sum(//execute/millis)"/></td>
            <td><xsl:value-of select="sum(//next/millis)"/></td>
            <td><xsl:value-of select="sum(//get/millis)"/></td>
            <td><xsl:value-of select="sum(//close/millis)"/></td>
            <td><xsl:value-of select="sum(//*/millis)"/></td>
          </tr>
        </table>

        <h2>Elapsed time by SQL text</h2>

        <p>The largest consumer of time is listed first.</p>

        <table>
          <tr>
            <th>ID</th>
            <th>Prepare</th>
            <th>Set</th>
            <th>Execute</th>
            <th>Next</th>
            <th>Get</th>
            <th>Close</th>
            <th>Total</th>
          </tr>

          <xsl:for-each select="text">
            <xsl:sort select="sum(//statement[@text = current()/@id]/lifecycle/*/millis)" data-type="number" order="descending"/>

            <tr>
              <th><a href="#text{@id}"><xsl:value-of select="@id"/></a></th>
              <td><xsl:value-of select="sum(//statement[@text = current()/@id]/lifecycle/prepare/millis)"/></td>
              <td><xsl:value-of select="sum(//statement[@text = current()/@id]/lifecycle/set/millis)"/></td>
              <td><xsl:value-of select="sum(//statement[@text = current()/@id]/lifecycle/execute/millis)"/></td>
              <td><xsl:value-of select="sum(//statement[@text = current()/@id]/lifecycle/next/millis)"/></td>
              <td><xsl:value-of select="sum(//statement[@text = current()/@id]/lifecycle/get/millis)"/></td>
              <td><xsl:value-of select="sum(//statement[@text = current()/@id]/lifecycle/close/millis)"/></td>
              <td><xsl:value-of select="sum(//statement[@text = current()/@id]/lifecycle/*/millis)"/></td>
            </tr>
          </xsl:for-each>
        </table>

        <h2>SQL catalog</h2>

        <table>
          <tr>
            <th>ID</th>
            <th>SQL</th>
          </tr>

          <xsl:for-each select="text">
            <tr>
              <th><a id="text{@id}"><xsl:value-of select="@id"/></a></th>
              <td><pre><xsl:value-of select="."/></pre></td>
            </tr>
          </xsl:for-each>
        </table>


        <h2>Elapsed time by object type</h2>

        <p>The largest consumer of time is listed first.</p>

        <table>
          <tr>
            <th>Object Type</th>
            <th>Prepare</th>
            <th>Set</th>
            <th>Execute</th>
            <th>Next</th>
            <th>Get</th>
            <th>Close</th>
            <th>Total</th>
          </tr>

          <xsl:for-each select="type">
            <xsl:sort select="sum(//statement[objectType = current()]/lifecycle/*/millis)" data-type="number" order="descending"/>

            <tr>
              <th><xsl:value-of select="."/></th>
              <td><xsl:value-of select="sum(//statement[objectType = current()]/lifecycle/prepare/millis)"/></td>
              <td><xsl:value-of select="sum(//statement[objectType = current()]/lifecycle/set/millis)"/></td>
              <td><xsl:value-of select="sum(//statement[objectType = current()]/lifecycle/execute/millis)"/></td>
              <td><xsl:value-of select="sum(//statement[objectType = current()]/lifecycle/next/millis)"/></td>
              <td><xsl:value-of select="sum(//statement[objectType = current()]/lifecycle/get/millis)"/></td>
              <td><xsl:value-of select="sum(//statement[objectType = current()]/lifecycle/close/millis)"/></td>
              <td><xsl:value-of select="sum(//statement[objectType = current()]/lifecycle/*/millis)"/></td>
            </tr>
          </xsl:for-each>
        </table>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
