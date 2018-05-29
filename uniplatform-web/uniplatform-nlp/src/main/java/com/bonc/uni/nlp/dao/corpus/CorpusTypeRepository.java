package com.bonc.uni.nlp.dao.corpus;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonc.uni.nlp.entity.corpus.CorpusType;

public interface CorpusTypeRepository extends JpaRepository<CorpusType, String> {

}
