package com.bonc.uni.nlp.entity.menu;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月30日 下午7:50:32 
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_second_menu")
public class SecondMenu {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 二级菜单名称
	 */
	@Column(name = "name")
	private String name;
	/**
	 * 一级菜单id
	 */
	@Column(name = "first_menu_id")
	private String firstMenuId;
	/**
	 * 二级菜单对应的url
	 */
	@Column(name = "url")
	private String url;
	/**
	 * 排序
	 */
	@Column(name = "menu_index")
	private String index;


}
 
 