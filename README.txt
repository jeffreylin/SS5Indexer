///////////////////////////////////////////////////////////////////////////////
// Epiphany47 - SS5Indexer [Samba Server Indexer Code (Java)]
///////////////////////////////////////////////////////////////////////////////

Some old school code from 2009 that I'm posting to Github for archival purposes

Purpose:
SS5Indexer finds Pomona ResNet hosts with active Samba shares and 
indexes those shares into a MySQL database.
Please note that the Pomona ResNet is firewalled from outside hosts.

Contents:
/SS5Indexer - Eclipse 3.5.2 project.
/SS5Indexer/bin - Compiled Java classes
/SS5Indexer/doc - JavaDoc
/SS5Indexer/lib - Java library dependencies
/SS5Indexer/src - Java source!
/SS5Indexer/epiphany/ss5 - source for the SS5 Indexer
/SS5Indexer/epiphany/ss5/db - MySQL-related classes
/SS5Indexer/epiphany/ss5/main - SS5Indexer.java and other key components
/SS5Indexer/epiphany/ss5/objects - Custom data structures
/SS5Indexer/jcifs - Contains a patched version of SmbFile.java for jcifs
/SS5Indexer/nmap-03-01-run5.xml - Sample Nmap output