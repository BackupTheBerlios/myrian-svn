/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.db;

import java.sql.SQLException;


/**
 * A simple implementation of the java.sql.DatabaseMetaData interface
 * that wraps a "real" implementation of java.sql.DatabaseMetaData
 *
 * @author Mark Thomas
 * @version $Revision: #6 $ $Date: 2003/08/15 $
 * @since 4.5
 */
public class DatabaseMetaData implements java.sql.DatabaseMetaData {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/DatabaseMetaData.java#6 $ $Author: dennis $ $Date: 2003/08/15 $";

    // the object we wrap
    private java.sql.DatabaseMetaData m_metaData;

    // the connection our constructor was passed
    private Connection m_conn;

    // Constructor: use the "wrap" class method to create instances
    private DatabaseMetaData(Connection conn, java.sql.DatabaseMetaData meta) {
        m_metaData = meta;
        m_conn = conn;
    }


    // Methods

    /**
     * Can all the procedures returned by getProcedures be called by
     * the current user?
     */
    public boolean allProceduresAreCallable() throws SQLException {
        try {
            return m_metaData.allProceduresAreCallable();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can all the tables returned by getTable be SELECTed by the
     * current user?
     */
    public boolean allTablesAreSelectable() throws SQLException {
        try {
            return m_metaData.allTablesAreSelectable();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does a data definition statement within a transaction force
     * the transaction to commit?
     */
    public boolean dataDefinitionCausesTransactionCommit()
        throws SQLException {
        try {
            return m_metaData.dataDefinitionCausesTransactionCommit();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is a data definition statement within a transaction
     * ignored?
     */
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        try {
            return m_metaData.dataDefinitionIgnoredInTransactions();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Indicates whether or not a visible row delete can be detected
     * by calling ResultSet.rowDeleted().
     */
    public boolean deletesAreDetected(int type) throws SQLException {
        try {
            return m_metaData.deletesAreDetected(type);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Did getMaxRowSize() include LONGVARCHAR and LONGVARBINARY
     * blobs?
     */
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        try {
            return m_metaData.doesMaxRowSizeIncludeBlobs();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a description of a table's optimal set of columns that
     * uniquely identifies a row.
     */
    public java.sql.ResultSet getBestRowIdentifier(String catalog,
                                                   String schema,
                                                   String table,
                                                   int scope,
                                                   boolean nullable)
        throws SQLException {
        try {
            java.sql.ResultSet rs = m_metaData.getBestRowIdentifier(catalog,
                                                                    schema,
                                                                    table,
                                                                    scope,
                                                                    nullable);
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets the catalog names available in this database.
     */
    public java.sql.ResultSet getCatalogs() throws SQLException {
        try {
            return ResultSet.wrap(m_conn, m_metaData.getCatalogs());
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the separator between catalog and table name?
     */
    public String getCatalogSeparator() throws SQLException {
        try {
            return m_metaData.getCatalogSeparator();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the database vendor's preferred term for "catalog"?
     */
    public String getCatalogTerm() throws SQLException {
        try {
            return m_metaData.getCatalogTerm();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a description of the access rights for a table's
     * columns.
     */
    public java.sql.ResultSet getColumnPrivileges(String catalog,
                                                  String schema,
                                                  String table,
                                                  String columnNamePattern)
        throws SQLException {

        try {
            java.sql.ResultSet rs = m_metaData.getColumnPrivileges(catalog,
                                                                   schema,
                                                                   table,
                                                                   columnNamePattern);
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a description of table columns available in the specified
     * catalog.
     */
    public java.sql.ResultSet getColumns(String catalog,
                                         String schemaPattern,
                                         String tableNamePattern,
                                         String columnNamePattern)
        throws SQLException {
        try {
            java.sql.ResultSet rs = m_metaData.getColumns(catalog,
                                                          schemaPattern,
                                                          tableNamePattern,
                                                          columnNamePattern);
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Retrieves the connection that produced this metadata
     * object.
     */
    public java.sql.Connection getConnection() throws SQLException {
        return m_conn;
    }

    /**
     * Gets a description of the foreign key columns in the foreign
     * key table that reference the primary key columns of the primary
     * key table (describe how one table imports another's key.) This
     * should normally return a single foreign key/primary key pair
     * (most tables only import a foreign key from a table once.) They
     * are ordered by FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, and
     * KEY_SEQ.
     */
    public java.sql.ResultSet getCrossReference(String primaryCatalog,
                                                String primarySchema,
                                                String primaryTable,
                                                String foreignCatalog,
                                                String foreignSchema,
                                                String foreignTable)
        throws SQLException {
        try {
            java.sql.ResultSet rs = m_metaData.getCrossReference(primaryCatalog,
                                                                 primarySchema,
                                                                 primaryTable,
                                                                 foreignCatalog,
                                                                 foreignSchema,
                                                                 foreignTable);
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the name of this database product?
     */
    public String getDatabaseProductName() throws SQLException {
        try {
            return m_metaData.getDatabaseProductName();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the version of this database product?
     */
    public String getDatabaseProductVersion() throws SQLException {
        try {
            return m_metaData.getDatabaseProductVersion();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the database's default transaction isolation level? The
     * values are defined in java.sql.Connection.
     */
    public int getDefaultTransactionIsolation() throws SQLException {
        try {
            return m_metaData.getDefaultTransactionIsolation();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's this JDBC driver's major version number?
     */
    public int getDriverMajorVersion() {
        return m_metaData.getDriverMajorVersion();
    }

    /**
     * What's this JDBC driver's minor version number?
     */
    public int getDriverMinorVersion() {
        return m_metaData.getDriverMinorVersion();
    }

    /**
     * What's the name of this JDBC driver?
     */
    public String getDriverName() throws SQLException {
        try {
            return m_metaData.getDriverName();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the version of this JDBC driver?
     */
    public String getDriverVersion() throws SQLException {
        try {
            return m_metaData.getDriverVersion();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a description of the foreign key columns that reference a
     * table's primary key columns (the foreign keys exported by a
     * table).
     */
    public java.sql.ResultSet getExportedKeys(String catalog, String schema,
                                              String table)
        throws SQLException {
        try {
            java.sql.ResultSet rs = m_metaData.getExportedKeys(catalog, schema,
                                                               table);
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets all the "extra" characters that can be used in unquoted
     * identifier names (those beyond a-z, A-Z, 0-9 and _).
     */
    public String getExtraNameCharacters() throws SQLException {
        try {
            return m_metaData.getExtraNameCharacters();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the string used to quote SQL identifiers? This returns
     * a space " " if identifier quoting isn't supported.
     */
    public String getIdentifierQuoteString() throws SQLException {
        try {
            return m_metaData.getIdentifierQuoteString();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a description of the primary key columns that are
     * referenced by a table's foreign key columns (the primary keys
     * imported by a table).
     */
    public java.sql.ResultSet getImportedKeys(String catalog, String schema,
                                              String table)
        throws SQLException
    {
        try {
            java.sql.ResultSet rs = m_metaData.getImportedKeys(catalog, schema, table);
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a description of a table's indices and statistics.
     */
    public java.sql.ResultSet getIndexInfo(String catalog, String schema,
                                           String table, boolean unique,
                                           boolean approximate)
        throws SQLException {
        try {
            java.sql.ResultSet rs = m_metaData.getIndexInfo(catalog, schema,
                                                            table, unique,
                                                            approximate);
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * How many hex characters can you have in an inline binary
     * literal?
     */
    public int getMaxBinaryLiteralLength() throws SQLException {
        try {
            return m_metaData.getMaxBinaryLiteralLength();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the maximum length of a catalog name?
     */
    public int getMaxCatalogNameLength() throws SQLException {
        try {
            return m_metaData.getMaxCatalogNameLength();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the max length for a character literal?
     */
    public int getMaxCharLiteralLength() throws SQLException {
        try {
            return m_metaData.getMaxCharLiteralLength();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the limit on column name length?
     */
    public int getMaxColumnNameLength() throws SQLException {
        try {
            return m_metaData.getMaxColumnNameLength();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the maximum number of columns in a "GROUP BY"
     * clause?
     */
    public int getMaxColumnsInGroupBy() throws SQLException {
        try {
            return m_metaData.getMaxColumnsInGroupBy();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the maximum number of columns allowed in an index?
     */
    public int getMaxColumnsInIndex() throws SQLException {
        try {
            return m_metaData.getMaxColumnsInIndex();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the maximum number of columns in an "ORDER BY"
     * clause?
     */
    public int getMaxColumnsInOrderBy() throws SQLException {
        try {
            return m_metaData.getMaxColumnsInOrderBy();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the maximum number of columns in a "SELECT" list?
     */
    public int getMaxColumnsInSelect() throws SQLException {
        try {
            return m_metaData.getMaxColumnsInSelect();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the maximum number of columns in a table?
     */
    public int getMaxColumnsInTable() throws SQLException {
        try {
            return m_metaData.getMaxColumnsInTable();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * How many active connections can we have at a time to this
     * database?
     */
    public int getMaxConnections() throws SQLException {
        try {
            return m_metaData.getMaxConnections();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the maximum cursor name length?
     */
    public int getMaxCursorNameLength() throws SQLException {
        try {
            return m_metaData.getMaxCursorNameLength();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Retrieves the maximum number of bytes for an index, including
     * all of the parts of the index.
     */
    public int getMaxIndexLength() throws SQLException {
        try {
            return m_metaData.getMaxIndexLength();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the maximum length of a procedure name?
     */
    public int getMaxProcedureNameLength() throws SQLException {
        try {
            return m_metaData.getMaxProcedureNameLength();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the maximum length of a single row?
     */
    public int getMaxRowSize() throws SQLException {
        try {
            return m_metaData.getMaxRowSize();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the maximum length allowed for a schema name?
     */
    public int getMaxSchemaNameLength() throws SQLException {
        try {
            return m_metaData.getMaxSchemaNameLength();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the maximum length of an SQL statement?
     */
    public int getMaxStatementLength() throws SQLException {
        try {
            return m_metaData.getMaxStatementLength();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * How many active statements can we have open at one time to
     * this database?
     */
    public int getMaxStatements() throws SQLException {
        try {
            return m_metaData.getMaxStatements();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the maximum length of a table name?
     */
    public int getMaxTableNameLength() throws SQLException {
        try {
            return m_metaData.getMaxTableNameLength();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the maximum number of tables in a SELECT statement?
     */
    public int getMaxTablesInSelect() throws SQLException {
        try {
            return m_metaData.getMaxTablesInSelect();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the maximum length of a user name?
     */
    public int getMaxUserNameLength() throws SQLException {
        try {
            return m_metaData.getMaxUserNameLength();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a comma-separated list of math functions.
     */
    public String getNumericFunctions() throws SQLException {
        try {
            return m_metaData.getNumericFunctions();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a description of a table's primary key columns.
     */
    public java.sql.ResultSet getPrimaryKeys(String catalog, String schema,
                                             String table)
        throws SQLException {
        try {
            java.sql.ResultSet rs = m_metaData.getPrimaryKeys(catalog, schema,
                                                              table);
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a description of a catalog's stored procedure parameters
     * and result columns.
     */
    public java.sql.ResultSet getProcedureColumns(String catalog,
                                                  String schemaPattern,
                                                  String procedureNamePattern,
                                                  String columnNamePattern)
        throws SQLException {
        try {
            java.sql.ResultSet rs =
                m_metaData.getProcedureColumns(catalog,
                                               schemaPattern,
                                               procedureNamePattern,
                                               columnNamePattern);
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a description of the stored procedures available in a
     * catalog.
     */
    public java.sql.ResultSet getProcedures(String catalog,
                                            String schemaPattern,
                                            String procedureNamePattern)
        throws SQLException {
        try {
            java.sql.ResultSet rs = m_metaData.getProcedures(catalog, schemaPattern,
                                                             procedureNamePattern);
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the database vendor's preferred term for
     * "procedure"?
     */
    public String getProcedureTerm() throws SQLException {
        try {
            return m_metaData.getProcedureTerm();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets the schema names available in this database.
     */
    public java.sql.ResultSet getSchemas() throws SQLException {
        try {
            java.sql.ResultSet rs = m_metaData.getSchemas();
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the database vendor's preferred term for "schema"?
     */
    public String getSchemaTerm() throws SQLException {
        try {
            return m_metaData.getSchemaTerm();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets the string that can be used to escape wildcard
     * characters.
     */
    public String getSearchStringEscape() throws SQLException {
        try {
            return m_metaData.getSearchStringEscape();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a comma-separated list of all a database's SQL keywords
     * that are NOT also SQL92 keywords.
     */
    public String getSQLKeywords() throws SQLException {
        try {
            return m_metaData.getSQLKeywords();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a comma-separated list of string functions.
     */
    public String getStringFunctions() throws SQLException {
        try {
            return m_metaData.getStringFunctions();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a comma-separated list of system functions.
     */
    public String getSystemFunctions() throws SQLException {
        try {
            return m_metaData.getSystemFunctions();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a description of the access rights for each table
     * available in a catalog.
     */
    public java.sql.ResultSet getTablePrivileges(String catalog,
                                                 String schemaPattern,
                                                 String tableNamePattern)
        throws SQLException {
        try {
            java.sql.ResultSet rs = m_metaData.getTablePrivileges(catalog,
                                                                  schemaPattern,
                                                                  tableNamePattern);
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a description of tables available in a catalog.
     */
    public java.sql.ResultSet getTables(String catalog,
                                        String schemaPattern,
                                        String tableNamePattern,
                                        String[] types)
        throws SQLException {
        try {
            java.sql.ResultSet rs = m_metaData.getTables(catalog, schemaPattern,
                                                         tableNamePattern, types);
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets the table types available in this database.
     */
    public java.sql.ResultSet getTableTypes() throws SQLException {
        try {
            java.sql.ResultSet rs = m_metaData.getTableTypes();
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a comma-separated list of time and date functions.
     */
    public String getTimeDateFunctions() throws SQLException {
        try {
            return m_metaData.getTimeDateFunctions();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a description of all the standard SQL types supported by
     * this database.
     */
    public java.sql.ResultSet getTypeInfo() throws SQLException {
        try {
            return ResultSet.wrap(m_conn, m_metaData.getTypeInfo());
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a description of the user-defined types defined in a
     * particular schema.
     */
    public java.sql.ResultSet getUDTs(String catalog, String schemaPattern,
                                      String typeNamePattern, int[] types)
        throws SQLException {
        try {
            java.sql.ResultSet rs = m_metaData.getUDTs(catalog, schemaPattern,
                                                       typeNamePattern, types);
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's the url for this database?
     */
    public String getURL() throws SQLException {
        try {
            return m_metaData.getURL();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * What's our user name as known to the database?
     */
    public String getUserName() throws SQLException {
        try {
            return m_metaData.getUserName();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets a description of a table's columns that are automatically
     * updated when any value in a row is updated.
     */
    public java.sql.ResultSet getVersionColumns(String catalog, String schema,
                                                String table)
        throws SQLException {
        try {
            java.sql.ResultSet rs = m_metaData.getVersionColumns(catalog,
                                                                 schema,
                                                                 table);
            return ResultSet.wrap(m_conn, rs);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Indicates whether or not a visible row insert can be detected
     * by calling ResultSet.rowInserted().
     */
    public boolean insertsAreDetected(int type) throws SQLException {
        try {
            return m_metaData.insertsAreDetected(type);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does a catalog appear at the start of a qualified table name?
     * (Otherwise it appears at the end)
     */
    public boolean isCatalogAtStart() throws SQLException {
        try {
            return m_metaData.isCatalogAtStart();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is the database in read-only mode?
     */
    public boolean isReadOnly() throws SQLException {
        try {
            return m_metaData.isReadOnly();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are concatenations between NULL and non-NULL values NULL? For
     * SQL-92 compliance, a JDBC technology-enabled driver will return
     * true.
     */
    public boolean nullPlusNonNullIsNull() throws SQLException {
        try {
            return m_metaData.nullPlusNonNullIsNull();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are NULL values sorted at the end regardless of sort order?
     */
    public boolean nullsAreSortedAtEnd() throws SQLException {
        try {
            return m_metaData.nullsAreSortedAtEnd();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are NULL values sorted at the start regardless of sort
     * order?
     */
    public boolean nullsAreSortedAtStart() throws SQLException {
        try {
            return m_metaData.nullsAreSortedAtStart();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are NULL values sorted high?
     */
    public boolean nullsAreSortedHigh() throws SQLException {
        try {
            return m_metaData.nullsAreSortedHigh();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are NULL values sorted low?
     */
    public boolean nullsAreSortedLow() throws SQLException {
        try {
            return m_metaData.nullsAreSortedLow();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Indicates whether deletes made by others are visible.
     */
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        try {
            return m_metaData.othersDeletesAreVisible(type);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Indicates whether inserts made by others are visible.
     */
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        try {
            return m_metaData.othersInsertsAreVisible(type);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Indicates whether updates made by others are visible.
     */
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        try {
            return m_metaData.othersUpdatesAreVisible(type);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Indicates whether a result set's own deletes are visible.
     */
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        try {
            return m_metaData.ownDeletesAreVisible(type);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Indicates whether a result set's own inserts are visible.
     */
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        try {
            return m_metaData.ownInsertsAreVisible(type);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Indicates whether a result set's own updates are visible.
     */
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        try {
            return m_metaData.ownUpdatesAreVisible(type);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does the database treat mixed case unquoted SQL identifiers as
     * case insensitive and store them in lower case?
     */
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        try {
            return m_metaData.storesLowerCaseIdentifiers();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does the database treat mixed case quoted SQL identifiers as
     * case insensitive and store them in lower case?
     */
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        try {
            return m_metaData.storesLowerCaseQuotedIdentifiers();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does the database treat mixed case unquoted SQL identifiers as
     * case insensitive and store them in mixed case?
     */
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        try {
            return m_metaData.storesMixedCaseIdentifiers();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does the database treat mixed case quoted SQL identifiers as
     * case insensitive and store them in mixed case?
     */
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        try {
            return m_metaData.storesMixedCaseQuotedIdentifiers();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does the database treat mixed case unquoted SQL identifiers as
     * case insensitive and store them in upper case?
     */
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        try {
            return m_metaData.storesUpperCaseIdentifiers();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does the database treat mixed case quoted SQL identifiers as
     * case insensitive and store them in upper case?
     */
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        try {
            return m_metaData.storesUpperCaseQuotedIdentifiers();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is "ALTER TABLE" with add column supported?
     */
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        try {
            return m_metaData.supportsAlterTableWithAddColumn();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is "ALTER TABLE" with drop column supported?
     */
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        try {
            return m_metaData.supportsAlterTableWithDropColumn();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is the ANSI92 entry level SQL grammar supported? All JDBC
     * CompliantTM drivers must return true.
     */
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        try {
            return m_metaData.supportsANSI92EntryLevelSQL();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is the ANSI92 full SQL grammar supported?
     */
    public boolean supportsANSI92FullSQL() throws SQLException {
        try {
            return m_metaData.supportsANSI92FullSQL();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is the ANSI92 intermediate SQL grammar supported?
     */
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        try {
            return m_metaData.supportsANSI92IntermediateSQL();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Indicates whether the driver supports batch updates.
     */
    public boolean supportsBatchUpdates() throws SQLException {
        try {
            return m_metaData.supportsBatchUpdates();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can a catalog name be used in a data manipulation
     * statement?
     */
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        try {
            return m_metaData.supportsCatalogsInDataManipulation();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can a catalog name be used in an index definition
     * statement?
     */
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        try {
            return m_metaData.supportsCatalogsInIndexDefinitions();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can a catalog name be used in a privilege definition
     * statement?
     */
    public boolean supportsCatalogsInPrivilegeDefinitions()
        throws SQLException {
        try {
            return m_metaData.supportsCatalogsInPrivilegeDefinitions();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can a catalog name be used in a procedure call statement?
     */
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        try {
            return m_metaData.supportsCatalogsInProcedureCalls();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can a catalog name be used in a table definition statement?
     */
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        try {
            return m_metaData.supportsCatalogsInTableDefinitions();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is column aliasing supported?
     */
    public boolean supportsColumnAliasing() throws SQLException {
        try {
            return m_metaData.supportsColumnAliasing();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is the CONVERT function between SQL types supported?
     */
    public boolean supportsConvert() throws SQLException {
        try {
            return m_metaData.supportsConvert();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is CONVERT between the given SQL types supported?
     */
    public boolean supportsConvert(int fromType, int toType)
        throws SQLException {
        try {
            return m_metaData.supportsConvert(fromType, toType);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is the ODBC Core SQL grammar supported?
     */
    public boolean supportsCoreSQLGrammar() throws SQLException {
        try {
            return m_metaData.supportsCoreSQLGrammar();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are correlated subqueries supported? A JDBC CompliantTM driver
     * always returns true.
     */
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        try {
            return m_metaData.supportsCorrelatedSubqueries();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are both data definition and data manipulation statements
     * within a transaction supported?
     */
    public boolean supportsDataDefinitionAndDataManipulationTransactions()
        throws SQLException {
        try {
            return
                m_metaData.supportsDataDefinitionAndDataManipulationTransactions();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are only data manipulation statements within a transaction
     * supported?
     */
    public boolean supportsDataManipulationTransactionsOnly()
        throws SQLException {
        try {
            return m_metaData.supportsDataManipulationTransactionsOnly();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * If table correlation names are supported, are they restricted
     * to be different from the names of the tables?
     */
    public boolean supportsDifferentTableCorrelationNames()
        throws SQLException {
        try {
            return m_metaData.supportsDifferentTableCorrelationNames();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are expressions in "ORDER BY" lists supported?
     */
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        try {
            return m_metaData.supportsExpressionsInOrderBy();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is the ODBC Extended SQL grammar supported?
     */
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        try {
            return m_metaData.supportsExtendedSQLGrammar();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are full nested outer joins supported?
     */
    public boolean supportsFullOuterJoins() throws SQLException {
        try {
            return m_metaData.supportsFullOuterJoins();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is some form of "GROUP BY" clause supported?
     */
    public boolean supportsGroupBy() throws SQLException {
        try {
            return m_metaData.supportsGroupBy();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can a "GROUP BY" clause add columns not in the SELECT provided
     * it specifies all the columns in the SELECT?
     */
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        try {
            return m_metaData.supportsGroupByBeyondSelect();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can a "GROUP BY" clause use columns not in the SELECT?
     */
    public boolean supportsGroupByUnrelated() throws SQLException {
        try {
            return m_metaData.supportsGroupByUnrelated();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is the SQL Integrity Enhancement Facility supported?
     */
    public boolean supportsIntegrityEnhancementFacility()
        throws SQLException {
        try {
            return m_metaData.supportsIntegrityEnhancementFacility();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is the escape character in "LIKE" clauses supported? A JDBC
     * CompliantTM driver always returns true.
     */
    public boolean supportsLikeEscapeClause() throws SQLException {
        try {
            return m_metaData.supportsLikeEscapeClause();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is there limited support for outer joins? (This will be true
     * if supportFullOuterJoins is true.)
     */
    public boolean supportsLimitedOuterJoins() throws SQLException {
        try {
            return m_metaData.supportsLimitedOuterJoins();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is the ODBC Minimum SQL grammar supported? All JDBC
     * CompliantTM drivers must return true.
     */
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        try {
            return m_metaData.supportsMinimumSQLGrammar();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does the database treat mixed case unquoted SQL identifiers as
     * case sensitive and as a result store them in mixed case? A JDBC
     * CompliantTM driver will always return false.
     */
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        try {
            return m_metaData.supportsMixedCaseIdentifiers();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does the database treat mixed case quoted SQL identifiers as
     * case sensitive and as a result store them in mixed case? A JDBC
     * CompliantTM driver will always return true.
     */
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        try {
            return m_metaData.supportsMixedCaseQuotedIdentifiers();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are multiple ResultSet from a single execute supported?
     */
    public boolean supportsMultipleResultSets() throws SQLException {
        try {
            return m_metaData.supportsMultipleResultSets();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can we have multiple transactions open at once (on different
     * connections)?
     */
    public boolean supportsMultipleTransactions() throws SQLException {
        try {
            return m_metaData.supportsMultipleTransactions();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can columns be defined as non-nullable? A JDBC CompliantTM
     * driver always returns true.
     */
    public boolean supportsNonNullableColumns() throws SQLException {
        try {
            return m_metaData.supportsNonNullableColumns();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can cursors remain open across commits?
     */
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        try {
            return m_metaData.supportsOpenCursorsAcrossCommit();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can cursors remain open across rollbacks?
     */
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        try {
            return m_metaData.supportsOpenCursorsAcrossRollback();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can statements remain open across commits?
     */
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        try {
            return m_metaData.supportsOpenStatementsAcrossCommit();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can statements remain open across rollbacks?
     */
    public boolean supportsOpenStatementsAcrossRollback()
        throws SQLException {
        try {
            return m_metaData.supportsOpenStatementsAcrossRollback();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can an "ORDER BY" clause use columns not in the SELECT
     * statement?
     */
    public boolean supportsOrderByUnrelated() throws SQLException {
        try {
            return m_metaData.supportsOrderByUnrelated();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is some form of outer join supported?
     */
    public boolean supportsOuterJoins() throws SQLException {
        try {
            return m_metaData.supportsOuterJoins();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is positioned DELETE supported?
     */
    public boolean supportsPositionedDelete() throws SQLException {
        try {
            return m_metaData.supportsPositionedDelete();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is positioned UPDATE supported?
     */
    public boolean supportsPositionedUpdate() throws SQLException {
        try {
            return m_metaData.supportsPositionedUpdate();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does the database support the concurrency type in combination
     * with the given result set type?
     */
    public boolean supportsResultSetConcurrency(int type, int concurrency)
        throws SQLException {
        try {
            return m_metaData.supportsResultSetConcurrency(type, concurrency);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does the database support the given result set type?
     */
    public boolean supportsResultSetType(int type) throws SQLException {
        try {
            return m_metaData.supportsResultSetType(type);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can a schema name be used in a data manipulation statement?
     */
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        try {
            return m_metaData.supportsSchemasInDataManipulation();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can a schema name be used in an index definition statement?
     */
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        try {
            return m_metaData.supportsSchemasInIndexDefinitions();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can a schema name be used in a privilege definition
     * statement?
     */
    public boolean supportsSchemasInPrivilegeDefinitions()
        throws SQLException {
        try {
            return m_metaData.supportsSchemasInPrivilegeDefinitions();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can a schema name be used in a procedure call statement?
     */
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        try {
            return m_metaData.supportsSchemasInProcedureCalls();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Can a schema name be used in a table definition statement?
     */
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        try {
            return m_metaData.supportsSchemasInTableDefinitions();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is SELECT for UPDATE supported?
     */
    public boolean supportsSelectForUpdate() throws SQLException {
        try {
            return m_metaData.supportsSelectForUpdate();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are stored procedure calls using the stored procedure escape
     * syntax supported?
     */
    public boolean supportsStoredProcedures() throws SQLException {
        try {
            return m_metaData.supportsStoredProcedures();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are subqueries in comparison expressions supported? A JDBC
     * CompliantTM driver always returns true.
     */
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        try {
            return m_metaData.supportsSubqueriesInComparisons();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are subqueries in 'exists' expressions supported? A JDBC
     * CompliantTM driver always returns true.
     */
    public boolean supportsSubqueriesInExists() throws SQLException {
        try {
            return m_metaData.supportsSubqueriesInExists();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are subqueries in 'in' statements supported? A JDBC
     * CompliantTM driver always returns true.
     */
    public boolean supportsSubqueriesInIns() throws SQLException {
        try {
            return m_metaData.supportsSubqueriesInIns();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are subqueries in quantified expressions supported? A JDBC
     * CompliantTM driver always returns true.
     */
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        try {
            return m_metaData.supportsSubqueriesInQuantifieds();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are table correlation names supported? A JDBC CompliantTM
     * driver always returns true.
     */
    public boolean supportsTableCorrelationNames() throws SQLException {
        try {
            return m_metaData.supportsTableCorrelationNames();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does this database support the given transaction isolation
     * level?
     */
    public boolean supportsTransactionIsolationLevel(int level)
        throws SQLException
    {
        try {
            return m_metaData.supportsTransactionIsolationLevel(level);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Are transactions supported? If not, invoking the method commit
     * is a noop and the isolation level is TRANSACTION_NONE.
     */
    public boolean supportsTransactions() throws SQLException {
        try {
            return m_metaData.supportsTransactions();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is SQL UNION supported?
     */
    public boolean supportsUnion() throws SQLException {
        try {
            return m_metaData.supportsUnion();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Is SQL UNION ALL supported?
     */
    public boolean supportsUnionAll() throws SQLException {
        try {
            return m_metaData.supportsUnionAll();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Indicates whether or not a visible row update can be detected
     * by calling the method ResultSet.rowUpdated.
     */
    public boolean updatesAreDetected(int type) throws SQLException {
        try {
            return m_metaData.updatesAreDetected(type);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does the database use a file for each table?
     */
    public boolean usesLocalFilePerTable() throws SQLException {
        try {
            return m_metaData.usesLocalFilePerTable();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Does the database store tables in a local file?
     */
    public boolean usesLocalFiles() throws SQLException {
        try {
            return m_metaData.usesLocalFiles();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Wraps a DatabaseMetaData instance and returns an
     * AdDatabaseMetaData.
     */
    static DatabaseMetaData wrap(Connection conn,
                                 java.sql.DatabaseMetaData metaData) {
        if (null == metaData) {
            return null;
        }
        if (metaData instanceof com.arsdigita.db.DatabaseMetaData) {
            return (com.arsdigita.db.DatabaseMetaData) metaData;
        } else {
            return new DatabaseMetaData(conn, metaData);
        }
    }
}
