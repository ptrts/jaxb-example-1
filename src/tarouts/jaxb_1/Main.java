package tarouts.jaxb_1;

import tarouts.jaxb_1.business.DirectoryEntry;
import tarouts.jaxb_1.mapping.TR;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {

	public static List<DirectoryEntry> getDirectoryEntries(Path path) {

		// Делаем поток чтения содержимого директории
		DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath);

		// Заготавливаем список элементов директории
		List<DirectoryEntry> entries = new ArrayList<>();


		DirectoryEntry entry = new DirectoryEntry(
				"..",
				true,
				0,
				null
		);

		// Цикл по всем элементам директории
		for (Path childPath : directoryStream) {

			BasicFileAttributeView attributeView = Files.getFileAttributeView(childPath, BasicFileAttributeView.class);

			BasicFileAttributes fileAttributes = attributeView.readAttributes();

			entry = new DirectoryEntry(
					childPath.getFileName().toString(),
					fileAttributes.isDirectory(),
					fileAttributes.size(),
					fileAttributes.lastModifiedTime()
			);

			entries.add(entry);
		}

		// Сортируем чего насобирали
		Collections.sort(
				entries,
				new Comparator<DirectoryEntry>() {
					@Override
					public int compare(DirectoryEntry entry1, DirectoryEntry entry2) {
						if (entry1.isDirectory() == entry2.isDirectory()) {
							return entry1.getName().compareToIgnoreCase(entry2.getName());
						} else if (entry1.isDirectory()) {
							return -1;
						} else {
							return 1;
						}
					}
				}
		);

		return entries;
	}

	public static void main(String[] args) throws IOException {

		if (args.length == 0) {
			System.out.println("No directory specified");
			return;
		}

		OutputStream outputStream = null;
		PrintWriter printWriter = null;

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;

		try {

			// Получаем директорию из параметра вызова
			Path directoryPath = Paths.get(args[0]);

			// Получаем список объектов "DirectoryEntry"
			List<DirectoryEntry> entries = getDirectoryEntries(directoryPath);


			///////////////////////////////////////////////////////////////
			// ПОЛУЧАЕМ PRINT WRITER
			///////////////////////////////////////////////////////////////

			// Получаем полный путь "index.xhtml"
			Path indexPath = Paths.get(directoryPath.toString(), "index.xhtml");

			// Создаем байтовый поток
			outputStream = new FileOutputStream(indexPath.toString());

			// Создаем символьный поток
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

			// Создаем буферизированный символьный поток
			BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

			// Оборачиваем буферизированный символьный поток в PrintWriter, который умеет делать форматированный вывод
			printWriter = new PrintWriter(bufferedWriter);


			///////////////////////////////////////////////////////////////
			// ПОЛУЧАЕМ BUFFERED READER
			///////////////////////////////////////////////////////////////

			inputStream = Main.class.getResourceAsStream("template.xhtml");
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputStreamReader);


			///////////////////////////////////////////////////////////////
			// ИНИЦИАЛИЗИРУЕМ МАРШАЛЛЕР
			///////////////////////////////////////////////////////////////

			// Открываем контекст, связанный с нашим классом Customer
			JAXBContext jaxbContext = JAXBContext.newInstance(TR.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// Делаем настройки маршаллера
			jaxbMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", false);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);


			///////////////////////////////////////////////////////////////
			// БЕРЕМ С ТЕМПЛЕЙТА - ПИШЕМ В ФАЙЛ (ИНОГДА ЧЕРЕЗ МАРШАЛЛЕР)
			///////////////////////////////////////////////////////////////

			String line = bufferedReader.readLine();
			while (line != null) {

				String trimmedLine = line.trim();

				if (trimmedLine.equals("HERE!")) {

					for (DirectoryEntry directoryEntry : entries) {

						FileTime lastModifiedTime = directoryEntry.getLastModifiedTime();

						TR tr = new TR(
								directoryEntry.getName(),
								directoryEntry.getName(),
								Long.toString(directoryEntry.getSize()),
								lastModifiedTime == null ? "" : lastModifiedTime.toString()
						);

						// Выплевываем объект в файл
						jaxbMarshaller.marshal(tr, printWriter);
					}

				} else {
					printWriter.println(line);
				}

				line = bufferedReader.readLine();
			}


		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if (printWriter != null) {
				printWriter.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}
}
