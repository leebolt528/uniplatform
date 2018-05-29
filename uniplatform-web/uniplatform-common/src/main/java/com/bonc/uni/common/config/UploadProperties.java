package com.bonc.uni.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@ConfigurationProperties
public class UploadProperties {

	public static File file;

	static {
		file = new File(System.getProperty("user.dir") + File.separator + "resource");
	}

	public final static String root = file.getAbsolutePath();

	public UploadProperties() {}

	public String getRoot() {
		return root;
	}

	public String getCommom() {
		return root + File.separator + "common";
	}

	public String getCorpus() {
		return root + File.separator + "corpus";
	}

	public String getDcci() {
		return root + File.separator + "dcci";
	}

	public String getNlp() {
		return root + File.separator + "nlp";
	}

	public String getUsou() {
		return root + File.separator + "usou";
	}
}
