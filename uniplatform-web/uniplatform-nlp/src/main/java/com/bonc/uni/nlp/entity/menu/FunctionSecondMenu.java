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
* @version: 2018年1月5日 下午5:09:24 
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_function_second_menu")
public class FunctionSecondMenu {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 功能展示二级菜单名称
	 */
	@Column(name = "name")
	private String name;
	/**
	 * 功能展示二级菜单url
	 */
	@Column(name = "url")
	private String url;
	/**
	 * 功能展示一级菜单id
	 */
	@Column(name = "function_id")
	private String functionId;
	/**
	 * 二级菜单顺序
	 */
	@Column(name = "function_second_index")
	private String index;
}
 