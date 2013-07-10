package tarouts.jaxb_1;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class Main {

	public static void doExample() {

		//Переменная, ранее описанного класса(Customer):
		Customer customer = new Customer();
		customer.setId(100);
		customer.setName("Pavel");
		customer.setAge(99);

		try
		{
			// Открываем XML файл
			File file = new File("jaxb.xml");

			// Открываем контекст, связанный с нашим классом Customer
			JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// Указываем, что вывод будет форматированным
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Выплевываем объект в файл
			jaxbMarshaller.marshal(customer, file);

			// Выплевываем объект в System.out
			jaxbMarshaller.marshal(customer, System.out);
		}
		catch (Exception ex)
		{
			System.out.println(ex.getLocalizedMessage());
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		if (args.length == 0) {
			System.out.println("No directory specified");
			return;
		}

		OutputStream outputStream = null;

		try {

			Path directoryPath = Paths.get(args[0]);

			DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath);

			List<DirectoryEntry> entries = new ArrayList<>();

			for (Path childPath : directoryStream) {

				BasicFileAttributeView attributeView = Files.getFileAttributeView(childPath, BasicFileAttributeView.class);

				BasicFileAttributes fileAttributes = attributeView.readAttributes();

				DirectoryEntry entry = new DirectoryEntry(
						childPath.getFileName().toString(),
						fileAttributes.isDirectory(),
						fileAttributes.size(),
						fileAttributes.lastModifiedTime()
				);

				entries.add(entry);
			}

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

			for (DirectoryEntry entry : entries) {
				System.out.println(entry.getName());
			}

			// Получаем полный путь "index.xhtml"
			Path indexPath = Paths.get(directoryPath.toString(), "index.xhtml");

			// Создаем байтовый поток
			outputStream = new FileOutputStream(indexPath.toString());

			// Создаем символьный поток
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

			// Создаем буферизированный символьный поток
			BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

			// Оборачиваем буферизированный символьный поток в PrintWriter, который умеет делать форматированный вывод
			PrintWriter printWriter = new PrintWriter(bufferedWriter);

			printWriter.println("");

			//<tr>
			//<td>
			//<a href="1.txt">1.txt</a>
			//</td>
			//<td>
			//		01.01.2013
			//		</td>
			//<td>
			//		1Kb
			//		</td>
			//</tr>
			//<tr>
			//<td>
			//<a href="1.txt">1.txt</a>
			//</td>
			//<td>
			//		01.01.2013
			//		</td>
			//<td>
			//		1Kb
			//		</td>
			//</tr>


			//<?xml version="1.0" encoding="UTF-8" ?>
			//<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
			//<html xmlns="http://www.w3.org/1999/xhtml">
			//<head>
			//<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
			//<title>Directory contents</title>
			//<style>
			//		td {
			//	width: 200px;
			//	border: 1px solid black;
			//}
			//</style>
			//</head>
			//<body>
			//<table>
			//<here/>
			//</table>
			//</body>
			//</html>



		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}
}
