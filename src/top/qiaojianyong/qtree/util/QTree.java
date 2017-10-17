
package top.qiaojianyong.qtree.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QTree {
	private static final String SPACE = "  ";
	private static final boolean COLLAPSE = true;

	private static String ENTER = System.getProperty("line.separator");

	public static void main(String args[]) throws IOException {
		long time_start = System.currentTimeMillis();
		System.out.println("processing...");
		String dir = "";
		//		dir = "D:\\";
		dir = "C:\\_QJY\\MyDevelop\\MyJava\\Tree";

		//----------------从外部接收数据 通过命令行传参数--------------------------------------
		//---命令行传过来的参数 String args[]
		//---(注意文件夹中不能出现空格,否则你的路径名会被split(" ")切割放进数组里,所以这里要拼接一下,以防万一,)
		//		for (String s : args) {
		//			dir += s.replace("\\", "\\\\") + " ";
		//		}
		//		dir = dir.trim();
		//------------------------------------------------------
		FileWrapper fileWrapper = new FileWrapper(dir);
		File targetFile = new File(dir + "\\tree-"
				+ new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(System.currentTimeMillis())) + ".txt");

		PrintWriter print = new PrintWriter(new OutputStreamWriter(new FileOutputStream(targetFile), "utf-8"), true);
		print(fileWrapper, print);

		System.out.println("---finish---");

		long time_end = System.currentTimeMillis();
		long timeElapsed = time_end - time_start;

		long ms = timeElapsed % 1000;
		long s = (timeElapsed / 1000) % 60;
		long min = timeElapsed / 1000 / 60;

		String timeElapsedStr = ENTER + ENTER + ENTER + min + " min" + ENTER + s + " s" + ENTER + ms + " ms" + ENTER
				+ ENTER;
		System.out.println(timeElapsedStr);
		print.print(timeElapsedStr);
		print.close();
	}

	/**
	 *  ─ │ ├ └
	 *  
	 * @param fileWrapper
	 * @param print
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	public static void print(FileWrapper fileWrapper, PrintWriter print) throws IOException {
		//---初始化 得到孩子
		fileWrapper.init();

		List<File> files = fileWrapper.getFiles();
		List<FileWrapper> dirs = fileWrapper.getDirs();

		int filesSize = files.size(); // file个数
		int dirsSize = dirs.size(); // dir个数

		List<Boolean> flags = new ArrayList<Boolean>();
		flags.addAll(fileWrapper.getFlags());// 获得祖上传下来的信息(标记)

		boolean hasAnOnlyChildAndIsDir = dirsSize == 1 && filesSize == 0;

		//---------打印自己(当前文件夹)----------------------------------------------
		if (COLLAPSE && fileWrapper.isSingle()) {//折叠且独子,不打印prefix
			if (hasAnOnlyChildAndIsDir)//是独子,有且只有一个孩子,并且是文件夹类型-->不换行,否则换行
				print.print("/" + fileWrapper.getName());
			else
				print.println("/" + fileWrapper.getName());
		} else {//非独子.打印prefix+fileName
			StringBuilder prefix = new StringBuilder();
			for (int i = 0; i <= flags.size() - 1; i++) {
				prefix.append(SPACE);
				if (i == flags.size() - 1) { //最后一个记录,也就是自己
					if (flags.get(i))// 如果不是小儿子true
						prefix.append("├");
					else
						prefix.append("└");// 小儿子
					break;//循环结束
				}
				if (flags.get(i))// (祖先)非小儿子true
					prefix.append("│");
				else
					prefix.append(" ");
			}
			prefix.append("─");
			//---有且只有一个孩子,并且是文件夹类型-->不换行,否则换行
			if (COLLAPSE && hasAnOnlyChildAndIsDir) {
				print.print(prefix.toString() + fileWrapper.getName());
			} else {
				print.println(prefix.toString() + fileWrapper.getName());
			}
		}

		//---------子文件(先打印)----------------------------------------------

		StringBuilder prefix = new StringBuilder();
		for (int i = 0; i <= flags.size() - 1; i++) {
			prefix.append(SPACE);
			if (flags.get(i))//---他的爸爸不是小儿子
				prefix.append("│");
			else
				prefix.append(" ");//---他的爸爸是小儿子
		}
		prefix.append(SPACE);

		if (dirsSize != 0)//---如果文件夹下还有子文件夹
			prefix.append("│");

		for (File file : files) {
			print.println(prefix.toString() + " " + file.getName());
		}

		//---------遍历子文件夹(传递标记到孩子)----------------------------------------------

		for (int j = 0; j < dirsSize; j++) {
			FileWrapper fileWrapper_son = dirs.get(j);
			List<Boolean> flagsTemp = new ArrayList<>();
			flagsTemp.addAll(flags);

			//---如果折叠,并且是独子(dirsSize==1),下面不执行(不需标记),反之执行(原因:将多级单一文件夹看成一个,整体标记跟随此行第一个文件夹)
			if (!(COLLAPSE && fileWrapper_son.isSingle()))
			if (j == dirsSize - 1)//最后一个
				flagsTemp.add(false);
			else
				flagsTemp.add(true);
			fileWrapper_son.setFlags(flagsTemp);
			print(fileWrapper_son, print);
		}
	}
}
