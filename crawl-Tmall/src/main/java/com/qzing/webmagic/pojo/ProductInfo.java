package com.qzing.webmagic.pojo;

import lombok.Data;

/**
 * 商品信息
 * @author 11073
 *
 */
@Data
public class ProductInfo {
	private String id;
	private String name;
	private String price;
	private String goodsUrl;
	private String imgUrl;
	private String shop;	
}
