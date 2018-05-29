package com.bonc.uni.dcci.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.bonc.uni.common.entity.EntityCommon;
import com.bonc.uni.dcci.util.APIType;

/**
 * 数据商城api
 * @author futao
 * 2017年9月22日
 */
@Entity
@Table(name = "`dcci_data_api`")
public class DataApi extends EntityCommon{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 名
	 */
	@Column(name = "`NAME`")
	private String name;
	
	/**
	 * 价格
	 */
	@Column(name = "`PRICE`",columnDefinition = "bigint(10) DEFAULT 0")
	private int price;
	
	/**
	 * 规格
	 */
	@Column(name = "`SPECIFICATIONS`",columnDefinition = "bigint(20) DEFAULT 0")
	private long specifications;
	
	/**
	 * 单位
	 * 次
	 * 天
	 * 周
	 * 月
	 * 年
	 */
	@Column(name = "`UNIT`")
	@Enumerated(EnumType.STRING)
	private APIType.Unit unit;
	
	/**数据分类
	 * 
	 */
	@Column(name = "`TYPE`")
	@Enumerated(EnumType.STRING)
	private APIType type;

	/**
	 * 调用数
	 */
	@Column(name = "`COUNT`", columnDefinition = "bigint(20) DEFAULT 0")
	private long count;
	
	/**：
	 * 状态  
	 */
	@Column(name = "`STATUS`")
	@Enumerated(EnumType.STRING)
	private APIType.Status status;
	
	/**
	 * 描述
	 */
	@Column(name = "`DESCRIPTION`")
	private String description;
	
	/**
	 * 图片路径
	 */
	@Column(name = "`IMAGE_PATH`")
	private String imagePath;
	
	@Column(name = "`IMAGE_NAME`")
	private String imageName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public long getSpecifications() {
		return specifications;
	}

	public void setSpecifications(long specifications) {
		this.specifications = specifications;
	}

	public APIType.Unit getUnit() {
		return unit;
	}

	public void setUnit(APIType.Unit unit) {
		this.unit = unit;
	}

	public APIType getType() {
		return type;
	}

	public void setType(APIType type) {
		this.type = type;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public APIType.Status getStatus() {
		return status;
	}

	public void setStatus(APIType.Status status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
}
