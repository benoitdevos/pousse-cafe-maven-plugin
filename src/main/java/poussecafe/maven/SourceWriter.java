package poussecafe.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Set;
import org.codehaus.plexus.util.IOUtil;
import org.stringtemplate.v4.ST;
import poussecafe.exception.PousseCafeException;

public class SourceWriter {

    public static class Builder {

        private SourceWriter generator = new SourceWriter();

        public Builder modelPackageName(String modelPackageName) {
            generator.modelPackageName = modelPackageName;
            return this;
        }

        public Builder adaptersPackageName(String adaptersPackageName) {
            generator.adaptersPackageName = adaptersPackageName;
            return this;
        }

        public Builder name(String name) {
            generator.name = name;
            return this;
        }

        public Builder storageAdapters(Set<String> storageAdapters) {
            generator.storageAdapters = storageAdapters;
            return this;
        }

        public Builder demoAttribute(boolean demoAttribute) {
            generator.demoAttribute = demoAttribute;
            return this;
        }

        public SourceWriter build() {
            Objects.requireNonNull(generator.modelPackageName);
            Objects.requireNonNull(generator.name);
            Objects.requireNonNull(generator.storageAdapters);
            return generator;
        }
    }

    private SourceWriter() {

    }

    private String modelPackageName;

    private String adaptersPackageName;

    private String name;

    private Set<String> storageAdapters;

    private boolean demoAttribute;

    public void writeSource(File file, String templateName) {
        if(file.exists()) {
            return;
        }

        ST template = template(templateName);
        template.add("modelPackage", modelPackageName);
        template.add("adaptersPackage", adaptersPackageName);
        template.add("name", name);
        template.add("demoAttribute", demoAttribute);

        for(String storageAdapter : storageAdapters) {
            template.add("storage_" + storageAdapter.replace("-", "_"), true);
        }

        try {
            template.write(file, new SkipAllSTErrorListener());
        } catch (Exception e) {
            throw new PousseCafeException("Unable to write aggregate root id file", e);
        }
    }

    private ST template(String name) {
        try {
            InputStream is = getClass().getResourceAsStream("/templates/" + name + ".st");
            return new ST(IOUtil.toString(is));
        } catch (IOException e) {
            throw new PousseCafeException("Unable to read template " + name, e);
        }
    }
}
