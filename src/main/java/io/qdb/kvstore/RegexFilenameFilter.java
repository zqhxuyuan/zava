package io.qdb.kvstore;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * Matches files using a regex.
 */
class RegexFilenameFilter implements FilenameFilter {

    private final Pattern pattern;

    public RegexFilenameFilter(String patternStr) {
        this(Pattern.compile(patternStr));
    }

    public RegexFilenameFilter(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean accept(File dir, String fileName) {
        return pattern.matcher(fileName).matches();
    }
}
