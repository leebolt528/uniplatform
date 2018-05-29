package com.bonc.uni.nlp.entity.dic;

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
 * 
 * @author zlq
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_dic_sub_type")
public class DicSubType {
	
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;	
	
	@Column(name = "name", unique = true, nullable = false)
	private String name;
	
	@Column(name = "display_name", unique = true, nullable = false)
	private String disPlayName;
	
	@Column(name = "format", unique = false, nullable = false)
	private String format;
	
	@Column(name="subtype_index" ,unique = true, nullable = false)
	private Integer index;
}
