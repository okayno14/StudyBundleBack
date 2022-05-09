package dataAccess.repository;

import dataAccess.entity.Bundle;
import exception.DataAccess.*;
import exception.DataAccess.FileNotFoundException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BundleRepoFile implements IBundleRepoFile
{
	private       WordParser wordParser = new WordParser();
	private       String     storage;
	private       String     supportedFormats[];
	private       int        zipFileSizeLimit;
	private final Charset    charset    = Charset.forName("cp866");

	public BundleRepoFile(String storage, String[] supportedFormats, int zipFileSizeLimit)
	{
		this.storage          = storage;
		this.supportedFormats = supportedFormats;
		this.zipFileSizeLimit = zipFileSizeLimit;
	}

	private boolean checkFormat(String name)
	{
		boolean res = false;
		for (String format : supportedFormats)
		{
			res = res || name.contains(format);
		}
		return res;
	}

	private void checkBundleDir(String path) throws IOException
	{
		Path p  = Paths.get(path);
		Path p1 = null;
		int  a  = p.getNameCount();

		for (int i = 1; i <= a; i++)
		{
			p1 = p.subpath(0, i);
			p1 = Paths.get(p.getRoot().toString(), p1.toString());
			if (!Files.exists(p1))
			{
				Files.createDirectory(p1);
			}
		}
	}

	private void reportWrite(Bundle bundle, String bundleDir, ZipInputStream zIN, ZipEntry zipEntry)
			throws IOException
	{
		String name = zipEntry.getName();
		if (!checkFormat(name))
		{
			throw new DataAccessException(new FormatNotSupported(name));
		}
		File oldReport = new File(bundleDir + "/" + bundle.getReport().getFileName());
		if (oldReport.exists())
		{
			oldReport.delete();
		}
		byte buf[] = new byte[(int) zipEntry.getSize()];
		//делаю побитовое чтение, так как
		//при порционном чтении APACHE POI ругается на повреждённый документ
		//		while (zIN.read(buf,0,2048) != -1)
		//		{
		//			continue;
		//		}
		for (int i = 0; i < zipEntry.getSize(); i++)
		{
			buf[i] = (byte) zIN.read();
		}
		ByteArrayInputStream doc  = new ByteArrayInputStream(buf);
		String               text = wordParser.parseDoc(doc);
		bundle.getReport().setFileNameAndMeta(name, text);
		doc.reset();
		FileOutputStream fOut       = new FileOutputStream(bundleDir + "/" + name);
		int              readed     = 0;
		byte             bufWrite[] = new byte[1024];
		while ((readed = doc.read(bufWrite)) != -1)
		{
			fOut.write(bufWrite, 0, readed);
		}
		doc.close();
		fOut.close();
		zIN.closeEntry();
	}

	private boolean cleanDir(File toDel)
	{
		File[] content = toDel.listFiles();
		if (content != null)
		{
			for (int i = 0; i < content.length; i++)
			{
				cleanDir(content[i]);
			}
		}
		return toDel.delete();
	}

	private void srcWrite(String bundleDir, ZipInputStream zIN) throws IOException
	{
		File srcDir = new File(bundleDir + "/src");
		if (srcDir.exists())
		{
			cleanDir(srcDir);
		}
		srcDir.mkdir();

		ZipEntry zipEntry = null;
		while ((zipEntry = zIN.getNextEntry()) != null)
		{
			String name = zipEntry.getName();
			File   file = new File(bundleDir + "/" + name);
			if (name.endsWith("/"))
			{
				file.mkdir();
			}
			else
			{
				file.createNewFile();
				BufferedOutputStream fOUT = new BufferedOutputStream(new FileOutputStream(file));
				int                  c    = 0;
				while ((c = zIN.read()) != -1)
				{
					fOUT.write(c);
				}
				fOUT.close();
			}
			zIN.closeEntry();
		}
	}

	@Override
	public void save(Bundle bundle, byte array[])
	{
		if (array.length > zipFileSizeLimit)
		{
			throw new DataAccessException(new ZipFileSizeException(array.length, zipFileSizeLimit));
		}
		try (ZipInputStream zIN = new ZipInputStream(new ByteArrayInputStream(array), charset))
		{
			String bundleDir = storage + "/" + bundle.getFolder();
			checkBundleDir(bundleDir);
			ZipEntry zipEntry = null;
			while ((zipEntry = zIN.getNextEntry()) != null)
			{
				String name = zipEntry.getName();
				if (name.equals("src/"))
				{
					srcWrite(bundleDir, zIN);
				}
				else
				{
					reportWrite(bundle, bundleDir, zIN, zipEntry);
				}
			}
		}
		catch (WordParserException | IOException e)
		{
			throw new DataAccessException(e);
		}
		catch (IllegalArgumentException ee)
		{
			throw new DataAccessException(new ZipDamaged());
		}
	}

	private void zipFolderFromStorage(String root, File zipDir, ZipOutputStream out)
			throws IOException
	{
		//Заход в глубь дерева
		File content[] = zipDir.listFiles();
		if (content != null)
		{
			for (int i = 0; i < content.length; i++)
			{
				zipFolderFromStorage(root, content[i], out);
			}
		}
		//зашли в узел
		String absName = zipDir.getAbsolutePath();
		absName = absName.replace("\\", "/");
		String relName = absName.replace(root, "");
		//если вершина дерева
		if (root.equals(absName + "/"))
		{
			out.closeEntry();
			out.close();
			return;
		}
		//если вершина поддерева
		if (zipDir.isDirectory())
		{
			out.putNextEntry(new ZipEntry(relName + "/"));
			out.closeEntry();
			return;
		}
		//если лист (файл)
		try (BufferedInputStream file = new BufferedInputStream(new FileInputStream(zipDir)))
		{
			out.putNextEntry(new ZipEntry(relName));
			int c = 0;
			while ((c = file.read()) != -1)
			{
				out.write(c);
			}
		}
		finally
		{
			out.closeEntry();
		}
	}

	@Override
	public byte[] get(Bundle bundle)
	{
		if (bundle.getReport().getFileName() == null)
		{
			throw new DataAccessException(new FileNotFoundException(bundle));
		}
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			 ZipOutputStream out = new ZipOutputStream(byteArrayOutputStream))
		{
			zipFolderFromStorage(storage + "/" + bundle.getFolder() + "/",
								 new File(storage + "/" + bundle.getFolder()), out);
			return byteArrayOutputStream.toByteArray();
		}
		catch (IOException e)
		{
			throw new DataAccessException(e);
		}
	}

	@Override
	public void fillTextVector(Bundle bundle)
	{
		if (bundle.getReport().getFileName() == null)
		{
			throw new DataAccessException(new FileNotFoundException(bundle));
		}
		String name = storage + "/" + bundle.getFolder() + "/" + bundle.getReport().getFileName();
		bundle.getReport().setFileNameAndMeta(name, wordParser.parseDoc(name));
	}

	@Override
	public void fillTextVector(List<Bundle> bundleList)
	{
		Iterator<Bundle> iterator = bundleList.iterator();
		while (iterator.hasNext())
		{
			fillTextVector(iterator.next());
		}
	}

	private Path changeGroup(Path oldPath, String OLD_GROUP, String NEW_GROUP)
	{
		String str = oldPath.toString();
		str = str.replaceFirst(OLD_GROUP, NEW_GROUP);
		return Paths.get(str);
	}


	private void moveBundleRec(File oldBundleNode, String OLD_GROUP, String NEW_GROUP)
			throws IOException
	{
		File children[] = oldBundleNode.listFiles();
		//цикл обхода
		if (children != null)
		{
			for (File child : children)
			{
				if (child.isDirectory())
				{
					Path destination = changeGroup(child.toPath(), OLD_GROUP, NEW_GROUP);
					Files.createDirectory(destination);
				}
				moveBundleRec(child, OLD_GROUP, NEW_GROUP);
			}
		}
		//обработка
		if (oldBundleNode.isDirectory())
		{
			return;
		}
		Path source      = Paths.get(oldBundleNode.getPath());
		Path destination = changeGroup(source, OLD_GROUP, NEW_GROUP);
		Files.move(source, destination);
	}

	@Override
	public void moveGroupChanged(Bundle client, String destinationFolder)
	{
		StringBuffer buffer    = new StringBuffer(client.getFolder());
		int          b         = buffer.indexOf("/");
		final String OLD_GROUP = buffer.substring(0, b);
		buffer = new StringBuffer(destinationFolder);
		b      = buffer.indexOf("/");
		final String NEW_GROUP = buffer.substring(0, b);

		File oldFolder = new File(storage + "/" + client.getFolder());
		try
		{
			checkBundleDir(storage + "/" + destinationFolder);
			moveBundleRec(oldFolder, OLD_GROUP, NEW_GROUP);
			delete(client);
			client.setFolder(destinationFolder);
		}
		catch (IOException e)
		{
			throw new DataAccessException(e);
		}
	}

	@Override
	public void delete(Bundle bundle)
	{
		File toDel = new File(storage + "/" + bundle.getFolder());
		if (!cleanDir(toDel))
		{
			throw new DataAccessException(new FileNotFoundException(bundle));
		}
		File root = new File(storage);
		for (File node = toDel.getParentFile();
			 !node.equals(root) && node.listFiles().length == 0 && node != null;
			 node = node.getParentFile())
		{
			node.delete();
		}
	}
}
