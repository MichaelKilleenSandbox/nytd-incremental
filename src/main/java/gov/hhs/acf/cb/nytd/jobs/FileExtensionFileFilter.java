package gov.hhs.acf.cb.nytd.jobs;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * File filter returns false for received files that have been processed.
 * An instance of this filter is persisted in the job data map of FileScanJob.
 *
 * User: 13873
 * Date: Jul 28, 2010
 */
public class FileExtensionFileFilter implements FileFilter {
    // regular expression pattern for files with xml extension
    private Pattern fileExtensionPattern;

    public FileExtensionFileFilter(String fileExtension) {
        fileExtensionPattern = Pattern.compile("^.+\\" + fileExtension + "$", Pattern.CASE_INSENSITIVE);        
    }

    @Override
    public boolean accept(File pathname) {
        Matcher fileExtensionMatcher =
                fileExtensionPattern.matcher(pathname.getName());
        if (pathname.isDirectory() || pathname.isHidden() || !fileExtensionMatcher.matches()) {
            return false;
        }

        return true;
    }
}
