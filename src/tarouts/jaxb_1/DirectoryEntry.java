package tarouts.jaxb_1;

import java.nio.file.attribute.FileTime;

public class DirectoryEntry {

	private String name;
	private boolean isDirectory;
	private long size;
	private FileTime lastModifiedTime;

	DirectoryEntry(String name, boolean isDirectory, long size, FileTime lastModifiedTime) {
		this.name = name;
		this.isDirectory = isDirectory;
		this.size = size;
		this.lastModifiedTime = lastModifiedTime;
	}

	public String getName() {
		return name;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public long getSize() {
		return size;
	}

	public FileTime getLastModifiedTime() {
		return lastModifiedTime;
	}

	@Override
	public String toString() {
		return name;
	}
}
