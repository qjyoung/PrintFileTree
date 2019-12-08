package top.qjyoung.qtree.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
	// ─ │ ├ └

	file功能不能需求,给他包装一下
	对于最后一个被遍历到的文件夹有点特殊,他前面打印的符号是 └,其他都是├
	
	先打印纯文件,在遍历文件夹
*/
public class FileWrapper /*extends File*/ {
    private File file;
    // 标记是不是中间孩子节点(true是中间孩子节点，false末尾孩子节点)
    // 两个用途，一是决定每个孩子节点之前的前缀排列情况。即每个位置上是树杠"|"还是空格" "
    // 二是决定每个孩子节点紧前的符号是"├"还是"└"，true为"├"false为"└"
    private List<Boolean> flags = new ArrayList<>();
    // 所有文件夹
    private List<FileWrapper> dirs = new ArrayList<>();
    // 所有文件
    private List<File> files = new ArrayList<>();
    // 是否是独子
    private boolean isSingle;
    
    /*
     * 写在这里相当于一个递归,当程序启动时
       FileWrapper fileWrapper = new FileWrapper(file, isSingle);
     * 这句会一直执行下去,直到根文件夹下所有文件对象都被new出来
     * 可能会导致堆溢出
     * 但我试了遍历D盘,打印65万行,没有出现溢出
     * 保险起见,将这段代码写在init方法里 每次print递归的时候手动初始化一下
        File[] totalFiles = file.listFiles(new FilenameFilter() {
    
            @Override
            public boolean accept(File dir, String name) {
                return !name.equals("bin");
            }
        });
    
        if (totalFiles != null) {
            boolean isSingle = totalFiles.length == 1 ? true : false;
            for (File file : totalFiles) {
                if (file.isDirectory()) {
                    //System.out.println("dir--" + file.getName());
                    FileWrapper fileWrapper = new FileWrapper(file, isSingle);
                    dirs.add(fileWrapper);
                } else {
                    //System.out.println("file--" + file.getName());
                    files.add(file);
                }
            }
        }
    */
    public FileWrapper(String pathname) {
        file = new File(pathname);
    }
    
    public FileWrapper(File file, boolean isSingle) {
        this.file = file;
        this.isSingle = isSingle;
    }
    
    public void init() {
        //---得到所有子文件,并分类放入不同list
        File[] totalFiles = file.listFiles((dir, name) -> {
            return true/*!name.equals("bin")*/;
        });
        
        if (totalFiles != null) {
            boolean isSingle = totalFiles.length == 1;
            for (File file : totalFiles) {
                if (file.isDirectory()) {
                    FileWrapper fileWrapper = new FileWrapper(file, isSingle);
                    dirs.add(fileWrapper);
                } else {
                    files.add(file);
                }
            }
        }
    }
    
    /////////////////////// getName ////////////////////////////////
    public String getName() {
        return file.getName();
    }
    
    /////////////////////// flags ////////////////////////////////
    public List<Boolean> getFlags() {
        return flags;
    }
    
    public void setFlags(List<Boolean> flags) {
        this.flags = flags;
    }
    
    /////////////////// sons Files dirs /////////////////////////////////////
    public List<FileWrapper> getDirs() {
        return dirs;
    }
    
    public List<File> getFiles() {
        return files;
    }
    
    /////////////////// isSingle /////////////////////////////////////
    public boolean isSingle() {
        return isSingle;
    }
}
