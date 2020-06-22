package com.qzing.webmagic.spider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qzing.webmagic.pojo.ProductInfo;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
@Component
public class TaobaoSpider implements PageProcessor {
	private String targetUrl = "https://list.tmall.com/search_product.htm?q=笔记本电脑";
	@Autowired
	private TaobaoPipeline taoBaoPipeline;
	@Override
	public void process(Page page) {
		String url = page.getRequest().getUrl();
		if(targetUrl.equals(url)) {
		    crawlProductInfo(page);
			//获取总页数
			int pageTotal = getPageNum(page);
			//获取每页的大小
			int pageSize = getPageSize(page);
			//组装页面地址
			List<String>urlList = getPageUrl(pageTotal,pageSize,page);
			//爬取每页商品信息
			page.addTargetRequests(urlList);
		}else {
				crawlProductInfo(page);
		}
	}
	
	private int getPageSize(Page page) {
		int pageSize = page.getHtml().getDocument().select("div[class='view grid-nosku']").select("div[class='product']").size();
		System.out.println("每页大小："+pageSize);
		return pageSize;
	}

	/**
	 * 获取页面链接
	 * @param pageToatal
	 * @param page
	 * @return
	 */
	public List<String>  getPageUrl(int pageTotal, int pageSize, Page page) {
		//存放url
		List<String> urlList = new ArrayList<String>();
		//获取第一页
		String firstPage = page.getRequest().getUrl();
		//urlList.add(firstPage);
		//获取其他页地址
		for(int i=1;i<=pageTotal;i++) {
			String pageUrl = firstPage+"&s="+i*pageSize;
			urlList.add(pageUrl);
		}
		return urlList;
	}

