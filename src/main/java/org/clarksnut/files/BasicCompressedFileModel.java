package org.clarksnut.files;

import org.clarksnut.files.uncompress.exceptions.NotReadableCompressFileException;

import java.util.List;

public class BasicCompressedFileModel implements CompressedFileModel {

    private final FileModel file;

    public BasicCompressedFileModel(FileModel file) {
        this.file = file;
    }

    @Override
    public List<FileModel> getChildren() throws NotReadableCompressFileException {
        return FileModelUtils.uncompress(file);
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
}
