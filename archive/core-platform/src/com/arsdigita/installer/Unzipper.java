/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

/**
 * $Id: //core-platform/dev/src/com/arsdigita/installer/Unzipper.java#2 $
 *
 *  Extract the .zip archive in specified directory.
 *
 */

package com.arsdigita.installer;

import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;


class Unzipper {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/installer/Unzipper.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    public static void main (String args[]) 
            throws IOException, FileNotFoundException
    {

        if (args.length < 2) {
            System.err.println(
              "Usage: Unzipper <archivefile> <extractdir> [file1 [file2] ...]]");
            System.exit(1);
        }

        ZipFile zipArchive = new ZipFile(args[0]);

        File destDir = new File(args[1]);

        boolean debug = false;

        String zipDebugPty = System.getProperty("zip.debug");

        if (zipDebugPty != null) {
            debug = zipDebugPty.equals("true");
        }

        if (args.length == 2) {

            Enumeration entries = zipArchive.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                extractZipEntry(destDir, zipArchive, zipEntry, debug);
            }

        }  else {

            for (int i = 2; i < args.length; i++) {
                extractZipEntry(destDir, zipArchive, args[i], debug);
            }
        
        }

    }


    static void extractZipEntry (File destDir, ZipFile zipArchive, String entryName,
                boolean debug) 
            throws IOException, FileNotFoundException
    {
        ZipEntry zipEntry = zipArchive.getEntry(entryName);
        if (zipEntry == null) {
            throw new FileNotFoundException("Entry: '" +
                entryName + "' could not be found in ZIP archive");
        }
        extractZipEntry(destDir, zipArchive, zipEntry, debug);
    }


    static void extractZipEntry (File destDir, ZipFile zipArchive, ZipEntry zipEntry,
              boolean debug) 
            throws IOException, FileNotFoundException
    {

        File entryFile = new File(destDir, zipEntry.getName());

        if (debug) {
            System.err.print("Creating: " + zipEntry.getName() + " ... ");
        }

        if (entryFile.exists()) {
           System.err.println("File exists!");
           return;
        }

        if (zipEntry.isDirectory()) {
            //  Create directory
            entryFile.mkdirs();
        } else {
            //  However, we have a problem here if we only want to
            //  extract say, file/in/some/subdirectory.  We need to
            //  make sure that all parent directories exist.
            File parentDir = entryFile.getParentFile();
            if (!parentDir.isDirectory()) {
                if (debug) {
                    System.err.print("(creating " + parentDir.toString() +
                        ") ");
                }
                parentDir.mkdirs();
            }
            //  This is a caveman approach.  Depressingly enough, it
            //  works.
            int bytesRead;
            byte[] buffer = new byte[1024];
            InputStream input = zipArchive.getInputStream(zipEntry);
            BufferedOutputStream output = new BufferedOutputStream(
                    new FileOutputStream(entryFile.toString(), true));
            try {
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }

            } finally {
                try {
                    output.close();
                } catch(IOException e) {
                    System.err.println("Error closing output stream!" + e.getMessage());
                    e.printStackTrace();
                }

            }
        }
        if (debug) {
            System.err.println("OK");
        }

    }

}
            
