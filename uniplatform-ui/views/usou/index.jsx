import React, { Component } from 'react';
import { COMM_HeadBanner,Footer } from '../../components/uniplatform-ui';
import { Container,Button,Icon,Tabs,Tab } from 'epm-ui';

const page = {
    title: '首页',
    css: [
        'css/index.min.css',
        'css/singleBanner.min.css'
    ]
};

class IndexApp extends Component {
    constructor(props){
        super(props);
        this.clusterList =  this.clusterList.bind(this);
        this.demoList=this.demoList.bind(this);
    }
    clusterList(){
        window.open('/usou/clusterList','_self');
    }

    demoList(){
        window.open('/usou/search');
    }

    render() {
        return (
            <Container type="fluid">
              <COMM_HeadBanner prefix="搜索引擎"/>

              <div className="inner-content">
                <div className="inner-contentUp">
                  <div className="inner-contentUp-top">
                    <div className="inner-contentUp-topL">
                        <h2><img src="image/Shape94.png" />&nbsp;&nbsp;全文检索</h2>
                        <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;搜USou是分布式的全文搜索引擎，兼有搜索引擎和NoSQL数据库功能，其设计目标是实现海量（PB级）数据的高性能存储和检索。它本质上属于大数据技术架构的NoSQL层，主要用于增强查询能力，实现大数据存储、管理和检索的高度一体化，并提供企业级应用的可靠性、安全性和易用性。</p>
                        <Button className="btn" onClick={this.clusterList.bind(this)} style={{backgroundColor:'#02aab5',color:'white'}}>
                            进入管理
                        </Button>
                        <Button className="btn" onClick={this.demoList.bind(this)} style={{backgroundColor:'#02aab5',color:'white'}}>
                            搜索首页
                        </Button>
                    </div>
                    <div className="inner-contentUp-topR" style={{textAlign:'center'}}>
                        <img className="inner-contentUp-topL-normal" src='image/zu1.png' />
                    </div>
                  </div>
                  <div className="inner-contentUp-bottom">
                      <Tabs >
                          <Tab title="优势" style={{backgroundColor:'red'}}>
                              <div className="inner-contentUp-topL">
                                  <h3>标签一</h3>
                                  <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.</p>
                              </div>
                              <div className="inner-contentUp-topR" >
                                  <img className="inner-contentUp-topL-normal" src='image/quan2.png' />
                              </div>
                          </Tab>
                          <Tab title="功能" style={{backgroundColor:'red'}}>
                              <div className="inner-contentUp-topL">
                                  <h3>标签二</h3>
                                  <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.</p>
                              </div>
                              <div className="inner-contentUp-topR" >
                                  <img className="inner-contentUp-topL-normal" src='image/quan2.png' />
                              </div>
                          </Tab>
                          <Tab title="场景" style={{backgroundColor:'red'}}>
                              <div className="inner-contentUp-topL">
                                  <h3>标签三</h3>
                                  <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.</p>
                              </div>
                              <div className="inner-contentUp-topR" >
                                  <img className="inner-contentUp-topL-normal" src='image/quan2.png' />
                              </div>
                          </Tab>
                      </Tabs>
                  </div>
                </div>
              </div>
              <Footer/>
            </Container>
        );

    }
}

IndexApp.UIPage = page;
export default IndexApp;
