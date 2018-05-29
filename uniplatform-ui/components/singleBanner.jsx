import React, { Component } from 'react';
import { Row, Col, ButtonGroup, Button} from 'epm-ui';

class SingleBanner extends Component {

  getClusterDetail(id){
      window.open(`/usou/clusterDetail?id=${id}`,'_self');
  }
  getIndise(id){
      window.open(`/usou/indexManage?id=${id}`,'_self');
  }
  getSearchData(id){
      window.open(`/usou/searchData?id=${id}`,'_self');
  }
  getParticiple(id){
      window.open(`/usou/dicManage?id=${id}`,'_self');
  }
  getMonitor(id){
      window.open(`/usou/monitorManage?id=${id}`,'_self');
  }
  
  render() {
    const { prefix,id } = this.props;
    return (
      <div className="singleBannerDiv">
          <Row className="singleBanner_place"></Row>
        <Row className="singleBanner">
          <Col className="singleBannerCol" size={{ normal: 24, small: 24, medium: 8, large: 8 }}>
            <span className="singleBannerCom">BONC</span>
            <span className="singleBannerChg"> { prefix }</span>
          </Col>
          {
            (id != '') ?
              <Col className="singleBannerCol" size={{ normal: 24, small: 24, medium: 16, large: 16 }}>
                <ButtonGroup style={{float:'right',margin:'16px auto',position:'relative',zIndex:1}}>
                  <Button type="default" size="medium" onClick={this.getClusterDetail.bind(this,id)}>集群详情</Button>
                  <Button type="default" size="medium" onClick={this.getIndise.bind(this,id)}>索引管理</Button>
                  <Button type="default" size="medium" onClick={this.getSearchData.bind(this,id)}>数据查询</Button>
                  <Button type="default" size="medium" onClick={this.getParticiple.bind(this,id)}>分词管理</Button>
                  <Button type="default" size="medium" onClick={this.getMonitor.bind(this,id)}>集群监控管理</Button>
                </ButtonGroup>
              </Col>
            : ''
          }
        </Row>
      </div>
    );
  }

}

export default SingleBanner;