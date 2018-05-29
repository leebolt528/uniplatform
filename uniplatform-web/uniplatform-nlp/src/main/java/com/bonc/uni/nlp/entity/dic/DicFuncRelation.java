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
@Table(name = "nlap_dic_function_relation")
public class DicFuncRelation {
	
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	
	@Column(name = "dic_id", unique = false, nullable = false)
	private String dicId;
	
	@Column(name = "function_id", unique = false, nullable = false)
	private String functionId;
	
	@Column(name = "dic_status", unique = false, nullable = false)
	private int status;
}
