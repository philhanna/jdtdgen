package com.philhanna.dtdgen.dtd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.philhanna.dtdgen.BaseTest;
import com.philhanna.dtdgen.DocumentModel;
import com.philhanna.dtdgen.ElementModel;
import com.philhanna.dtdgen.modelbuilder.DocumentModelBuilder;

/**
 * Unit tests for DTDGenerator
 */
public class TestDTDGenerator extends BaseTest {

   private static final Logger log = Logger.getLogger(TestDTDGenerator.class);

   @Test
   public void runDTDGenerator() throws IOException, SAXException {

      // Get the known good file

      final File benchedFile = new File(testDir, "allgraphs.dtd");

      // Analyze the input XML file

      final DocumentModelBuilder modelBuilder = new DocumentModelBuilder();
      final File inputFile = new File(testDir, "allgraphs.xml");
      final InputStream in = new FileInputStream(inputFile);
      modelBuilder.run(in);
      in.close();

      // Print a DTD from the document model
      
      final DocumentModel model = modelBuilder.getDocumentModel();
      final File outputFile = File.createTempFile("allgraphs", ".dtd");
     // outputFile.deleteOnExit();
      log.info(String.format(
            "Output DTD file is %s",
            outputFile.getCanonicalPath()));
      final PrintWriter out = new PrintWriter(new FileWriter(outputFile));
      final DTDGenerator dtdgen = new DTDGenerator(model);
      dtdgen.printDTD(out);
      out.flush();
      out.close();

      // Compare the files to see if any change has happened

      assertFilesEquals(benchedFile, outputFile);
   }

   @Test
   public void getsCorrectRootElement() throws IOException, SAXException {

      final DocumentModelBuilder modelBuilder = new DocumentModelBuilder();
      final File inputFile = new File(testDir, "allgraphs.xml");
      final InputStream in = new FileInputStream(inputFile);
      modelBuilder.run(in);
      in.close();

      final DocumentModel model = modelBuilder.getDocumentModel();
      final String expected = "Analysis";
      final String actual = model.getRootElementName();
      assertEquals(expected, actual);
   }

   @Test
   public void formatsAttlistsCorrectly() throws IOException, SAXException {

      // Analyze an .xml file to build a document model

      final DocumentModelBuilder modelBuilder = new DocumentModelBuilder();
      final File inputFile = new File("./Testware/Data/bench1.xml")
            .getCanonicalFile();
      final InputStream in = new FileInputStream(inputFile);
      modelBuilder.run(in);
      in.close();
      
      // Check the details of the ATTLIST

      final DocumentModel model = modelBuilder.getDocumentModel();

      // <!ELEMENT Member EMPTY >

      final ElementModel elementModel = model.getElementModel("Member");
      assertNotNull(elementModel);

      // Check for expected formatted values

      final String[] expectedAttlists = {
            "<!ATTLIST Member name NMTOKEN #IMPLIED >",
            "<!ATTLIST Member variable NMTOKEN #IMPLIED >",
            "<!ATTLIST Member value CDATA #IMPLIED >",
            };
      final String[] actualAttlists = DTDGenerator.getATTLISTs(elementModel);
      assertNotNull(actualAttlists);
      assertEquals(expectedAttlists.length, actualAttlists.length);
      for (int i = 0, n = expectedAttlists.length; i < n; i++) {
         final String expected = expectedAttlists[i];
         final String actual = actualAttlists[i];
         assertEquals("Mismatch in attlist " + i, expected, actual);
      }

   }

   @Test
   public void getsAttributeNames() throws IOException, SAXException {

      // Analyze an .xml file to build a document model

      final DocumentModelBuilder modelBuilder = new DocumentModelBuilder();
      final File inputFile = new File("./Testware/Data/bench1.xml")
            .getCanonicalFile();
      final InputStream in = new FileInputStream(inputFile);
      modelBuilder.run(in);
      in.close();

      // Check the details of the ATTLIST
      
      final DocumentModel model = modelBuilder.getDocumentModel();
      
      // <!ELEMENT Member EMPTY >

      final ElementModel elementModel = model.getElementModel("Member");
      assertNotNull(elementModel);

      // Check for expected names

      final String[] expectedNames = { "name", "variable", "value", };

      int i = 0;
      final Iterator<String> it = elementModel.attributeNameIterator();
      while (it.hasNext()) {
         final String actual = it.next();
         if (i >= expectedNames.length) {
            final String errmsg = String
                  .format(
                        "More actual attributes than expected (%d). Actual attribute name was %s",
                        expectedNames.length,
                        actual);
            fail(errmsg);
         }
         final String expected = expectedNames[i];
         assertEquals("Mismatch in attribute name " + i, expected, actual);
         i++;
      }
      assertEquals(
            "Fewer attribute names found than expected",
            expectedNames.length,
            i);
   }

}