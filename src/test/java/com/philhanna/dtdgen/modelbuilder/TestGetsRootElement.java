package com.philhanna.dtdgen.modelbuilder;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.philhanna.dtdgen.BaseTest;
import com.philhanna.dtdgen.DocumentModel;
import com.philhanna.dtdgen.modelbuilder.DocumentModelBuilder;

/**
 * Unit tests for getting the root element of a bodu
 */
public class TestGetsRootElement extends BaseTest {

   @Before
   public void setUp() throws Exception {
      super.setUp();
   }

   @After
   public void tearDown() throws Exception {
      super.tearDown();
   }

   @Test
   public void handlesTypicalExploration() throws Exception {
      runTest("ksink1.xml", "Analysis");
   }

   private void runTest(final String fileName, final String expected)
         throws IOException, SAXException {
      
      // Analyze the xml file to get a document model
      
      final File inputFile = new File(testDir, fileName);
      final InputStream in = new FileInputStream(inputFile);
      final DocumentModelBuilder modelBuilder = new DocumentModelBuilder();
      modelBuilder.run(in);
      in.close();
      
      final DocumentModel model = modelBuilder.getDocumentModel();
      final String actual = model.getRootElementName();
      assertEquals(expected, actual);
   }

}