package org.clarksnut.files;

import org.w3c.dom.Document;

public class FlyWeightXmlFileModel implements XmlFileModel {

    protected final XmlFileModel file;
    protected Document document;

    public FlyWeightXmlFileModel(XmlFileModel file) {
        this.file = file;
    }

    @Override
    public String getId() {
        return file.getId();
    }

    @Override
    public String getFilename() {
        return file.getFilename();
    }

    @Override
    public byte[] getFile() {
        return file.getFile();
    }

    @Override
    public long getChecksum() {
        return file.getChecksum();
    }

    @Override
    public Document getDocument() throws Exception {
        if (document == null) {
            this.document = file.getDocument();
        }
        return document;
    }

}
