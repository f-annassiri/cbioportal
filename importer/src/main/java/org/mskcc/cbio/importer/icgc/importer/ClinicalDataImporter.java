/*
 *  Copyright (c) 2014 Memorial Sloan-Kettering Cancer Center.
 * 
 *  This library is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 *  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 *  documentation provided hereunder is on an "as is" basis, and
 *  Memorial Sloan-Kettering Cancer Center 
 *  has no obligations to provide maintenance, support,
 *  updates, enhancements or modifications.  In no event shall
 *  Memorial Sloan-Kettering Cancer Center
 *  be liable to any party for direct, indirect, special,
 *  incidental or consequential damages, including lost profits, arising
 *  out of the use of this software and its documentation, even if
 *  Memorial Sloan-Kettering Cancer Center 
 *  has been advised of the possibility of such damage.
 */
package org.mskcc.cbio.importer.icgc.importer;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.inject.internal.Lists;
import com.google.inject.internal.Preconditions;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import org.mskcc.cbio.importer.icgc.support.IcgcImportService;

import org.mskcc.cbio.importer.icgc.transformer.ClinicalDataFileTransformer;

/*
 responsible for invoking ETL operations for clinical data file from ICGC studies
 primary input is a list of ICGC studys

 */
public class ClinicalDataImporter implements Callable<List<String>> {

    private static Logger logger = Logger.getLogger(ClinicalDataImporter.class);
    private static final Integer EXTRACTOR_THREADS = 4;
    private List<String> clinicalDataStudyList;
    private Path destPath;    

    public ClinicalDataImporter(final List<String> baseUrlList, final Path destDir) {
        
        Preconditions.checkArgument(null != baseUrlList && baseUrlList.size() > 0,
                "A list of ICGC Study URLs is required");
        Preconditions.checkArgument(null != destDir,
                "A destination directory for ICGC study data is required");
        Preconditions.checkArgument(Files.isDirectory(destDir)
                && Files.isDirectory(destDir),
                "The specified directory " + destDir + " is invalid");
        this.destPath = destDir;
       
        // edit MUTATION_TYPE place holder in URLs to clinical
        
        this.clinicalDataStudyList = FluentIterable.from(baseUrlList)
                .transform(new Function<String, String>() {
                    @Override
                    public String apply(String f) {
                        return f.replaceAll(IcgcImportService.INSTANCE.MUTATION_TYPE,
                                IcgcImportService.INSTANCE.CLINICAL_TYPE);
                    }
                }).toList();
        //instantiate an ETL processor with n threads
        //this.etl = new IcgcStudyFileETL(EXTRACTOR_THREADS);
    }
    
    private void dispose() {

    }
    
    public  List<Path> processClinicalData() {
       // process the clinical data files with the ETL app      
     //  List<Path> txtList = this.etl.processICGCStudies(clinicalDataStudyList, destPath,
               new ClinicalDataFileTransformer();
      // return a list of text files containing the transformed clinical data
      // return txtList;
        return null;
    }

    /*
    main method for testing
    */
    public static void main(String...args){
        // Base URls for ICGC non-US studies - requires editing to specific file type by Importer

        Map<String, String> urlMap = IcgcImportService.INSTANCE.getIcgcMutationUrlMap();
        // test Path
         Path p = Paths.get("/tmp/asynctest");
        ClinicalDataImporter controller = new ClinicalDataImporter(
            Lists.newArrayList(urlMap.values()), p);
      List<Path> txtPathList = controller.processClinicalData();
      for(Path path : txtPathList){
          logger.info("Data_clinical.txt File: " +path.toString());
      }
      logger.info("Finis");
        
    }

    @Override
    public List<String> call() throws Exception {
        return null;
    }
}
