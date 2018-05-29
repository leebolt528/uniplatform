package com.bonc.uni.common.entity.user.resultset;

import java.sql.Timestamp;

public interface ISharedDataSetInfo {

    Integer getDataSetShareId();

    Timestamp getDataSetShareDate();

    String getSharer();

    String getReceiver();

    Integer getDsiId();

    String getDsiName();

    String getDsiDescription();

    String getDsiTitle();

}
