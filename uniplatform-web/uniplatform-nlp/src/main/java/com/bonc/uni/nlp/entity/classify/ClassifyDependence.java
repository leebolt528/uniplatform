package com.bonc.uni.nlp.entity.classify;

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
 * 分类体系依赖对应实体
 * @author zlq
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_classify_dependence")
public class ClassifyDependence {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 本身分类体系id
	 */
	@Column(name="post_classify_id", unique = false, nullable = false)
	private String postClassifyId;
	/**
	 * 前置依赖的分类体系id
	 */
	@Column(name="pre_classify_id", unique = false, nullable = false)
	private String preClassifyId = "0";
	/**
	 * 前置依赖的分类对象id
	 */
	@Column(name="pre_object_id", unique = false, nullable = false)
	private String preObjectId = "0";
}
