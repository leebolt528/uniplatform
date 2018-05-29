import React, { Component } from 'react';
import { COMM_HeadBanner, COMM_PublicDec, Footer } from '../../components/uniplatform-ui';
import { Container } from 'epm-ui';

const page = {
  title: '首页',
  css: [
    'css/index.min.css'
  ],
  js: [
    'js/index.min.js'
  ]
};

class IndexApp extends Component {

  render() {
    return (
      <Container type="fluid">
        <COMM_HeadBanner prefix="非结构化统一平台"/>

        <div className="inner-content">
          <div className="inner-contentUp">
            <div className="inner-contentUp-top">
              <img className="inner-contentUp-top-com" src="image/com.png" />
              <div className="inner-contentUp-topL">
                <img className="inner-contentUp-topL-normal" src="//gw.alicdn.com/tps/TB191S9OVXXXXb2XFXXXXXXXXXX-132-134.png" />
                <img className="inner-contentUp-topL-onmouseover" src="//gw.alicdn.com/tps/TB191S9OVXXXXb2XFXXXXXXXXXX-132-134.png" />
              </div>
              <div className="inner-contentUp-topR">
                <h2>非结构化统一平台</h2>
                <p>非结构化平台以非结构化数据为处理对象，提供数据采集、处理、存储、分析挖掘和数据应用全方位全业务服务。</p>
              </div>
            </div>
            <div className="inner-contentUp-bottom">
              <h2>解决方案</h2>
              <span className="inner-contentUp-bottom-subtitle">开发者工具、建站、API、企业应用，支撑云端应用服务</span>
              <div className="inner-contentUp-bottom-dec">
                <div className="inner-contentUp-bottom-decLink">
                  <a href="">
                    <img alt="icon" src="image/yuqing.png"/>
                    <h4>互联网舆情</h4>
                  </a>
                  <span>MQ推出微消息服务，支持MQTT协议，实现消息的双向通信。</span>
                </div>
                <div className="inner-contentUp-bottom-decLink">
                  <a href="">
                    <img alt="icon" src="image/hulian.png"/>
                    <h4>运营商</h4>
                  </a>
                  <span>MQ推出微消息服务，支持MQTT协议，实现消息的双向通信。</span>
                </div>
                <div className="inner-contentUp-bottom-decLink">
                  <a href="">
                    <img alt="icon" src="image/kefu.png"/>
                    <h4>客服</h4>
                  </a>
                  <span>MQ推出微消息服务，支持MQTT协议，实现消息的双向通信。</span>
                </div>
                <div className="inner-contentUp-bottom-decLink">
                  <a href="">
                    <img alt="icon" src="image/zhengfu.png"/>
                    <h4>政府</h4>
                  </a>
                  <span>MQ推出微消息服务，支持MQTT协议，实现消息的双向通信。</span>
                </div>
              </div>
            </div>
          </div>
          <COMM_PublicDec />
        </div>
        <Footer/>
      </Container>
    );

  }
}

IndexApp.UIPage = page;
export default IndexApp;