	/**
	 * 获取总页数
	 * @param page
	 * @return
	 */
	public int getPageNum(Page page) {
		 String pageToatal = page.getHtml().getDocument().select("form[name='filterPageForm']").select("input[name='totalPage']").attr("value");
		 System.out.println("总共页数："+pageToatal);
		 return Integer.parseInt(pageToatal);
	}
	/**
	 * 根据搜索页爬取商品信息
	 * @param page
	 * @throws Exception 
	 */
    public void crawlProductInfo(Page page){
       List<ProductInfo>productInfoList = new ArrayList<ProductInfo>();
       System.out.println("爬取页面："+page.getRequest().getUrl());
	   Document doc = page.getHtml().getDocument();
        // 通过浏览器查看商品页面的源代码，找到信息所在的div标签，再对其进行一步一步地解析
       Elements ulList  = doc.select("div[class='view grid-nosku']");
       Elements liList = ulList.select("div[class='product']");
       liList.stream().forEach(item->{
    	   ProductInfo p = new ProductInfo();
           // 商品ID
           String id = item.select("div[class='product']").select("p[class='productStatus']").select("span[class='ww-light ww-small m_wangwang J_WangWang']").attr("data-item");
           System.out.println("商品ID：" + id);
           // 商品名称
           String name = item.select("p[class='productTitle']").select("a").attr("title");
           //System.out.println("商品名称：" + name);
           // 商品价格
           String price = item.select("p[class='productPrice']").select("em").attr("title");
           //System.out.println("商品价格：" + price);
           // 商品网址
           String goodsUrl = item.select("p[class='productTitle']").select("a").attr("href");
           //System.out.println("商品网址：" + goodsUrl);
           // 商品图片网址
           String imgUrl = item.select("div[class='productImg-wrap']").select("a").select("img").attr("data-ks-lazyload");
           //System.out.println("商品图片网址：" + imgUrl);
           // 商品店铺
           String shop = item.select("div[class='productShop']").select("a").text();
           //System.out.println("商品店铺：" + shop);
           //System.out.println("------------------------------------");
    	   p.setId(id);
    	   p.setName(name);
    	   p.setPrice(price);
    	   p.setGoodsUrl("http://"+goodsUrl);
    	   p.setImgUrl("http://"+imgUrl);
    	   p.setShop(shop);
    	   productInfoList.add(p);
       });
       page.putField("productInfoList", productInfoList);
    }
	Site site = Site.me().setRetryTimes(3).setSleepTime(100)
			.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
//			.addHeader("Cookie", "miid=1417462415476410157; cna=IkuKFeYlnAsCAXWVCioV0Fjt; thw=cn; hng=CN%7Czh-CN%7CCNY%7C156; _samesite_flag_=true; cookie2=13d8cbc6794b7e47a56592ae67d1dec0; t=d288403f3469a9331c9c4f26faa6bf88; _tb_token_=ee67ee4feeb0e; v=0; lLtC1_=1; uc3=vt3=F8dBxGR2VY03%2FA9IpE4%3D&id2=Uoe8jRgLdJ2DyA%3D%3D&lg2=VFC%2FuZ9ayeYq2g%3D%3D&nk2=AQYI6HgIuCbHgA0JfA%3D%3D; csg=63e4ad9e; lgc=bb1107382263a; dnk=bb1107382263a; skt=9b0c390505f07ce5; existShop=MTU4NzUzNjgyNg%3D%3D; uc4=nk4=0%40A6tZbQqhFIPrBN3cupikuxnZEeto2fwC&id4=0%40UO%2B6a2sbkQ85%2F7l%2B1KGK0aRpk2I3; tracknick=bb1107382263a; _cc_=URm48syIZQ%3D%3D; sgcookie=E9kd1qgyMSvOk%2Bx66dMhM; enc=qWlpSKNoErXLQgXINnprEUx5fuwUVtb%2FL7HL%2BQN4DB1oAAZRUZVSWEiSZChr7X6%2BgxZWbQ8BLqzm5oQSIk1dLw%3D%3D; tfstk=cTTOBRx64vDGuZON4EnhlQ3fHyklapoOnvXzkjwKs1NzUc0i7sqyrU8cmyf8Bwhd.; tk_trace=oTRxOWSBNwn9dPyorMJE%2FoPdY8zfvmw%2Fq5hp2qIVuPhvyALIyVLcmqfph6ry163J87EhemlAnu%2FxIqCx7OCxzAFskfDTBqWHFNDH4Fqdu7Grucn%2Bjpo63%2FOZHSNyMKbI8KRdYFjbFRz4qdOYiLm%2BrTK1ZTAbtr%2BrXX3cgu%2Bb2pGhRQyupV8NeaFrkbnEZ9dH9w%2Bs25zPSv%2BnVhk87GTBIJ2ksl849fCByHUTwoD%2FAnAwGbMVgKmhDYXJ6NRMSV2oXGGxCiyz3D%2BUBoZUXEdG17OW%2BgiHCw%3D%3D; mt=ci=-1_0; alitrackid=www.taobao.com; lastalitrackid=www.taobao.com; JSESSIONID=0C9DF44D9D98791BD78FA0BAE794D320; uc1=cookie16=Vq8l%2BKCLySLZMFWHxqs8fwqnEw%3D%3D&cookie21=UtASsssmeW6lpyd%2BB%2B3t&cookie15=Vq8l%2BKCLz3%2F65A%3D%3D&existShop=false&pas=0&cookie14=UoTUPcqcAE7spQ%3D%3D; l=eBx3Yo9qqma5YveCBOfwdAkI1zQ9jIRYou8wRckkiT5P9aCp5j25WZjYd8T9CnGVh65DR3kRxbhvBeYBqIv4n5U62j-la_Mmn; isg=BO_vs5GS7cYwwOrW1UPBRcWzfgP5lEO2tFO0RQF8j95lUA9SCWUmBioC1kDuLhsu")
//			.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
//			.addCookie("jiaowu.sicau.edu.cn", "ASPSESSIONIDCSRARDRA", "MBOKABCAEKMOPGBBLMLACNJM")
			//.addCookie("jiaowu.sicau.edu.cn", "ASPSESSIONIDCSRARDRA", "MBOKABCAEKMOPGBBLMLACNJM")
			;
	@Override
	public Site getSite() {
		return site;
	}

	@Scheduled(fixedRate=1000*60*60*5,initialDelay = 1000*1)
	public void taobaoSpiderRun() {
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		System.out.println("========任务执行时间："+time);
		Spider.create(new TaobaoSpider())
		        .addUrl(targetUrl)
				.addPipeline(taoBaoPipeline)
				.thread(1)
				.run();
	}
}
