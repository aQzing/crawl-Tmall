package com.qzing.webmagic.spider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

//自定义实现Pipeline接口
@Component
@SuppressWarnings("all")
class TaobaoPipeline implements Pipeline  {

	// @Transactional
	public void process(ResultItems resultitems, Task task) {
		Map<String, Object> mapResults = resultitems.getAll();
		Iterator<Entry<String, Object>> iter = mapResults.entrySet().iterator();
		Map.Entry<String, Object> entry;
		while (iter.hasNext()) {
			entry = iter.next();
			if (entry.getKey().equals("productInfoList") && entry.getValue() != null) {
				List list = (List) entry.getValue();
				// 持久化
				try {
					saveproductInfoToFile(list);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void saveproductInfoToFile(List list) throws Exception {
			String path = "C:\\Users\\11073\\Desktop\\taobao\\商品信息-笔记本.txt";
	        FileWriter fw = null;
	        try {
	            //如果文件存在，则追加内容；如果文件不存在，则创建文件
	            File f=new File(path);
	            fw = new FileWriter(f, true);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        PrintWriter pw = new PrintWriter(fw);
			list.forEach(p->{
				pw.println(p);
			});
	        pw.flush();
	        try {
	            fw.flush();
	            pw.close();
	            fw.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	}



}