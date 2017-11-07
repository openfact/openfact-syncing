package org.openfact.theme.freemarker;

import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.openfact.config.ThemeConfig;
import org.openfact.theme.Theme;
import org.openfact.theme.exceptions.FreeMarkerException;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class FreeMarkerUtil {

    private final ThemeConfig config;
    private ConcurrentHashMap<String, Template> cache;

    @Inject
    public FreeMarkerUtil(ThemeConfig config) {
        this.config = config;
    }

    @PostConstruct
    private void init() {
        if (config.getCacheTemplates(true)) {
            cache = new ConcurrentHashMap<>();
        }
    }

    public String processTemplate(Object data, String templateName, Theme theme) throws FreeMarkerException {
        try {
            Template template;
            if (cache != null) {
                String key = theme.getName() + "/" + templateName;
                template = cache.get(key);
                if (template == null) {
                    template = getTemplate(templateName, theme);
                    if (cache.putIfAbsent(key, template) != null) {
                        template = cache.get(key);
                    }
                }
            } else {
                template = getTemplate(templateName, theme);
            }

            Writer out = new StringWriter();
            template.process(data, out);
            return out.toString();
        } catch (Exception e) {
            throw new FreeMarkerException("Failed to process template " + templateName, e);
        }
    }

    private Template getTemplate(String templateName, Theme theme) throws IOException {
        Configuration cfg = new Configuration();
        cfg.setTemplateLoader(new ThemeTemplateLoader(theme));
        return cfg.getTemplate(templateName, "UTF-8");
    }

    class ThemeTemplateLoader extends URLTemplateLoader {

        private Theme theme;

        public ThemeTemplateLoader(Theme theme) {
            this.theme = theme;
        }

        @Override
        protected URL getURL(String name) {
            try {
                return theme.getTemplate(name);
            } catch (IOException e) {
                return null;
            }
        }

    }

}